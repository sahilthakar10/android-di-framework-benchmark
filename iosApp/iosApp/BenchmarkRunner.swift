import Foundation
import BenchmarkMetro
import BenchmarkKoin

// MARK: - Models for layered benchmark results

struct LayerResult: Identifiable {
    let id = UUID()
    let name: String
    let count: Int
    let description: String
    let totalTimeMs: Double
    let items: [(name: String, timeMs: Double)]

    var top5Slowest: [(name: String, timeMs: Double)] {
        Array(items.sorted { $0.timeMs > $1.timeMs }.prefix(5))
    }
}

struct FullFrameworkResult {
    let frameworkName: String
    let grandTotalMs: Double
    let totalClassCount: Int
    let layers: [LayerResult]
}

// MARK: - Old models (kept for backward compatibility)

struct InjectionEntry: Identifiable {
    let id = UUID()
    let className: String
    let metroMicros: Double
    let koinMicros: Double
    var winner: String { metroMicros <= koinMicros ? "Metro" : "Koin" }
}

struct FrameworkResult {
    let name: String
    let initTimeMs: Double
    let totalWarmAvgMicros: Double
}

struct ComparisonResult {
    let metro: FrameworkResult
    let koin: FrameworkResult
    let coldEntries: [InjectionEntry]
    let warmEntries: [InjectionEntry]
}

// MARK: - Full Benchmark Runner Model

class FullBenchmarkRunnerModel: ObservableObject {
    @Published var metroResult: FullFrameworkResult?
    @Published var koinResult: FullFrameworkResult?
    @Published var isRunningMetro = false
    @Published var isRunningKoin = false

    func runMetro() {
        guard !isRunningMetro else { return }
        isRunningMetro = true
        metroResult = nil

        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            let raw = MetroBenchmarkRunnerKt.runFullBenchmark()
            let result = Self.convertMetroResult(raw: raw)

            DispatchQueue.main.async {
                self?.metroResult = result
                self?.isRunningMetro = false
            }
        }
    }

    func runKoin() {
        guard !isRunningKoin else { return }
        isRunningKoin = true
        koinResult = nil

        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            let raw = KoinBenchmarkRunnerKt.runFullBenchmark()
            let result = Self.convertKoinResult(raw: raw)

            DispatchQueue.main.async {
                self?.koinResult = result
                self?.isRunningKoin = false
            }
        }
    }

    private static func convertMetroResult(raw: BenchmarkMetro.FullBenchmarkResult) -> FullFrameworkResult {
        return convertAnyResult(rawLayers: raw.layers, frameworkName: "Metro")
    }

    private static func convertKoinResult(raw: BenchmarkKoin.FullBenchmarkResult) -> FullFrameworkResult {
        return convertAnyResult(rawLayers: raw.layers, frameworkName: "Koin")
    }

    private static func convertAnyResult(rawLayers: [Any], frameworkName: String) -> FullFrameworkResult {
        var layers: [LayerResult] = []
        var grandTotal: Double = 0
        var totalClasses: Int = 0

        for rawLayer in rawLayers {
            let layer = rawLayer as AnyObject
            let name = layer.value(forKey: "name") as! String
            let count = (layer.value(forKey: "count") as! NSNumber).intValue
            let desc = layer.value(forKey: "description_") as! String
            let rawItems = layer.value(forKey: "items") as! [AnyObject]

            var items: [(name: String, timeMs: Double)] = []
            var layerTotal: Double = 0

            for pair in rawItems {
                let itemName = pair.value(forKey: "first") as! String
                let nanos = (pair.value(forKey: "second") as! NSNumber).int64Value
                let ms = Double(nanos) / 1_000_000.0
                items.append((name: itemName, timeMs: ms))
                layerTotal += ms
            }

            grandTotal += layerTotal
            totalClasses += count

            layers.append(LayerResult(
                name: name,
                count: count,
                description: desc,
                totalTimeMs: layerTotal,
                items: items
            ))
        }

        return FullFrameworkResult(
            frameworkName: frameworkName,
            grandTotalMs: grandTotal,
            totalClassCount: totalClasses,
            layers: layers
        )
    }
}

// MARK: - Legacy Runner (kept for backward compatibility)

class BenchmarkRunnerModel: ObservableObject {
    @Published var result: ComparisonResult?
    @Published var isRunning = false
    @Published var statusText = "Ready"

    func run(iterations: Int32 = 100) {
        guard !isRunning else { return }
        isRunning = true
        statusText = "Running benchmarks..."

        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            let metroRaw = MetroBenchmarkRunnerKt.runMetroBenchmark(warmIterations: iterations)
            let koinRaw = KoinBenchmarkRunnerKt.runKoinBenchmark(warmIterations: iterations)

            let coldEntries = Self.buildEntries(
                metroDict: metroRaw.coldInjectionNanos as! [String: Any],
                koinDict: koinRaw.coldInjectionNanos as! [String: Any]
            )
            let warmEntries = Self.buildEntries(
                metroDict: metroRaw.warmInjectionAvgNanos as! [String: Any],
                koinDict: koinRaw.warmInjectionAvgNanos as! [String: Any]
            )

            let classCount = max(Double(metroRaw.warmInjectionAvgNanos.count), 1.0)
            let iterCount = max(Double(iterations), 1.0)

            let metro = FrameworkResult(
                name: "Metro",
                initTimeMs: Double(metroRaw.initTimeNanos) / 1_000_000.0,
                totalWarmAvgMicros: Double(metroRaw.totalWarmNanos) / 1000.0 / (classCount * iterCount)
            )
            let koin = FrameworkResult(
                name: "Koin",
                initTimeMs: Double(koinRaw.initTimeNanos) / 1_000_000.0,
                totalWarmAvgMicros: Double(koinRaw.totalWarmNanos) / 1000.0 / (classCount * iterCount)
            )

            let comparison = ComparisonResult(
                metro: metro, koin: koin,
                coldEntries: coldEntries, warmEntries: warmEntries
            )

            DispatchQueue.main.async {
                self?.result = comparison
                self?.isRunning = false
                self?.statusText = "Complete"
            }
        }
    }

    private static func buildEntries(metroDict: [String: Any], koinDict: [String: Any]) -> [InjectionEntry] {
        var entries: [InjectionEntry] = []
        for (key, value) in metroDict {
            let mNanos = Self.toInt64(value)
            let kNanos = Self.toInt64(koinDict[key] as Any)
            entries.append(InjectionEntry(
                className: key,
                metroMicros: Double(mNanos) / 1000.0,
                koinMicros: Double(kNanos) / 1000.0
            ))
        }
        return entries.sorted { $0.className < $1.className }
    }

    private static func toInt64(_ value: Any) -> Int64 {
        if let n = value as? NSNumber { return n.int64Value }
        if let n = value as? Int64 { return n }
        if let n = value as? Int { return Int64(n) }
        return 0
    }
}

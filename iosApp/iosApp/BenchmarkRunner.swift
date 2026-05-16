import Foundation
import BenchmarkMetro
import BenchmarkKoin

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

class BenchmarkRunnerModel: ObservableObject {
    @Published var result: ComparisonResult?
    @Published var isRunning = false
    @Published var statusText = "Ready"

    func run(iterations: Int32 = 100) {
        guard !isRunning else { return }
        isRunning = true
        statusText = "Running benchmarks..."

        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            // Metro benchmark
            let metroRaw = MetroBenchmarkRunnerKt.runMetroBenchmark(warmIterations: iterations)

            // Koin benchmark
            let koinRaw = KoinBenchmarkRunnerKt.runKoinBenchmark(warmIterations: iterations)

            // Build cold entries
            let coldEntries = Self.buildEntries(
                metroDict: metroRaw.coldInjectionNanos as! [String: Any],
                koinDict: koinRaw.coldInjectionNanos as! [String: Any]
            )

            // Build warm entries
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

            Self.printConsole(comparison, iterations: Int(iterations))

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

    // Note: iOS process-level memory measurement (mach_task_basic_info.resident_size)
    // is unreliable for DI-level granularity because Kotlin/Native GC, system page
    // reclamation, and SwiftUI rendering all affect resident_size between measurements.
    // Use Xcode Instruments → Allocations for accurate iOS memory profiling.

    private static func printConsole(_ r: ComparisonResult, iterations: Int) {
        NSLog("")
        NSLog("════════════════════════════════════════════════════")
        NSLog("  iOS RUNTIME BENCHMARK: Metro vs Koin")
        NSLog("  ~350 classes, ~285 bindings, \(iterations) warm iterations")
        NSLog("════════════════════════════════════════════════════")
        NSLog("")
        NSLog("  Init Time:")
        NSLog("    Metro: \(String(format: "%.2f", r.metro.initTimeMs))ms")
        NSLog("    Koin:  \(String(format: "%.2f", r.koin.initTimeMs))ms")
        NSLog("")
        NSLog("  Cold Injection:")
        for e in r.coldEntries {
            let mMark = e.winner == "Metro" ? " <" : ""
            let kMark = e.winner == "Koin" ? " <" : ""
            NSLog("    \(e.className.padding(toLength: 22, withPad: " ", startingAt: 0)) Metro:\(String(format: "%8.0f", e.metroMicros))us\(mMark)  Koin:\(String(format: "%8.0f", e.koinMicros))us\(kMark)")
        }
        NSLog("")
        NSLog("  Warm Injection (avg of \(iterations)):")
        for e in r.warmEntries {
            let mMark = e.winner == "Metro" ? " <" : ""
            let kMark = e.winner == "Koin" ? " <" : ""
            NSLog("    \(e.className.padding(toLength: 22, withPad: " ", startingAt: 0)) Metro:\(String(format: "%8.0f", e.metroMicros))us\(mMark)  Koin:\(String(format: "%8.0f", e.koinMicros))us\(kMark)")
        }
        NSLog("")
        NSLog("  Avg per injection: Metro \(String(format: "%.0f", r.metro.totalWarmAvgMicros))us | Koin \(String(format: "%.0f", r.koin.totalWarmAvgMicros))us")
        NSLog("  Note: Memory profiling on iOS requires Xcode Instruments (resident_size is unreliable for DI-level measurement)")
        NSLog("════════════════════════════════════════════════════")
    }
}

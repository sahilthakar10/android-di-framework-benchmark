import SwiftUI

private let metroBlue = Color(red: 0.13, green: 0.59, blue: 0.95)
private let koinOrange = Color(red: 1.0, green: 0.60, blue: 0.0)
private let winnerGreen = Color(red: 0.30, green: 0.69, blue: 0.31)

struct ContentView: View {
    @StateObject private var runner = BenchmarkRunnerModel()

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {

                // ── Header ──
                VStack(spacing: 6) {
                    Text("Metro vs Koin")
                        .font(.title)
                        .fontWeight(.bold)
                    Text("iOS Runtime Benchmark")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                    Text("~350 classes  |  ~285 bindings  |  100 iterations")
                        .font(.caption2)
                        .foregroundColor(.secondary)
                }
                .padding(.top, 24)

                // ── Run Button ──
                Button(action: { runner.run(iterations: 100) }) {
                    HStack(spacing: 8) {
                        if runner.isRunning {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                .scaleEffect(0.8)
                            Text("Running...")
                        } else {
                            Image(systemName: "play.fill")
                            Text("Run Benchmark")
                        }
                    }
                    .font(.headline)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(runner.isRunning ? Color.gray : metroBlue)
                    .foregroundColor(.white)
                    .cornerRadius(12)
                }
                .disabled(runner.isRunning)
                .padding(.horizontal)

                if let r = runner.result {
                    // ── Init Card ──
                    BenchmarkCard {
                        CardTitle("Container Initialization")
                        CardSubtitle("One-time cost at app startup")

                        HStack(spacing: 10) {
                            ValueBox(
                                label: "Metro",
                                value: fmtMs(r.metro.initTimeMs),
                                color: metroBlue,
                                isWinner: r.metro.initTimeMs <= r.koin.initTimeMs
                            )
                            ValueBox(
                                label: "Koin",
                                value: fmtMs(r.koin.initTimeMs),
                                color: koinOrange,
                                isWinner: r.koin.initTimeMs < r.metro.initTimeMs
                            )
                        }

                        let ratio = r.koin.initTimeMs / max(r.metro.initTimeMs, 0.001)
                        Insight("Metro initializes \(String(format: "%.0f", ratio))x faster")
                    }

                    // ── Cold Injection Card ──
                    BenchmarkCard {
                        CardTitle("Cold Injection")
                        CardSubtitle("First access — constructing object + all transitive dependencies")

                        let coldMetroAvg = r.coldEntries.map(\.metroMicros).reduce(0, +) / max(Double(r.coldEntries.count), 1)
                        let coldKoinAvg = r.coldEntries.map(\.koinMicros).reduce(0, +) / max(Double(r.coldEntries.count), 1)

                        HStack(spacing: 10) {
                            ValueBox(label: "Metro", value: fmtUs(coldMetroAvg), sublabel: "avg cold", color: metroBlue, isWinner: coldMetroAvg <= coldKoinAvg)
                            ValueBox(label: "Koin", value: fmtUs(coldKoinAvg), sublabel: "avg cold", color: koinOrange, isWinner: coldKoinAvg < coldMetroAvg)
                        }

                        ComparisonTable(entries: r.coldEntries)

                        let metroWins = r.coldEntries.filter { $0.metroMicros <= $0.koinMicros }.count
                        Insight("Metro wins \(metroWins)/\(r.coldEntries.count) classes on first access")
                    }

                    // ── Warm Injection Card ──
                    BenchmarkCard {
                        CardTitle("Warm Injection")
                        CardSubtitle("Avg of 100 repeated accesses — singletons cached, factories re-created")

                        HStack(spacing: 10) {
                            ValueBox(label: "Metro", value: fmtUs(r.metro.totalWarmAvgMicros), sublabel: "avg/injection", color: metroBlue, isWinner: r.metro.totalWarmAvgMicros <= r.koin.totalWarmAvgMicros)
                            ValueBox(label: "Koin", value: fmtUs(r.koin.totalWarmAvgMicros), sublabel: "avg/injection", color: koinOrange, isWinner: r.koin.totalWarmAvgMicros < r.metro.totalWarmAvgMicros)
                        }

                        ComparisonTable(entries: r.warmEntries)

                        let metroWins = r.warmEntries.filter { $0.metroMicros <= $0.koinMicros }.count
                        Insight("Metro wins \(metroWins)/\(r.warmEntries.count) classes on repeated access")
                    }

                    // ── Verdict Card ──
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Verdict")
                            .font(.headline)
                            .foregroundColor(.white)

                        let initRatio = r.koin.initTimeMs / max(r.metro.initTimeMs, 0.001)
                        VerdictRow(category: "Init", detail: "Metro — \(String(format: "%.0f", initRatio))x faster")

                        let coldWins = r.coldEntries.filter { $0.metroMicros <= $0.koinMicros }.count
                        VerdictRow(category: "Cold", detail: "Metro — wins \(coldWins)/\(r.coldEntries.count) classes")

                        VerdictRow(category: "Warm", detail: "Metro — \(fmtUs(r.metro.totalWarmAvgMicros)) vs \(fmtUs(r.koin.totalWarmAvgMicros)) avg")

                        Text("Memory profiling requires Xcode Instruments")
                            .font(.caption2)
                            .foregroundColor(.white.opacity(0.7))
                    }
                    .padding(16)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(red: 0.1, green: 0.14, blue: 0.49))
                    .cornerRadius(12)
                    .padding(.horizontal)

                } else if !runner.isRunning {
                    // ── Empty State ──
                    VStack(spacing: 12) {
                        Image(systemName: "gauge.with.dots.needle.33percent")
                            .font(.system(size: 44))
                            .foregroundColor(.secondary)
                        Text("Tap Run to start")
                            .foregroundColor(.secondary)
                    }
                    .padding(.top, 60)
                }

                Spacer(minLength: 40)
            }
        }
    }
}

// MARK: - Card Container

struct BenchmarkCard<Content: View>: View {
    @ViewBuilder let content: Content

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            content
        }
        .padding(14)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(.systemGray6))
        .cornerRadius(12)
        .padding(.horizontal)
    }
}

// MARK: - Value Box

struct ValueBox: View {
    let label: String
    var value: String
    var sublabel: String? = nil
    let color: Color
    let isWinner: Bool

    var body: some View {
        VStack(spacing: 3) {
            Text(label)
                .font(.caption)
                .fontWeight(.semibold)
                .foregroundColor(color)
            if let sub = sublabel {
                Text(sub)
                    .font(.system(size: 9))
                    .foregroundColor(.secondary)
            }
            Text(value)
                .font(.title3)
                .fontWeight(.bold)
            if isWinner {
                Text("WINNER")
                    .font(.system(size: 8, weight: .heavy))
                    .foregroundColor(winnerGreen)
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 12)
        .background(isWinner ? winnerGreen.opacity(0.08) : Color(.systemGray5))
        .cornerRadius(10)
    }
}

// MARK: - Comparison Table

struct ComparisonTable: View {
    let entries: [InjectionEntry]

    var body: some View {
        VStack(spacing: 0) {
            // Header
            HStack {
                Text("Class")
                    .frame(maxWidth: .infinity, alignment: .leading)
                Text("Metro")
                    .foregroundColor(metroBlue)
                    .frame(width: 60, alignment: .trailing)
                Text("Koin")
                    .foregroundColor(koinOrange)
                    .frame(width: 60, alignment: .trailing)
            }
            .font(.system(size: 11, weight: .bold))
            .padding(.bottom, 4)

            Rectangle()
                .fill(Color(.separator))
                .frame(height: 0.5)

            // Rows
            ForEach(entries) { e in
                HStack {
                    Text(e.className)
                        .lineLimit(1)
                        .frame(maxWidth: .infinity, alignment: .leading)
                    Text(fmtUs(e.metroMicros))
                        .fontWeight(e.winner == "Metro" ? .bold : .regular)
                        .foregroundColor(e.winner == "Metro" ? winnerGreen : .primary)
                        .frame(width: 60, alignment: .trailing)
                    Text(fmtUs(e.koinMicros))
                        .fontWeight(e.winner == "Koin" ? .bold : .regular)
                        .foregroundColor(e.winner == "Koin" ? winnerGreen : .primary)
                        .frame(width: 60, alignment: .trailing)
                }
                .font(.system(size: 11))
                .padding(.vertical, 3)
            }
        }
    }
}

// MARK: - Small Components

struct CardTitle: View {
    let text: String
    init(_ text: String) { self.text = text }
    var body: some View {
        Text(text).font(.headline)
    }
}

struct CardSubtitle: View {
    let text: String
    init(_ text: String) { self.text = text }
    var body: some View {
        Text(text).font(.caption2).foregroundColor(.secondary)
    }
}

struct Insight: View {
    let text: String
    init(_ text: String) { self.text = text }
    var body: some View {
        Text(text)
            .font(.caption)
            .foregroundColor(winnerGreen)
            .fontWeight(.medium)
    }
}

struct VerdictRow: View {
    let category: String
    let detail: String

    var body: some View {
        HStack {
            Text(category)
                .font(.subheadline)
                .foregroundColor(.white.opacity(0.7))
                .frame(width: 50, alignment: .leading)
            Text(detail)
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundColor(.white)
        }
    }
}

// MARK: - Formatters

private func fmtMs(_ ms: Double) -> String {
    if ms < 0.01 { return "<0.01ms" }
    if ms >= 1 { return String(format: "%.1fms", ms) }
    return String(format: "%.2fms", ms)
}

private func fmtUs(_ us: Double) -> String {
    if us >= 1000 { return String(format: "%.1fms", us / 1000) }
    if us < 1 { return "<1us" }
    return String(format: "%.0fus", us)
}

#Preview {
    ContentView()
}

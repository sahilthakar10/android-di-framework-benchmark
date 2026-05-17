import SwiftUI

struct BenchmarkDetailView: View {
    let frameworkName: String
    let accentColor: Color
    @Binding var result: FullFrameworkResult?
    @Binding var isRunning: Bool
    let onRun: () -> Void

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                // Run button
                Button(action: onRun) {
                    HStack(spacing: 8) {
                        if isRunning {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                .scaleEffect(0.8)
                            Text("Running...")
                        } else {
                            Image(systemName: "play.fill")
                            Text("Run \(frameworkName) Benchmark")
                        }
                    }
                    .font(.headline)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(isRunning ? Color.gray : accentColor)
                    .foregroundColor(.white)
                    .cornerRadius(12)
                }
                .disabled(isRunning)
                .padding(.horizontal)

                if let r = result {
                    // Grand Total Card
                    GrandTotalCard(result: r, accentColor: accentColor)

                    // Layer Cards
                    ForEach(r.layers) { layer in
                        LayerCard(layer: layer, accentColor: accentColor)
                    }
                } else if !isRunning {
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
            .padding(.top, 8)
        }
        .navigationTitle("\(frameworkName) Benchmark")
        .navigationBarTitleDisplayMode(.inline)
    }
}

// MARK: - Grand Total Card

struct GrandTotalCard: View {
    let result: FullFrameworkResult
    let accentColor: Color

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text("Grand Total")
                    .font(.headline)
                Spacer()
                Text("\(result.totalClassCount) classes")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }

            Text(formatMs(result.grandTotalMs))
                .font(.system(size: 32, weight: .bold, design: .monospaced))
                .foregroundColor(accentColor)

            Text("Total time to resolve all dependencies")
                .font(.caption2)
                .foregroundColor(.secondary)
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(accentColor.opacity(0.08))
        .cornerRadius(12)
        .padding(.horizontal)
    }
}

// MARK: - Layer Card

struct LayerCard: View {
    let layer: LayerResult
    let accentColor: Color
    @State private var isExpanded = false

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            // Header
            Button(action: { withAnimation { isExpanded.toggle() } }) {
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        HStack(spacing: 6) {
                            Text(layer.name)
                                .font(.subheadline)
                                .fontWeight(.semibold)
                                .foregroundColor(.primary)
                            Text("(\(layer.count))")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        Text(layer.description)
                            .font(.caption2)
                            .foregroundColor(.secondary)
                    }
                    Spacer()
                    VStack(alignment: .trailing, spacing: 2) {
                        Text(formatMs(layer.totalTimeMs))
                            .font(.subheadline)
                            .fontWeight(.bold)
                            .foregroundColor(timeColor(layer.totalTimeMs))
                    }
                    Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
            .buttonStyle(PlainButtonStyle())

            // Top 5 slowest (always shown)
            VStack(spacing: 0) {
                ForEach(Array(layer.top5Slowest.enumerated()), id: \.offset) { idx, item in
                    HStack {
                        Text("\(idx + 1).")
                            .font(.system(size: 10, design: .monospaced))
                            .foregroundColor(.secondary)
                            .frame(width: 16, alignment: .trailing)
                        Text(item.name)
                            .font(.system(size: 11))
                            .lineLimit(1)
                        Spacer()
                        Text(formatMs(item.timeMs))
                            .font(.system(size: 11, weight: .medium, design: .monospaced))
                            .foregroundColor(timeColor(item.timeMs))
                    }
                    .padding(.vertical, 2)
                }
            }

            // Expanded: show all items
            if isExpanded && layer.items.count > 5 {
                Divider()
                Text("All items:")
                    .font(.caption2)
                    .foregroundColor(.secondary)

                VStack(spacing: 0) {
                    ForEach(Array(layer.items.sorted { $0.timeMs > $1.timeMs }.enumerated()), id: \.offset) { idx, item in
                        HStack {
                            Text("\(idx + 1).")
                                .font(.system(size: 10, design: .monospaced))
                                .foregroundColor(.secondary)
                                .frame(width: 20, alignment: .trailing)
                            Text(item.name)
                                .font(.system(size: 10))
                                .lineLimit(1)
                            Spacer()
                            Text(formatMs(item.timeMs))
                                .font(.system(size: 10, weight: .medium, design: .monospaced))
                                .foregroundColor(timeColor(item.timeMs))
                        }
                        .padding(.vertical, 1)
                    }
                }
            }
        }
        .padding(14)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(.systemGray6))
        .cornerRadius(12)
        .padding(.horizontal)
    }
}

// MARK: - Helpers

private func formatMs(_ ms: Double) -> String {
    if ms < 0.001 { return "<0.001ms" }
    if ms >= 100 { return String(format: "%.0fms", ms) }
    if ms >= 10 { return String(format: "%.1fms", ms) }
    if ms >= 1 { return String(format: "%.2fms", ms) }
    return String(format: "%.3fms", ms)
}

private func timeColor(_ ms: Double) -> Color {
    if ms > 5.0 { return .red }
    if ms > 1.0 { return .orange }
    return .primary
}

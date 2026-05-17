import SwiftUI

private let metroPurple = Color(red: 0.40, green: 0.23, blue: 0.72)
private let koinOrange = Color(red: 1.0, green: 0.60, blue: 0.0)

struct ContentView: View {
    @StateObject private var runner = FullBenchmarkRunnerModel()

    var body: some View {
        NavigationStack {
            VStack(spacing: 32) {
                Spacer()

                VStack(spacing: 8) {
                    Text("DI Benchmark")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                    Text("iOS - Layer-by-Layer Analysis")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                    Text("~350 classes | ~285 bindings")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }

                VStack(spacing: 16) {
                    // Metro button
                    NavigationLink(destination: BenchmarkDetailView(
                        frameworkName: "Metro",
                        accentColor: metroPurple,
                        result: $runner.metroResult,
                        isRunning: $runner.isRunningMetro,
                        onRun: { runner.runMetro() }
                    )) {
                        HStack(spacing: 10) {
                            Image(systemName: "bolt.fill")
                            Text("Metro Full Benchmark")
                        }
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(metroPurple)
                        .foregroundColor(.white)
                        .cornerRadius(14)
                    }

                    // Koin button
                    NavigationLink(destination: BenchmarkDetailView(
                        frameworkName: "Koin",
                        accentColor: koinOrange,
                        result: $runner.koinResult,
                        isRunning: $runner.isRunningKoin,
                        onRun: { runner.runKoin() }
                    )) {
                        HStack(spacing: 10) {
                            Image(systemName: "bolt.fill")
                            Text("Koin Full Benchmark")
                        }
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(koinOrange)
                        .foregroundColor(.white)
                        .cornerRadius(14)
                    }
                }
                .padding(.horizontal, 24)

                Spacer()
                Spacer()
            }
            .navigationBarHidden(true)
        }
    }
}

#Preview {
    ContentView()
}

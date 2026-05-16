# iOS DI Benchmark App

## Prerequisites

1. **Install Xcode** from the App Store
2. Set Xcode as active developer tools:
   ```bash
   sudo xcode-select -s /Applications/Xcode.app/Contents/Developer
   ```

## Setup

### 1. Build the XCFrameworks

```bash
cd /Users/sahilthakar/AndroidStudioProjects/BenchMarking

# Build both frameworks
./gradlew :benchmark-kmp-metro:assembleBenchmarkMetroReleaseXCFramework
./gradlew :benchmark-kmp-koin:assembleBenchmarkKoinReleaseXCFramework
```

Frameworks will be at:
- `benchmark-kmp-metro/build/XCFrameworks/release/BenchmarkMetro.xcframework`
- `benchmark-kmp-koin/build/XCFrameworks/release/BenchmarkKoin.xcframework`

### 2. Create Xcode Project

1. Open Xcode → File → New → Project → iOS App
2. Product Name: `iosApp`, Team: your team, Language: Swift, Interface: SwiftUI
3. Save in the `iosApp/` directory
4. Copy `iosAppApp.swift`, `ContentView.swift`, `BenchmarkRunner.swift` into the project
5. Drag both `.xcframework` bundles into "Frameworks, Libraries, and Embedded Content"
6. Build & Run on iOS Simulator

### 3. Run Compile-Time Benchmark (no Xcode needed)

```bash
./benchmark-compile-time-ios.sh 5
```

## What It Measures

- **Container Init**: `createGraph()` vs `startKoin()`
- **Cold Injection**: First access for 10 key classes
- **Warm Injection**: 100 iterations per class averaged
- **Memory**: Resident memory delta via `mach_task_basic_info`

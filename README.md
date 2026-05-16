# Android & iOS DI Framework Benchmark: Hilt vs Metro vs Koin

> **The most comprehensive, open-source dependency injection performance benchmark for Android and iOS (Kotlin Multiplatform).** Compare Hilt, Metro, and Koin across compile-time, runtime injection speed, cold start, warm access, and memory — using a real-world e-commerce app with ~390 classes and ~290 bindings.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue.svg)](https://kotlinlang.org)
[![AGP](https://img.shields.io/badge/AGP-9.2.0-green.svg)](https://developer.android.com/build)
[![KMP](https://img.shields.io/badge/Kotlin_Multiplatform-iOS_%2B_Android-purple.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/badge/License-Educational-orange.svg)](#license)

---

## Why This Benchmark Exists

Choosing between Hilt, Metro, and Koin is one of the most debated decisions in Android/KMP development. Most comparisons rely on opinions or toy examples. **This project provides hard numbers** from an identical, production-scale e-commerce app — same 390 classes, same 14 business domains, same dependency chains — implemented in all three frameworks.

We also benchmark **Kotlin Multiplatform (iOS)** to answer: *"How do Metro and Koin perform on Kotlin/Native where there's no JIT, no ConcurrentHashMap, and a stop-the-world GC?"*

---

## Frameworks Compared

| Framework | Type | How It Works | Version |
|-----------|------|-------------|---------|
| [**Hilt**](https://dagger.dev/hilt/) (Google/Dagger) | Compile-time | KSP annotation processing → generates Java factories & components | 2.59.2 |
| [**Metro**](https://github.com/ZacSweers/metro) (Zac Sweers) | Compile-time | Kotlin compiler plugin (FIR + IR) → zero source generation | 0.6.5 |
| [**Koin**](https://insert-koin.io/) | Runtime | DSL-based service locator → HashMap lookups at runtime | 4.1.1 |

---

## Key Results at a Glance

### Android (Pixel 9 Pro Emulator, API 35)

| Metric | Hilt | Metro | Koin | Winner |
|--------|------|-------|------|--------|
| **Compile Time** (5 runs avg) | 3,943ms | 2,182ms | 1,673ms | Koin (57.6% faster than Hilt) |
| **Generated Code** | 291 files / 488KB | 0 files | 0 files | Metro & Koin |
| **Container Init** | 0.02ms | 0.14ms | 1.45ms | Hilt |
| **Warm Injection** (avg) | 4us | 2us | 64us | Metro |
| **Memory Overhead** | 96KB | 241KB | 1,537KB | Hilt |

### iOS (iPhone 16 Pro Simulator, iOS 26.5)

| Metric | Metro | Koin | Winner |
|--------|-------|------|--------|
| **Compile Time** (3 runs avg) | 1,871ms | 1,260ms | Koin (32.7% faster) |
| **Container Init** | 0.01ms | 0.17ms | Metro (17x faster) |
| **Cold Injection** (avg) | 2us | 13us | Metro (6.5x faster) |
| **Warm Injection** (avg) | <1us | 6us | Metro (>6x faster) |

### Cross-Platform Takeaway

Metro's advantage over Koin is **consistent across both platforms**: ~6x faster on Android, >6x faster on iOS. The ratio holds because the architectural difference is the same — Metro uses direct field reads (volatile/atomic), Koin uses HashMap lookups + lambda invocations.

---

## Test Application Architecture

All frameworks benchmark against an **identical e-commerce application** following Clean Architecture:

```
ShopApp (per framework)
├── Core Layer (53 singletons)
│   ├── Network: HttpClient, AuthInterceptor, GraphQLClient, WebSocket...
│   ├── Auth: AuthManager, TokenStorage, SessionManager, OAuth, 2FA...
│   ├── Analytics: AnalyticsTracker, CrashReporter, A/B Testing...
│   ├── Storage: DatabaseManager, CacheManager, SecureStorage...
│   ├── Config: FeatureFlags, RemoteConfig, ThemeManager...
│   ├── Logging, Image, Notification, Location services
│
├── Data Layer (14 domains × 4 classes = 56 singletons)
│   ├── Repository + RemoteDataSource + LocalDataSource + Mapper
│   └── Domains: Product, User, Cart, Order, Payment, Chat, Search,
│       Review, Category, Address, Wishlist, Promotion, Shipping, Feed
│
├── Domain Layer (14 domains × 10 use cases = 140 factories)
│   └── Get, Create, Update, Delete, Search, Validate, Refresh, Count, Filter
│
└── Feature Layer (13 features, ~50 ViewModels/Presenters)
    └── Home, Search, ProductDetail, Cart, Checkout, Profile, Orders,
        Settings, Chat, Notifications, Onboarding, Reviews, Wishlist
```

**Total: ~390 classes | ~290 DI bindings | 120+ source files per framework**

---

## Project Structure

```
BenchMarking/
│
├── app/                              # Android benchmark app (Hilt + Metro + Koin UI)
│
├── benchmark-hilt-large/             # Hilt e-commerce app (~350 classes)
├── benchmark-metro-large/            # Metro e-commerce app (~350 classes)
├── benchmark-koin-large/             # Koin e-commerce app (~350 classes)
│
├── benchmark-kmp-common/             # KMP shared: platform utilities (expect/actual)
├── benchmark-kmp-metro/              # KMP Metro → iOS XCFramework
├── benchmark-kmp-koin/               # KMP Koin → iOS XCFramework
├── iosApp/                           # SwiftUI iOS benchmark app
│
├── di-benchmark-runtime/             # SDK: timing, memory tracking, auto-detection
├── di-benchmark-compiler/            # Gradle plugin: compile-time measurement
├── di-benchmark-ui/                  # Compose dashboard: charts, export
├── di-benchmark-export/              # JSON/CSV export, CI reporter
├── di-benchmark-annotations/         # @BenchmarkInjection, FrameworkType, ScopeType
│
├── sample-hilt-module/               # Small Hilt sample
├── sample-metro-module/              # Small Metro sample
│
├── benchmark-compile-time.sh         # Android compile-time benchmark (Hilt vs Metro vs Koin)
├── benchmark-compile-time-ios.sh     # iOS compile-time benchmark (Metro vs Koin)
├── generate_rfc.py                   # RFC document generator (python-docx)
└── RFC_DI_Framework_Benchmark.docx   # Full RFC with 12 sections of analysis
```

---

## What Is Measured

### Compile-Time Benchmarks
- Clean build time for each framework (5 runs averaged, Gradle daemon warm)
- Generated code analysis: file count, line count, total size
- Pipeline comparison: KSP (Hilt) vs compiler plugin (Metro) vs no codegen (Koin)
- Both Android (`compileDebugKotlin`) and iOS (`compileKotlinIosSimulatorArm64`)

### Runtime Benchmarks
- **Container initialization** — `createGraph()` vs `startKoin()` vs Hilt component access
- **Cold injection** — first-time resolution for 10 key classes (includes full transitive chain)
- **Warm injection** — 100 repeated resolutions per class, averaged
- **Memory overhead** — heap + native memory delta (Android only; iOS requires Instruments)

### Benchmark Methodology

Following [kotlinx-benchmark](https://github.com/Kotlin/kotlinx-benchmark) and JMH best practices:

- **Warmup phase**: 5 iterations of all 10 target classes (excluded from measurement)
- **Blackhole**: Prevents dead code elimination by LLVM (iOS) and JIT (Android)
- **Single lifecycle**: Container created once per framework, matching real-world app behavior
- **Nanosecond timing**: `System.nanoTime()` (Android) / `TimeSource.Monotonic` (iOS)
- **GC isolation**: `System.gc()` (Android) between framework runs; separate warmup/measurement phases

---

## Running the Benchmarks

### Prerequisites
- Android Studio with AGP 9.2.0+
- Kotlin 2.2.10
- JDK 21
- Android emulator or device (API 24+)
- Xcode 16+ (for iOS benchmarks)

### Android — Runtime Benchmark (In-App UI)
```bash
./gradlew installDebug
# Open app → Tap "Run Metro vs Koin Runtime Benchmark"
# View results in-app with Init, Cold, Warm, Memory cards
```

### Android — Runtime Benchmark (Headless / CI)
```bash
adb shell am start -n com.codeint.benchmarking/.RuntimeBenchmarkActivity --ei iterations 100
adb logcat -s RuntimeBenchmark:I
```

### Android — Compile-Time Benchmark
```bash
./benchmark-compile-time.sh 5    # 5 runs for Hilt vs Metro vs Koin
```

### iOS — Compile-Time Benchmark (no Xcode required)
```bash
./benchmark-compile-time-ios.sh 3    # 3 runs for Metro vs Koin
```

### iOS — Runtime Benchmark
```bash
# Build XCFrameworks
./gradlew :benchmark-kmp-metro:assembleBenchmarkMetroReleaseXCFramework
./gradlew :benchmark-kmp-koin:assembleBenchmarkKoinReleaseXCFramework

# Build and run iOS app
cd iosApp
xcodegen generate
xcodebuild -project DIBenchmarkiOS.xcodeproj -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 16 Pro' build
# Install and tap "Run Benchmark" in the app
```

### Generate RFC Document
```bash
pip3 install python-docx
python3 generate_rfc.py
open RFC_DI_Framework_Benchmark.docx
```

---

## Best Practices Applied

Each framework is configured using its **production-recommended best practices** to ensure a fair comparison:

### Hilt
- `@Inject` constructor injection — no manual wiring
- `@Singleton` on all core services, repositories, data sources (109 singletons)
- KSP (not KAPT) for annotation processing
- `@Module + @Provides` only where constructor injection isn't possible

### Metro
- `@DependencyGraph(AppScope::class)` — scoped graph for singleton caching
- `@SingleIn(AppScope::class)` on all core/data layer classes (105 singletons)
- Zero source generation — code injected directly into Kotlin IR
- Same `@Inject` pattern as Hilt for familiarity

### Koin
- `single { }` for all core services, repos, data sources (90 singletons)
- `createdAtStart = true` on 19 critical infrastructure singletons
- `factory { }` for all use cases, ViewModels, presenters (181 factories)
- Modular organization: 24 Koin modules (9 core + 1 data + 1 domain + 13 feature)

---

## How Metro Achieves Superior Performance

Metro is a Kotlin compiler plugin that hooks into the K2 compiler pipeline:

1. **FIR phase (Frontend)**: Reads `@DependencyGraph`, `@Inject`, validates the full dependency graph, reports errors as compiler diagnostics
2. **IR phase (Backend)**: Generates provider factories, graph implementation class, DoubleCheck singletons — all as IR nodes, zero source files
3. **Result**: One compilation pass, no file I/O, no separate javac invocation

**vs Hilt**: Hilt uses KSP → generates 291 Java files → requires separate javac pass = 2 compiler invocations + file I/O

**vs Koin**: Koin does zero compile-time work (fastest build) but pays at runtime — every `get<T>()` performs HashMap lookup + type resolution + lambda invocation

At runtime, Metro's singletons are volatile field reads (~0.2us). Koin's `single{}` cache still requires `HashMap.get()` + type check (~3-6us on iOS, ~3-64us on Android).

---

## RFC Document

The project includes a comprehensive **RFC document** (`RFC_DI_Framework_Benchmark.docx`) with 12 sections:

1. Executive Summary
2. Framework Overview (Hilt, Metro, Koin)
3. Test Application Architecture
4. Compile-Time Benchmark (Android: Hilt vs Metro vs Koin)
5. Runtime Benchmark (Android: all three frameworks)
6. Best Practices Applied
7. Compile-Time Safety Comparison (including Koin Compiler Plugin K2)
8. Runtime DI — Advantages & Limitations
9. iOS & KMP — Cross-Platform Performance Analysis (with measured results)
10. Deep Dive — How Metro Achieves Superior Performance (K2 pipeline, FIR/IR, DoubleCheck)
11. Summary & Recommendation
12. References & Further Reading (docs, blog posts, books, conference talks)

---

## When to Choose Each Framework

| Scenario | Recommendation | Why |
|----------|---------------|-----|
| Large Android app, Google ecosystem | **Hilt** | Official Google support, battle-tested, extensive docs |
| Performance-critical, KMP needed | **Metro** | Best runtime performance, compile-time safety, zero codegen |
| Small-medium app, rapid development | **Koin** | Simplest setup, fastest builds, lowest learning curve |
| Team migrating from Dagger to KMP | **Metro** | Familiar `@Inject`/`@Provides` patterns |
| Dynamic feature modules, runtime flexibility | **Koin** | `loadKoinModules()` / `unloadKoinModules()` at runtime |

---

## Built With

- **Kotlin** 2.2.10 — Language & KMP
- **Jetpack Compose** — Material Design 3 (Android UI)
- **SwiftUI** — iOS benchmark app
- **AGP** 9.2.0 — Android Gradle Plugin
- **Gradle** 9.4.1 — Build system
- **python-docx** — RFC document generation
- **kotlinx-serialization** — JSON/CSV export

---

## Further Reading

- [Metro Documentation](https://zacsweers.github.io/metro/) — Zac Sweers
- [Metro Migration at Square](https://engineering.block.xyz/blog/metro-migration-at-square-android) — 7,000 modules, 4,800 CI hours saved/week
- [Hilt Documentation](https://dagger.dev/hilt/) — Google
- [Koin Documentation](https://insert-koin.io/) — Insert-Koin
- [Kotlin/Native Memory Management](https://kotlinlang.org/docs/native-memory-manager.html) — JetBrains
- *Dependency Injection Principles, Practices, and Patterns* — Mark Seemann & Steven van Deursen (Manning, 2019)
- *Kotlin in Action, 2nd Edition* — Manning, 2024
- *Kotlin Design Patterns and Best Practices, 3rd Edition* — Packt, 2024

---

## License

This project is provided for **educational and benchmarking purposes**. The e-commerce app code is synthetic — designed to create realistic dependency graphs, not to function as an actual store.

---

<p align="center">
  <i>Built with Kotlin, measured with precision, documented with honesty.</i>
</p>

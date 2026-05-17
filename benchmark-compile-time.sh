#!/bin/bash
# ============================================================================
# DI Compile-Time Benchmark: Hilt vs Metro vs Koin
#
# Realistic e-commerce app structure (ShopApp):
#   Core:     Network, Auth, Analytics, Storage, Config, Logging, Image, Notifications, Location
#   Data:     14 domains x (RemoteDataSource + LocalDataSource + Mapper + Repository)
#   Domain:   14 domains x (10 UseCases + DomainModels)
#   Features: Home, Search, ProductDetail, Cart, Checkout, Profile, Orders,
#             Settings, Chat, Notifications, Onboarding, Reviews, Wishlist
#
#   ~285 bindings | ~350 total classes | 120+ files per module
#
# Hilt  = KSP annotation processing + Dagger code generation
# Metro = Kotlin compiler plugin (FIR + IR, zero source generation)
# Koin  = No code generation (pure runtime DSL, only Kotlin compilation)
# ============================================================================

set -e

RUNS=${1:-3}
echo "================================================================"
echo "  DI COMPILE-TIME BENCHMARK"
echo "  Hilt (KSP) vs Metro (Compiler Plugin) vs Koin (No codegen)"
echo "  E-Commerce App: ~350 classes, ~285 bindings"
echo "  Clean builds x $RUNS runs"
echo "================================================================"
echo ""

declare -a HILT_TIMES
declare -a METRO_TIMES
declare -a KOIN_TIMES

for run in $(seq 1 $RUNS); do
    echo "--- Run $run of $RUNS ---"

    # Clean all three
    ./gradlew :benchmark-hilt-large:clean :benchmark-metro-large:clean :benchmark-koin-large:clean --quiet 2>/dev/null

    # Benchmark HILT (KSP + Kotlin + Java — 3 compilation steps)
    echo -n "  Hilt  (KSP + Dagger):    "
    HILT_START=$(python3 -c 'import time; print(int(time.time() * 1000))')
    ./gradlew :benchmark-hilt-large:kspDebugKotlin :benchmark-hilt-large:compileDebugKotlin :benchmark-hilt-large:compileDebugJavaWithJavac --quiet 2>/dev/null
    HILT_END=$(python3 -c 'import time; print(int(time.time() * 1000))')
    HILT_MS=$((HILT_END - HILT_START))
    HILT_TIMES+=($HILT_MS)
    printf "%'dms\n" $HILT_MS

    # Clean all
    ./gradlew :benchmark-hilt-large:clean :benchmark-metro-large:clean :benchmark-koin-large:clean --quiet 2>/dev/null

    # Benchmark METRO (KMP module — use compileAndroidMain)
    echo -n "  Metro (Compiler Plugin): "
    METRO_START=$(python3 -c 'import time; print(int(time.time() * 1000))')
    ./gradlew :benchmark-metro-large:compileAndroidMain --quiet 2>/dev/null
    METRO_END=$(python3 -c 'import time; print(int(time.time() * 1000))')
    METRO_MS=$((METRO_END - METRO_START))
    METRO_TIMES+=($METRO_MS)
    printf "%'dms\n" $METRO_MS

    # Clean all
    ./gradlew :benchmark-hilt-large:clean :benchmark-metro-large:clean :benchmark-koin-large:clean --quiet 2>/dev/null

    # Benchmark KOIN (KMP module — use compileAndroidMain)
    echo -n "  Koin  (No codegen):      "
    KOIN_START=$(python3 -c 'import time; print(int(time.time() * 1000))')
    ./gradlew :benchmark-koin-large:compileAndroidMain --quiet 2>/dev/null
    KOIN_END=$(python3 -c 'import time; print(int(time.time() * 1000))')
    KOIN_MS=$((KOIN_END - KOIN_START))
    KOIN_TIMES+=($KOIN_MS)
    printf "%'dms\n" $KOIN_MS

    echo ""
done

# Calculate stats
HILT_SUM=0; HILT_MIN=999999; HILT_MAX=0
for t in "${HILT_TIMES[@]}"; do
    HILT_SUM=$((HILT_SUM + t))
    [ $t -lt $HILT_MIN ] && HILT_MIN=$t
    [ $t -gt $HILT_MAX ] && HILT_MAX=$t
done
HILT_AVG=$((HILT_SUM / RUNS))

METRO_SUM=0; METRO_MIN=999999; METRO_MAX=0
for t in "${METRO_TIMES[@]}"; do
    METRO_SUM=$((METRO_SUM + t))
    [ $t -lt $METRO_MIN ] && METRO_MIN=$t
    [ $t -gt $METRO_MAX ] && METRO_MAX=$t
done
METRO_AVG=$((METRO_SUM / RUNS))

KOIN_SUM=0; KOIN_MIN=999999; KOIN_MAX=0
for t in "${KOIN_TIMES[@]}"; do
    KOIN_SUM=$((KOIN_SUM + t))
    [ $t -lt $KOIN_MIN ] && KOIN_MIN=$t
    [ $t -gt $KOIN_MAX ] && KOIN_MAX=$t
done
KOIN_AVG=$((KOIN_SUM / RUNS))

# Find the fastest
FASTEST_AVG=$HILT_AVG
FASTEST_NAME="Hilt"
[ $METRO_AVG -lt $FASTEST_AVG ] && FASTEST_AVG=$METRO_AVG && FASTEST_NAME="Metro"
[ $KOIN_AVG -lt $FASTEST_AVG ] && FASTEST_AVG=$KOIN_AVG && FASTEST_NAME="Koin"

pct_vs() {
    local fast=$1 slow=$2
    python3 -c "print(f'{(1 - $fast/$slow) * 100:.1f}' if $slow > 0 else '0')"
}

echo "================================================================"
echo "  RESULTS (averaged over $RUNS clean builds)"
echo "================================================================"
echo ""
echo "  App Structure:"
echo "    Layers:   Core -> Data -> Domain -> Feature"
echo "    Domains:  Product, User, Cart, Order, Payment, Chat,"
echo "              Search, Review, Category, Address, Wishlist,"
echo "              Promotion, Shipping, Feed"
echo "    Features: 13 feature modules"
echo "    Classes:  ~350 per framework"
echo "    Bindings: ~285 per framework"
echo ""
echo "  ┌──────────────────────┬──────────────┬──────────────┬──────────────┐"
echo "  │ Metric               │ Hilt (KSP)   │ Metro        │ Koin         │"
echo "  ├──────────────────────┼──────────────┼──────────────┼──────────────┤"
printf "  │ Average              │ %7dms     │ %7dms     │ %7dms     │\n" $HILT_AVG $METRO_AVG $KOIN_AVG
printf "  │ Min                  │ %7dms     │ %7dms     │ %7dms     │\n" $HILT_MIN $METRO_MIN $KOIN_MIN
printf "  │ Max                  │ %7dms     │ %7dms     │ %7dms     │\n" $HILT_MAX $METRO_MAX $KOIN_MAX
echo "  └──────────────────────┴──────────────┴──────────────┴──────────────┘"
echo ""

echo "  Comparison vs slowest:"
# Find slowest
SLOWEST_AVG=$HILT_AVG
[ $METRO_AVG -gt $SLOWEST_AVG ] && SLOWEST_AVG=$METRO_AVG
[ $KOIN_AVG -gt $SLOWEST_AVG ] && SLOWEST_AVG=$KOIN_AVG

HILT_PCT=$(pct_vs $HILT_AVG $SLOWEST_AVG)
METRO_PCT=$(pct_vs $METRO_AVG $SLOWEST_AVG)
KOIN_PCT=$(pct_vs $KOIN_AVG $SLOWEST_AVG)

echo "    Hilt:  ${HILT_PCT}% faster than slowest"
echo "    Metro: ${METRO_PCT}% faster than slowest"
echo "    Koin:  ${KOIN_PCT}% faster than slowest"
echo ""

echo "  Head-to-head:"
echo "    Metro vs Hilt: Metro is $(pct_vs $METRO_AVG $HILT_AVG)% faster"
echo "    Koin vs Hilt:  Koin is $(pct_vs $KOIN_AVG $HILT_AVG)% faster"
echo "    Metro vs Koin: $(if [ $METRO_AVG -lt $KOIN_AVG ]; then echo "Metro is $(pct_vs $METRO_AVG $KOIN_AVG)% faster"; else echo "Koin is $(pct_vs $KOIN_AVG $METRO_AVG)% faster"; fi)"
echo ""

echo "  Individual runs:"
echo "    Hilt:  ${HILT_TIMES[*]} ms"
echo "    Metro: ${METRO_TIMES[*]} ms"
echo "    Koin:  ${KOIN_TIMES[*]} ms"
echo ""

# Generated code analysis
echo "================================================================"
echo "  GENERATED CODE ANALYSIS"
echo "================================================================"

./gradlew :benchmark-hilt-large:clean :benchmark-metro-large:clean :benchmark-koin-large:clean --quiet 2>/dev/null
./gradlew :benchmark-hilt-large:kspDebugKotlin :benchmark-hilt-large:compileDebugKotlin :benchmark-metro-large:compileAndroidMain :benchmark-koin-large:compileAndroidMain --quiet 2>/dev/null

analyze_gen() {
    local dir=$1
    if [ -d "$dir" ]; then
        local files=$(find "$dir" \( -name "*.java" -o -name "*.kt" \) 2>/dev/null | wc -l | tr -d ' ')
        local lines=$(find "$dir" \( -name "*.java" -o -name "*.kt" \) -exec cat {} + 2>/dev/null | wc -l | tr -d ' ')
        local size=$(find "$dir" \( -name "*.java" -o -name "*.kt" \) -exec cat {} + 2>/dev/null | wc -c | tr -d ' ')
        echo "$files files, $lines lines, $((size / 1024))KB"
    else
        echo "0 files (no code generation)"
    fi
}

echo ""
echo "  Hilt:  $(analyze_gen "benchmark-hilt-large/build/generated")"
echo "  Metro: $(analyze_gen "benchmark-metro-large/build/generated")"
echo "  Koin:  $(analyze_gen "benchmark-koin-large/build/generated")"
echo ""
echo "================================================================"
echo "  HOW EACH FRAMEWORK COMPILES"
echo "================================================================"
echo ""
echo "  Hilt (KSP + Dagger):"
echo "    1. KSP scans @Inject, @Module, @Provides annotations"
echo "    2. Generates Java source files (factories, components)"
echo "    3. Kotlin compiler compiles your code"
echo "    4. Java compiler compiles generated code"
echo "    -> Two compilation passes + code generation overhead"
echo ""
echo "  Metro (Kotlin Compiler Plugin):"
echo "    1. FIR phase: Analyzes @DependencyGraph, @Inject"
echo "    2. IR phase: Generates implementations directly in IR"
echo "    3. Single compilation pass, no source file generation"
echo "    -> One pass, code injected directly into compiler output"
echo ""
echo "  Koin (Pure Runtime):"
echo "    1. Kotlin compiler compiles your code + module DSL"
echo "    2. No annotation processing, no code generation"
echo "    3. All DI resolution happens at runtime via reflection"
echo "    -> Fastest compile, but pays the cost at app startup"
echo ""
echo "================================================================"

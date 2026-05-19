#!/bin/bash
# ============================================================================
# DI Compile-Time Benchmark: Hilt vs Metro vs Koin vs kotlin-inject-anvil
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
# Hilt             = KSP annotation processing + Dagger code generation
# Metro            = Kotlin compiler plugin (FIR + IR, zero source generation)
# Koin             = No code generation (pure runtime DSL, only Kotlin compilation)
# kotlin-inject-anvil = KSP annotation processing + component merging
# ============================================================================

set -e

RUNS=${1:-3}
MODULES="hilt-large metro-large koin-large kinject-large"

echo "================================================================"
echo "  DI COMPILE-TIME BENCHMARK"
echo "  Hilt vs Metro vs Koin vs kotlin-inject-anvil"
echo "  E-Commerce App: ~350 classes, ~285 bindings"
echo "  Clean builds x $RUNS runs"
echo "================================================================"
echo ""

declare -a HILT_TIMES
declare -a METRO_TIMES
declare -a KOIN_TIMES
declare -a KINJECT_TIMES

clean_all() {
    ./gradlew :benchmark-hilt-large:clean :benchmark-metro-large:clean :benchmark-koin-large:clean :benchmark-kinject-large:clean --quiet 2>/dev/null
}

measure() {
    local START=$(python3 -c 'import time; print(int(time.time() * 1000))')
    ./gradlew $@ --quiet 2>/dev/null
    local END=$(python3 -c 'import time; print(int(time.time() * 1000))')
    echo $((END - START))
}

for run in $(seq 1 $RUNS); do
    echo "--- Run $run of $RUNS ---"

    clean_all

    echo -n "  Hilt             (KSP + Dagger):  "
    HILT_MS=$(measure :benchmark-hilt-large:kspDebugKotlin :benchmark-hilt-large:compileDebugKotlin :benchmark-hilt-large:compileDebugJavaWithJavac)
    HILT_TIMES+=($HILT_MS)
    printf "%'dms\n" $HILT_MS

    clean_all

    echo -n "  Metro            (Compiler Plugin): "
    METRO_MS=$(measure :benchmark-metro-large:compileAndroidMain)
    METRO_TIMES+=($METRO_MS)
    printf "%'dms\n" $METRO_MS

    clean_all

    echo -n "  Koin             (No codegen):      "
    KOIN_MS=$(measure :benchmark-koin-large:compileAndroidMain)
    KOIN_TIMES+=($KOIN_MS)
    printf "%'dms\n" $KOIN_MS

    clean_all

    echo -n "  kotlin-inject-anvil (KSP):          "
    KINJECT_MS=$(measure :benchmark-kinject-large:kspDebugKotlin :benchmark-kinject-large:compileDebugKotlin)
    KINJECT_TIMES+=($KINJECT_MS)
    printf "%'dms\n" $KINJECT_MS

    echo ""
done

# Calculate stats
calc_stats() {
    local -n TIMES=$1
    local -n SUM_VAR=$2
    local -n MIN_VAR=$3
    local -n MAX_VAR=$4
    local -n AVG_VAR=$5
    SUM_VAR=0; MIN_VAR=999999; MAX_VAR=0
    for t in "${TIMES[@]}"; do
        SUM_VAR=$((SUM_VAR + t))
        [ $t -lt $MIN_VAR ] && MIN_VAR=$t
        [ $t -gt $MAX_VAR ] && MAX_VAR=$t
    done
    AVG_VAR=$((SUM_VAR / RUNS))
}

calc_stats HILT_TIMES HILT_SUM HILT_MIN HILT_MAX HILT_AVG
calc_stats METRO_TIMES METRO_SUM METRO_MIN METRO_MAX METRO_AVG
calc_stats KOIN_TIMES KOIN_SUM KOIN_MIN KOIN_MAX KOIN_AVG
calc_stats KINJECT_TIMES KINJECT_SUM KINJECT_MIN KINJECT_MAX KINJECT_AVG

pct_vs() {
    local fast=$1 slow=$2
    python3 -c "print(f'{(1 - $fast/$slow) * 100:.1f}' if $slow > 0 else '0')"
}

echo "================================================================"
echo "  RESULTS (averaged over $RUNS clean builds)"
echo "================================================================"
echo ""
echo "  ┌────────────────────────┬──────────────┬──────────────┬──────────────┬──────────────┐"
echo "  │ Metric                 │ Hilt (KSP)   │ Metro        │ Koin         │ k-inject-anvil│"
echo "  ├────────────────────────┼──────────────┼──────────────┼──────────────┼──────────────┤"
printf "  │ Average                │ %7dms     │ %7dms     │ %7dms     │ %7dms     │\n" $HILT_AVG $METRO_AVG $KOIN_AVG $KINJECT_AVG
printf "  │ Min                    │ %7dms     │ %7dms     │ %7dms     │ %7dms     │\n" $HILT_MIN $METRO_MIN $KOIN_MIN $KINJECT_MIN
printf "  │ Max                    │ %7dms     │ %7dms     │ %7dms     │ %7dms     │\n" $HILT_MAX $METRO_MAX $KOIN_MAX $KINJECT_MAX
echo "  └────────────────────────┴──────────────┴──────────────┴──────────────┴──────────────┘"
echo ""

echo "  Head-to-head vs Hilt (slowest):"
echo "    Metro:              $(pct_vs $METRO_AVG $HILT_AVG)% faster"
echo "    Koin:               $(pct_vs $KOIN_AVG $HILT_AVG)% faster"
echo "    kotlin-inject-anvil: $(pct_vs $KINJECT_AVG $HILT_AVG)% faster"
echo ""

echo "  Individual runs:"
echo "    Hilt:              ${HILT_TIMES[*]} ms"
echo "    Metro:             ${METRO_TIMES[*]} ms"
echo "    Koin:              ${KOIN_TIMES[*]} ms"
echo "    kotlin-inject-anvil: ${KINJECT_TIMES[*]} ms"
echo ""

# Generated code analysis
echo "================================================================"
echo "  GENERATED CODE ANALYSIS"
echo "================================================================"

clean_all
./gradlew :benchmark-hilt-large:kspDebugKotlin :benchmark-hilt-large:compileDebugKotlin :benchmark-metro-large:compileAndroidMain :benchmark-koin-large:compileAndroidMain :benchmark-kinject-large:kspDebugKotlin :benchmark-kinject-large:compileDebugKotlin --quiet 2>/dev/null

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
echo "  Hilt:              $(analyze_gen "benchmark-hilt-large/build/generated")"
echo "  Metro:             $(analyze_gen "benchmark-metro-large/build/generated")"
echo "  Koin:              $(analyze_gen "benchmark-koin-large/build/generated")"
echo "  kotlin-inject-anvil: $(analyze_gen "benchmark-kinject-large/build/generated")"
echo ""
echo "================================================================"
echo "  HOW EACH FRAMEWORK COMPILES"
echo "================================================================"
echo ""
echo "  Hilt (KSP + Dagger):"
echo "    1. KSP scans @Inject, @Module, @Provides"
echo "    2. Generates Java source files (factories, components)"
echo "    3. Kotlin compiler compiles your code"
echo "    4. Java compiler compiles generated code"
echo "    -> Two compilation passes + code generation"
echo ""
echo "  Metro (Kotlin Compiler Plugin):"
echo "    1. FIR phase: Analyzes @DependencyGraph, @Inject"
echo "    2. IR phase: Generates implementations directly in IR"
echo "    -> One pass, zero generated files"
echo ""
echo "  Koin (Pure Runtime):"
echo "    1. Kotlin compiler compiles your code + module DSL"
echo "    2. No annotation processing, no code generation"
echo "    -> Fastest compile, pays cost at runtime"
echo ""
echo "  kotlin-inject-anvil (KSP):"
echo "    1. KSP scans @Inject, @Component, @ContributesBinding"
echo "    2. Generates Kotlin source files (component impl, factories)"
echo "    3. Kotlin compiler compiles your code + generated code"
echo "    -> Two passes, but less codegen than Hilt (no Java)"
echo ""
echo "================================================================"

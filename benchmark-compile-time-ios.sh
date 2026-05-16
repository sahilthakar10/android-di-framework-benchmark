#!/bin/bash
# ============================================================================
# iOS DI Compile-Time Benchmark: Metro vs Koin (Kotlin/Native)
#
# Same realistic e-commerce app as Android benchmark:
#   ~350 classes, ~285 bindings, 14 domains, 13 features
#
# Metro = Kotlin compiler plugin (FIR + IR) → LLVM → ARM64
# Koin  = No codegen (pure DSL) → LLVM → ARM64
# ============================================================================

set -e

RUNS=${1:-3}
TARGET="IosSimulatorArm64"

echo "================================================================"
echo "  iOS COMPILE-TIME BENCHMARK"
echo "  Metro (Compiler Plugin) vs Koin (No codegen)"
echo "  Target: $TARGET (Kotlin/Native → LLVM → ARM64)"
echo "  E-Commerce App: ~350 classes, ~285 bindings"
echo "  Clean builds x $RUNS runs"
echo "================================================================"
echo ""

declare -a METRO_TIMES
declare -a KOIN_TIMES

for run in $(seq 1 $RUNS); do
    echo "--- Run $run of $RUNS ---"

    # Clean both
    ./gradlew :benchmark-kmp-metro:clean :benchmark-kmp-koin:clean --quiet 2>/dev/null

    # Benchmark METRO
    echo -n "  Metro (Compiler Plugin): "
    METRO_START=$(python3 -c 'import time; print(int(time.time() * 1000))')
    ./gradlew :benchmark-kmp-metro:compileKotlin${TARGET} --quiet 2>/dev/null
    METRO_END=$(python3 -c 'import time; print(int(time.time() * 1000))')
    METRO_MS=$((METRO_END - METRO_START))
    METRO_TIMES+=($METRO_MS)
    printf "%'dms\n" $METRO_MS

    # Clean both
    ./gradlew :benchmark-kmp-metro:clean :benchmark-kmp-koin:clean --quiet 2>/dev/null

    # Benchmark KOIN
    echo -n "  Koin  (No codegen):      "
    KOIN_START=$(python3 -c 'import time; print(int(time.time() * 1000))')
    ./gradlew :benchmark-kmp-koin:compileKotlin${TARGET} --quiet 2>/dev/null
    KOIN_END=$(python3 -c 'import time; print(int(time.time() * 1000))')
    KOIN_MS=$((KOIN_END - KOIN_START))
    KOIN_TIMES+=($KOIN_MS)
    printf "%'dms\n" $KOIN_MS

    echo ""
done

# Calculate stats
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

pct_vs() {
    local fast=$1 slow=$2
    python3 -c "print(f'{(1 - $fast/$slow) * 100:.1f}' if $slow > 0 else '0')"
}

echo "================================================================"
echo "  iOS RESULTS (averaged over $RUNS clean builds)"
echo "================================================================"
echo ""
echo "  Target: Kotlin/Native → LLVM → $TARGET"
echo "  Classes: ~350 per framework | Bindings: ~285 per framework"
echo ""
echo "  ┌──────────────────────┬──────────────┬──────────────┐"
echo "  │ Metric               │ Metro        │ Koin         │"
echo "  ├──────────────────────┼──────────────┼──────────────┤"
printf "  │ Average              │ %7dms     │ %7dms     │\n" $METRO_AVG $KOIN_AVG
printf "  │ Min                  │ %7dms     │ %7dms     │\n" $METRO_MIN $KOIN_MIN
printf "  │ Max                  │ %7dms     │ %7dms     │\n" $METRO_MAX $KOIN_MAX
echo "  └──────────────────────┴──────────────┴──────────────┘"
echo ""

echo "  Head-to-head:"
if [ $METRO_AVG -lt $KOIN_AVG ]; then
    echo "    Metro is $(pct_vs $METRO_AVG $KOIN_AVG)% faster than Koin"
else
    echo "    Koin is $(pct_vs $KOIN_AVG $METRO_AVG)% faster than Metro"
fi

echo ""
echo "  Individual runs:"
echo "    Metro: ${METRO_TIMES[*]} ms"
echo "    Koin:  ${KOIN_TIMES[*]} ms"

echo ""
echo "================================================================"
echo "  iOS vs Android Compile-Time Comparison"
echo "================================================================"
echo ""
echo "  Note: iOS compilation is slower than Android because:"
echo "    1. Kotlin/Native must produce LLVM IR (more complex than JVM bytecode)"
echo "    2. LLVM optimization passes run (function inlining, devirtualization)"
echo "    3. No incremental compilation support for K/N (always full rebuild)"
echo "    4. K/N compiler downloads and caches platform libraries on first run"
echo ""
echo "  This overhead is identical for Metro and Koin — the comparison"
echo "  between them is what matters, not absolute times vs Android."
echo "================================================================"

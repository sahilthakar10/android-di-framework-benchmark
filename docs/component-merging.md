# Component Merging in DI Frameworks

## What is Component Merging?

Component merging allows DI bindings to be declared in any Gradle module and automatically discovered and merged into the final DI graph — without a central file listing them all.

## How Hilt Does It

Each module declares its bindings with `@InstallIn`. Hilt's processor scans all modules at compile time and merges them into the target component.

```kotlin
// :feature-auth module
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides @Singleton
    fun provideAuthManager(): AuthManager = RealAuthManager()
}

// :feature-payment module (knows nothing about :feature-auth)
@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {
    @Provides @Singleton
    fun providePaymentService(): PaymentService = RealPaymentService()
}

// :app module — no manual wiring needed
@HiltAndroidApp
class MyApp : Application()
// Hilt auto-discovers AuthModule + PaymentModule and merges them
```

Adding a new feature module requires zero changes to the app module.

## How kotlin-inject-anvil Does It

Same concept, different annotations. `@ContributesBinding` declares where a binding belongs. `@MergeComponent` collects all contributions.

```kotlin
// :feature-auth module
@ContributesBinding(AppScope::class)
@Inject @SingleIn(AppScope::class)
class RealAuthManager : AuthManager

// :feature-payment module
@ContributesBinding(AppScope::class)
@Inject @SingleIn(AppScope::class)
class RealPaymentService : PaymentService

// :app module
@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class AppComponent
// RealAuthManager and RealPaymentService are auto-discovered
```

## How Metro Does It

Metro does NOT support component merging. Every binding must be explicitly declared in the `@DependencyGraph`.

```kotlin
@DependencyGraph(AppScope::class)
interface AppGraph {
    @Provides fun bindAuth(impl: RealAuthManager): AuthManager = impl
    @Provides fun bindPayment(impl: RealPaymentService): PaymentService = impl
    @Provides fun bindCart(impl: RealCartRepository): CartRepository = impl
    // ... every binding must be listed here
}
```

For multi-module setups, Metro uses `@Includes` to compose graphs:

```kotlin
@DependencyGraph(AppScope::class)
interface AppGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Includes authGraph: AuthGraph,
            @Includes paymentGraph: PaymentGraph
        ): AppGraph
    }
}
```

Each sub-graph is still explicitly included — no auto-discovery.

## Comparison

| Aspect | Hilt | kotlin-inject-anvil | Metro |
|--------|------|---------------------|-------|
| Auto-discovery | `@InstallIn` | `@ContributesTo` / `@ContributesBinding` | Not supported |
| Adding a new module | Add `@InstallIn` in new module | Add `@ContributesBinding` in new module | Update central `@DependencyGraph` |
| Central "god file" | No | No | Yes |
| Merge conflicts at scale | Rare | Rare | Common |

## When It Matters

**Small-to-medium apps (< 15 modules):** Component merging is nice-to-have but not critical. Metro's explicit graph is readable and manageable.

**Large apps (50+ modules, multiple teams):** Component merging becomes important. Without it, the central graph file becomes a bottleneck — every team touches it, merge conflicts increase, and team autonomy decreases.

## Why Metro Chose Not to Support It

Metro is a compiler plugin (FIR + IR). Component merging requires cross-module discovery at compile time — scanning annotations across all dependencies. KSP processors (Hilt, kotlin-inject-anvil) do this naturally because they run as annotation processors with access to all symbols. A compiler plugin operates within a single module's compilation, making cross-module discovery architecturally harder.

Metro's trade-off: faster compilation and zero generated files, at the cost of explicit wiring.

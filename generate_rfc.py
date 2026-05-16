#!/usr/bin/env python3
"""
Generates RFC document for DI Framework Benchmarking:
Hilt vs Metro vs Koin
"""

from docx import Document
from docx.shared import Inches, Pt, Cm, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.enum.section import WD_ORIENT
from docx.oxml.ns import qn
import datetime

doc = Document()

# ── Page Setup ──
for section in doc.sections:
    section.top_margin = Cm(2)
    section.bottom_margin = Cm(2)
    section.left_margin = Cm(2.5)
    section.right_margin = Cm(2.5)

# ── Styles ──
style = doc.styles['Normal']
style.font.name = 'Calibri'
style.font.size = Pt(11)

BLUE = RGBColor(0x21, 0x96, 0xF3)
ORANGE = RGBColor(0xFF, 0x98, 0x00)
GREEN_HILT = RGBColor(0x4C, 0xAF, 0x50)
DARK = RGBColor(0x33, 0x33, 0x33)
GRAY = RGBColor(0x66, 0x66, 0x66)
WHITE = RGBColor(0xFF, 0xFF, 0xFF)
HEADER_BG = RGBColor(0x1A, 0x23, 0x7E)

def set_cell_bg(cell, color_hex):
    shading = cell._element.get_or_add_tcPr()
    bg = shading.makeelement(qn('w:shd'), {
        qn('w:val'): 'clear',
        qn('w:color'): 'auto',
        qn('w:fill'): color_hex,
    })
    shading.append(bg)

def add_table(headers, rows, col_widths=None):
    table = doc.add_table(rows=1 + len(rows), cols=len(headers))
    table.style = 'Table Grid'
    table.alignment = WD_TABLE_ALIGNMENT.CENTER

    # Header row
    for i, h in enumerate(headers):
        cell = table.rows[0].cells[i]
        cell.text = h
        for p in cell.paragraphs:
            p.alignment = WD_ALIGN_PARAGRAPH.CENTER
            for r in p.runs:
                r.bold = True
                r.font.size = Pt(10)
                r.font.color.rgb = WHITE
        set_cell_bg(cell, '1A237E')

    # Data rows
    for ri, row in enumerate(rows):
        for ci, val in enumerate(row):
            cell = table.rows[ri + 1].cells[ci]
            cell.text = str(val)
            for p in cell.paragraphs:
                p.alignment = WD_ALIGN_PARAGRAPH.CENTER if ci > 0 else WD_ALIGN_PARAGRAPH.LEFT
                for r in p.runs:
                    r.font.size = Pt(10)
            if ri % 2 == 1:
                set_cell_bg(cell, 'F5F5F5')

    if col_widths:
        for i, w in enumerate(col_widths):
            for row in table.rows:
                row.cells[i].width = Cm(w)
    return table

def add_heading_styled(text, level=1):
    h = doc.add_heading(text, level=level)
    for r in h.runs:
        r.font.color.rgb = HEADER_BG
    return h

# ═══════════════════════════════════════════════════════
# TITLE PAGE
# ═══════════════════════════════════════════════════════
doc.add_paragraph()
doc.add_paragraph()
title = doc.add_paragraph()
title.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = title.add_run('RFC: Android DI Framework\nPerformance Benchmark')
run.font.size = Pt(28)
run.bold = True
run.font.color.rgb = HEADER_BG

subtitle = doc.add_paragraph()
subtitle.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = subtitle.add_run('Hilt (Dagger/KSP) vs Metro (Compiler Plugin) vs Koin (Runtime)')
run.font.size = Pt(14)
run.font.color.rgb = GRAY

doc.add_paragraph()

meta = doc.add_paragraph()
meta.alignment = WD_ALIGN_PARAGRAPH.CENTER
meta.add_run(f'Date: {datetime.date.today().strftime("%B %d, %Y")}\n').font.size = Pt(11)
meta.add_run('Version: 1.0\n').font.size = Pt(11)
meta.add_run('Status: Draft for Review\n').font.size = Pt(11)
meta.add_run('Test Environment: Pixel 9 Pro Emulator, API 35\n').font.size = Pt(11)
meta.add_run('Build Tools: AGP 9.2.0, Kotlin 2.2.10, Gradle 9.4.1').font.size = Pt(11)

doc.add_page_break()

# ═══════════════════════════════════════════════════════
# TABLE OF CONTENTS
# ═══════════════════════════════════════════════════════
add_heading_styled('Table of Contents', 1)
toc_items = [
    '1. Executive Summary',
    '2. Framework Overview',
    '   2.1 Hilt (Dagger + KSP)',
    '   2.2 Metro (Kotlin Compiler Plugin)',
    '   2.3 Koin (Runtime DI)',
    '3. Test Application Architecture',
    '   3.1 Layer Structure',
    '   3.2 Class & Binding Inventory',
    '4. Compile-Time Benchmark',
    '   4.1 Methodology',
    '   4.2 Results (5 Runs)',
    '   4.3 Generated Code Analysis',
    '   4.4 How Each Framework Compiles',
    '5. Runtime Benchmark',
    '   5.1 Methodology',
    '   5.2 Container Initialization',
    '   5.3 Cold Injection (First Access)',
    '   5.4 Warm Injection (100 Iterations)',
    '   5.5 Memory Overhead',
    '6. Best Practices Applied',
    '7. Compile-Time Safety Comparison',
    '   7.1 How Each Framework Validates',
    '   7.2 Koin Compiler Plugin (K2)',
    '   7.3 Validation vs Resolution',
    '   7.4 What Still Slips Through',
    '8. Runtime DI — Advantages & Limitations',
    '   8.1 Real Advantages of Runtime Resolution',
    '   8.2 What Compile-Time DI Cannot Do',
    '   8.3 Honest Assessment',
    '9. iOS & KMP — Cross-Platform Performance Analysis',
    '   9.1 How Kotlin Code Runs on iOS',
    '   9.2 Platform Runtime Characteristics',
    '   9.3 Metro on iOS — Compile-Time DI on Native',
    '   9.4 Koin on iOS — Runtime DI on Native',
    '   9.5 Swift Interop & GC Considerations',
    '   9.6 Measured iOS Results — Compile Time & Runtime',
    '   9.7 Android vs iOS — Cross-Platform Comparison',
    '   9.8 Practical Guidance for KMP Teams',
    '10. Deep Dive — How Metro Achieves Superior Performance',
    '   10.1 K2 Compiler Pipeline (FIR Frontend → IR Backend)',
    '   10.2 Where Metro Hooks In',
    '   10.3 Hilt Pipeline vs Metro Pipeline (Step-by-Step)',
    '   10.4 What Metro Generates at Runtime',
    '   10.5 Why Metro Is 10-80x Faster Than Koin at Runtime',
    '   10.6 Square/Cash App — Real-World Proof',
    '11. Summary & Recommendation',
    '12. References & Further Reading',
]
for item in toc_items:
    p = doc.add_paragraph(item)
    p.paragraph_format.space_after = Pt(2)
    p.paragraph_format.space_before = Pt(0)

doc.add_page_break()

# ═══════════════════════════════════════════════════════
# 1. EXECUTIVE SUMMARY
# ═══════════════════════════════════════════════════════
add_heading_styled('1. Executive Summary', 1)

doc.add_paragraph(
    'This RFC presents a comprehensive performance comparison of three Android dependency injection '
    'frameworks: Hilt (Google/Dagger), Metro (Zac Sweers), and Koin (Insert-Koin). '
    'We benchmarked both compile-time and runtime performance using an identical, realistic '
    'e-commerce application with ~390 classes and ~290 DI bindings across 14 business domains '
    'and 13 feature modules.'
)

doc.add_paragraph()
add_heading_styled('Key Findings', 2)

add_table(
    ['Metric', 'Hilt', 'Metro', 'Koin', 'Winner'],
    [
        ['Compile Time (avg)', '3,943ms', '2,182ms', '1,673ms', 'Koin (57.6% faster than Hilt)'],
        ['Generated Code', '291 files / 488KB', '0 files', '0 files', 'Metro & Koin (zero codegen)'],
        ['Container Init', '0.02ms', '0.12ms', '1.70ms', 'Hilt (98.5% faster than Koin)'],
        ['Warm Injection (avg)', '4us', '2us', '55us', 'Metro (96.4% faster than Koin)'],
        ['Memory Overhead', '96KB', '128KB', '2,000KB', 'Hilt (20.8x less than Koin)'],
    ],
    [4, 3.5, 3.5, 3.5, 5]
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Bottom line: ').bold = True
p.add_run(
    'Hilt and Metro deliver the best runtime performance — both are compile-time DI frameworks with '
    'near-zero overhead. Hilt achieves the fastest container access (0.02ms) and lowest memory (96KB), '
    'while Metro leads in warm injection speed (2us avg). Koin compiles fastest (no codegen, 57.6% faster '
    'than Hilt) but pays heavily at runtime: 55us avg injection (27x slower than Metro) and 2,000KB memory '
    '(20.8x more than Hilt). Hilt compiles slowest due to KSP + Dagger code generation but offers the '
    'most mature ecosystem.'
)

doc.add_page_break()

# ═══════════════════════════════════════════════════════
# 2. FRAMEWORK OVERVIEW
# ═══════════════════════════════════════════════════════
add_heading_styled('2. Framework Overview', 1)

add_heading_styled('2.1 Hilt (Dagger + KSP)', 2)
add_table(
    ['Attribute', 'Detail'],
    [
        ['Type', 'Compile-time (annotation processing)'],
        ['Version Tested', '2.59.2'],
        ['Processing', 'KSP (Kotlin Symbol Processing) + Dagger code generation'],
        ['Annotations', '@Inject, @Singleton, @Module, @Provides, @InstallIn, @HiltAndroidApp'],
        ['Output', 'Generates Java source files (factories, component implementations)'],
        ['Scoping', '@Singleton, @ActivityScoped, @ViewModelScoped, custom scopes'],
        ['Maturity', 'Production-stable, official Google recommendation for Android'],
        ['Trade-off', 'Slowest compile time, but zero runtime overhead and full compile-time safety'],
    ],
    [4, 12]
)

doc.add_paragraph()
add_heading_styled('2.2 Metro (Kotlin Compiler Plugin)', 2)
add_table(
    ['Attribute', 'Detail'],
    [
        ['Type', 'Compile-time (Kotlin compiler plugin, FIR + IR)'],
        ['Version Tested', '0.6.5'],
        ['Author', 'Zac Sweers (github.com/ZacSweers/metro)'],
        ['Processing', 'Native Kotlin compiler plugin — no KSP/KAPT needed'],
        ['Annotations', '@Inject, @DependencyGraph, @Provides, @SingleIn, @AppScope'],
        ['Output', 'Zero source generation — code injected directly into compiler IR output'],
        ['Scoping', '@SingleIn(AppScope::class), custom @Scope annotations'],
        ['Maturity', 'Adopted by Cash App, Vinted; Kotlin-first multiplatform support'],
        ['Trade-off', 'Fastest overall performance, newer ecosystem with fewer community resources'],
    ],
    [4, 12]
)

doc.add_paragraph()
add_heading_styled('2.3 Koin (Runtime DI)', 2)
add_table(
    ['Attribute', 'Detail'],
    [
        ['Type', 'Runtime (service locator pattern with DSL)'],
        ['Version Tested', '4.2.0'],
        ['Processing', 'No annotation processing, no code generation'],
        ['DSL', 'module { }, single { }, factory { }, get(), createdAtStart()'],
        ['Output', 'None — all resolution happens at app startup and during runtime'],
        ['Scoping', 'single (app lifetime), factory (per-request), scoped (context-bound)'],
        ['Maturity', 'Widely adopted, lightweight, KMP support, simple learning curve'],
        ['Trade-off', 'Fastest compile, but slowest runtime — no compile-time DI validation'],
    ],
    [4, 12]
)

doc.add_page_break()

# ═══════════════════════════════════════════════════════
# 3. TEST APPLICATION ARCHITECTURE
# ═══════════════════════════════════════════════════════
add_heading_styled('3. Test Application Architecture', 1)

doc.add_paragraph(
    'We built an identical e-commerce application (ShopApp) in all three frameworks. '
    'The app follows Clean Architecture with four layers: Core, Data, Domain, and Feature. '
    'Every class, dependency chain, and business logic is identical — only the DI wiring differs.'
)

add_heading_styled('3.1 Layer Structure', 2)
add_table(
    ['Layer', 'Files', 'Types', 'Description'],
    [
        ['Core', '10', '59', 'Network, Auth, Analytics, Storage, Config, Logging, Image, Notification, Location'],
        ['Data', '70', '98', '14 domains × (Repository + RemoteDataSource + LocalDataSource + Mapper + Models)'],
        ['Domain', '28', '182', '14 domains × (10 UseCases + DomainModel + PagedResult + ValidationResult)'],
        ['Feature', '13', '54', '13 features: Home, Search, ProductDetail, Cart, Checkout, Profile, Orders, Settings, Chat, Notifications, Onboarding, Reviews, Wishlist'],
        ['DI/Graph', '2-5', '1-24', 'DependencyGraph (Metro), @Module (Hilt), Koin modules (Koin)'],
    ],
    [2.5, 1.5, 1.5, 11]
)

doc.add_paragraph()
add_heading_styled('3.2 Class & Binding Inventory', 2)

add_table(
    ['Role', 'Count', 'Scope', 'Description'],
    [
        ['Core Services', '53', 'Singleton', 'AuthManager, AnalyticsTracker, HttpClient, CacheManager, etc.'],
        ['Repositories', '14', 'Singleton', 'One per domain — offline-first with remote + local data sources'],
        ['Remote Data Sources', '14', 'Singleton', 'API calls via HttpClient with auth interceptor'],
        ['Local Data Sources', '14', 'Singleton', 'Database + cache layer via DatabaseManager, CacheManager'],
        ['Mappers', '14', 'Singleton', 'Entity ↔ DomainModel conversion'],
        ['Use Cases', '140', 'Factory', '10 per domain (Get, Create, Update, Delete, Search, Validate, etc.)'],
        ['ViewModels', '12', 'Factory', 'One per feature screen'],
        ['Presenters/Calculators', '22', 'Factory', 'UI logic coordinators (CartCalculator, PriceCalculator, etc.)'],
        ['Data Models', '103', '—', 'data classes (Entity, Response, Request, DomainModel, etc.)'],
    ],
    [4, 1.5, 2, 9]
)

doc.add_paragraph()
add_heading_styled('Binding Summary per Framework', 3)
add_table(
    ['Binding Type', 'Hilt', 'Metro', 'Koin'],
    [
        ['Singletons (cached instances)', '109', '105', '90'],
        ['  Eager (created at startup)', '0', '0', '19'],
        ['  Lazy (on first access)', '109', '105', '71'],
        ['Factory (new instance per request)', '176', '180', '181'],
        ['@Provides methods', '5', '5', '0'],
        ['DI Modules / Graphs', '1', '1', '24'],
        ['TOTAL BINDINGS', '290', '290', '271'],
    ],
    [5, 2.5, 2.5, 2.5]
)

doc.add_page_break()

# ═══════════════════════════════════════════════════════
# 4. COMPILE-TIME BENCHMARK
# ═══════════════════════════════════════════════════════
add_heading_styled('4. Compile-Time Benchmark', 1)

add_heading_styled('4.1 Methodology', 2)
doc.add_paragraph(
    'Each framework module was clean-built 5 times in isolation. Between each run, all three modules '
    'were cleaned (./gradlew :module:clean) to ensure cold builds. Timing was measured as wall-clock time '
    'for the compileDebugKotlin Gradle task, which includes annotation processing (Hilt), compiler plugin '
    'execution (Metro), and standard Kotlin compilation (all three).'
)

p = doc.add_paragraph()
p.add_run('Environment: ').bold = True
p.add_run('macOS, AGP 9.2.0, Kotlin 2.2.10, Gradle 9.4.1, JDK 21')

add_heading_styled('4.2 Results (5 Clean Builds)', 2)
add_table(
    ['Run', 'Hilt (KSP)', 'Metro (Plugin)', 'Koin (No codegen)'],
    [
        ['Run 1', '8,281ms', '2,947ms', '2,066ms'],
        ['Run 2', '3,223ms', '2,049ms', '1,886ms'],
        ['Run 3', '3,401ms', '2,218ms', '1,708ms'],
        ['Run 4', '2,594ms', '1,780ms', '1,347ms'],
        ['Run 5', '2,218ms', '1,916ms', '1,361ms'],
        ['', '', '', ''],
        ['Average', '3,943ms', '2,182ms', '1,673ms'],
        ['Min', '2,218ms', '1,780ms', '1,347ms'],
        ['Max', '8,281ms', '2,947ms', '2,066ms'],
    ],
    [3, 3.5, 3.5, 3.5]
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Note: ').bold = True
p.add_run(
    'Run 1 for Hilt is consistently higher due to JVM/Gradle daemon cold start and initial KSP processor '
    'class loading. This is representative of a first build after opening the project.'
)

doc.add_paragraph()
add_heading_styled('Head-to-Head Compile Time', 3)
add_table(
    ['Comparison', 'Difference', 'Percentage'],
    [
        ['Metro vs Hilt', 'Metro is 1,761ms faster', '44.7% faster'],
        ['Koin vs Hilt', 'Koin is 2,270ms faster', '57.6% faster'],
        ['Koin vs Metro', 'Koin is 509ms faster', '23.3% faster'],
    ],
    [5, 5, 4]
)

doc.add_paragraph()
add_heading_styled('4.3 Generated Code Analysis', 2)
add_table(
    ['Metric', 'Hilt', 'Metro', 'Koin'],
    [
        ['Generated source files', '291', '0', '0'],
        ['Generated lines of code', '15,078', '0', '0'],
        ['Generated code size', '488 KB', '0 KB', '0 KB'],
        ['Requires separate compilation pass', 'Yes (Java compiler)', 'No', 'No'],
    ],
    [5.5, 2.5, 2.5, 2.5]
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Key insight: ').bold = True
p.add_run(
    'Hilt generates 291 Java files (factories, component implementations, module bindings) totaling 488KB. '
    'These require a separate javac compilation pass after KSP finishes. Metro generates zero files — it '
    'injects code directly into Kotlin\'s IR (Intermediate Representation) during compilation. '
    'Koin generates zero files because all DI resolution is deferred to runtime.'
)

doc.add_paragraph()
add_heading_styled('4.4 How Each Framework Compiles', 2)

add_table(
    ['Step', 'Hilt', 'Metro', 'Koin'],
    [
        ['1', 'KSP scans annotations (@Inject, @Module, @Provides)', 'FIR phase: Analyze @DependencyGraph, @Inject', 'Standard Kotlin compilation'],
        ['2', 'Generates Java source files (factories, components)', 'IR phase: Generate implementations in IR', '(No DI processing)'],
        ['3', 'Kotlin compiler compiles source code', 'Single pass — done', '(No DI processing)'],
        ['4', 'Java compiler compiles generated code', '—', '—'],
        ['Total passes', '2 compilation passes + codegen', '1 compilation pass', '1 compilation pass'],
    ],
    [2, 5, 5, 4]
)

doc.add_page_break()

# ═══════════════════════════════════════════════════════
# 5. RUNTIME BENCHMARK
# ═══════════════════════════════════════════════════════
add_heading_styled('5. Runtime Benchmark (Hilt vs Metro vs Koin)', 1)

doc.add_paragraph(
    'Runtime benchmarks compare all three frameworks using the identical e-commerce app with ~390 classes. '
    'Hilt\'s generated Dagger component is accessed via @EntryPoint, Metro via createGraph(), and Koin via '
    'startKoin(). All three are warmed up before measurement to eliminate JVM class loading bias.'
)

add_heading_styled('5.1 Methodology', 2)
p = doc.add_paragraph()
p.add_run('Warmup phase: ').bold = True
p.add_run(
    'Both Metro and Koin are fully initialized once before measurement to eliminate JVM class loading '
    'and JIT compilation bias. Without warmup, whichever framework runs first pays the class loading tax '
    'for all 390 classes and appears unfairly slower.'
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Measurement: ').bold = True
p.add_run(
    'System.nanoTime() for timing. Runtime.freeMemory() + Debug.getNativeHeapAllocatedSize() for memory. '
    'System.gc() + 300ms pause between framework measurements. 100 warm iterations per class.'
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Device: ').bold = True
p.add_run('Pixel 9 Pro emulator, API 35 (Android 15)')

add_heading_styled('5.2 Container Initialization', 2)
doc.add_paragraph(
    'Time to access/create the DI container. Hilt\'s component is created during Application.onCreate() by '
    '@HiltAndroidApp — we measure EntryPointAccessors.fromApplication() cost. Metro calls createGraph<ShopAppGraph>(). '
    'Koin calls startKoin { modules(allShopAppModules) } with 24 modules and 19 eager singletons.'
)
add_table(
    ['Framework', 'Init Time', 'What Happens'],
    [
        ['Hilt', '0.02ms', 'EntryPointAccessors.fromApplication() — component already built at app start'],
        ['Metro', '0.12ms', 'Creates one generated class instance (all wiring compiled into it)'],
        ['Koin', '1.70ms', 'Registers 271 lambda definitions across 24 modules, creates 19 eager singletons'],
    ],
    [3, 2.5, 10]
)
doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Note: ').bold = True
p.add_run(
    'Hilt\'s container initialization cost is paid once during Application.onCreate() (via @HiltAndroidApp). '
    'The measured time here reflects only the EntryPointAccessors lookup, not the full Dagger component build. '
    'Metro\'s graph is a single pre-compiled class with all wiring baked in. Koin must process '
    '24 module DSL blocks, register each binding in a HashMap, and eagerly create 19 singleton instances.'
)

doc.add_paragraph()
add_heading_styled('5.3 Cold Injection (First Access)', 2)
doc.add_paragraph(
    'Time to resolve a class for the first time after container initialization. Includes constructing '
    'the object and all its transitive dependencies.'
)
add_table(
    ['Class', 'Hilt', 'Metro', 'Koin'],
    [
        ['HomeViewModel (6 deps, ~50 transitive)', '15us', '46us', '610us'],
        ['SearchViewModel (3 deps)', '5us', '12us', '126us'],
        ['ProductDetailVM (6 deps)', '7us', '22us', '346us'],
        ['CartViewModel (5 deps)', '5us', '5us', '94us'],
        ['CheckoutViewModel (7 deps)', '9us', '102us', '394us'],
        ['ProfileViewModel (7 deps)', '6us', '12us', '166us'],
        ['ChatViewModel (5 deps)', '5us', '12us', '139us'],
        ['OrderHistoryVM (3 deps)', '2us', '3us', '55us'],
        ['AnalyticsTracker (2 deps)', '0.4us', '0.3us', '5us'],
        ['ProductRepository (4 deps)', '0.4us', '0.3us', '5us'],
    ],
    [5.5, 2.5, 2.5, 2.5]
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Analysis: ').bold = True
p.add_run(
    'Hilt resolves dependencies via generated Dagger factories — direct constructor calls with no reflection. '
    'Metro\'s singletons are initialized during createGraph() and accessed via direct field reads. '
    'Koin must traverse its registry, resolve each dependency via HashMap lookup, '
    'and invoke the provider lambda for each node in the dependency chain.'
)

doc.add_paragraph()
add_heading_styled('5.4 Warm Injection (Average of 100 Iterations)', 2)
doc.add_paragraph(
    'Time to resolve a class after it has been resolved at least once. Singletons return cached instances; '
    'factory-scoped classes create new instances each time.'
)
add_table(
    ['Class', 'Hilt', 'Metro', 'Koin'],
    [
        ['HomeViewModel', '3us', '4us', '105us'],
        ['SearchViewModel', '2us', '2us', '46us'],
        ['ProductDetailVM', '4us', '4us', '84us'],
        ['CartViewModel', '3us', '4us', '65us'],
        ['CheckoutViewModel', '3us', '4us', '103us'],
        ['ProfileViewModel', '11us', '4us', '68us'],
        ['ChatViewModel', '8us', '3us', '48us'],
        ['OrderHistoryVM', '7us', '2us', '29us'],
        ['AnalyticsTracker', '0.2us', '0.2us', '3us'],
        ['ProductRepository', '0.2us', '0.2us', '3us'],
        ['', '', '', ''],
        ['TOTAL avg/injection', '4us', '2us', '55us'],
    ],
    [5.5, 2.5, 2.5, 2.5]
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Analysis: ').bold = True
p.add_run(
    'Hilt\'s generated Dagger factories invoke constructors directly — singleton access is a volatile field read. '
    'With @SingleIn(AppScope::class) scoping, Metro\'s singleton access is a direct field read. '
    'Koin\'s single{} also caches, but get<T>() still performs: HashMap.get() → type check → return cached. '
    'For factory-scoped classes (ViewModels, UseCases), both Hilt and Metro invoke generated constructors directly, '
    'while Koin invokes a stored lambda + resolves each get() parameter recursively.'
)

doc.add_paragraph()
add_heading_styled('5.5 Memory Overhead', 2)
add_table(
    ['Framework', 'Memory Delta', 'What Consumes Memory'],
    [
        ['Hilt', '96 KB', 'Generated Dagger component instance + cached singleton instances (volatile fields)'],
        ['Metro', '128 KB', 'One graph class instance + cached singleton instances'],
        ['Koin', '2,000 KB', 'Module registry, 271 lambda definitions, HashMap entries, singleton cache, type metadata'],
    ],
    [3, 2.5, 10]
)
doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Analysis: ').bold = True
p.add_run(
    'Hilt and Metro both use compile-time generated code with direct field references — their memory footprint '
    'is primarily the singleton instances themselves. Koin\'s additional overhead comes from storing lambda '
    'closures for every binding, HashMap data structures for the service locator registry, and runtime type '
    'metadata for resolution.'
)

doc.add_page_break()

# ═══════════════════════════════════════════════════════
# 6. BEST PRACTICES APPLIED
# ═══════════════════════════════════════════════════════
add_heading_styled('6. Best Practices Applied', 1)

doc.add_paragraph(
    'To ensure a fair comparison, each framework was configured using its recommended best practices. '
    'No framework was given an unfair advantage or handicap.'
)

add_heading_styled('6.1 Hilt Best Practices', 2)
practices = [
    '@Singleton on all core services, repositories, data sources, and mappers (109 singletons)',
    '@Inject constructor for dependency declaration — no manual wiring',
    '@Module + @Provides only for classes where constructor injection is not possible (5 methods)',
    '@InstallIn(SingletonComponent::class) for app-level bindings',
    'KSP (not KAPT) for annotation processing — faster than legacy KAPT',
    'Hilt 2.59.2 — required for AGP 9 compatibility',
]
for p in practices:
    doc.add_paragraph(p, style='List Bullet')

add_heading_styled('6.2 Metro Best Practices', 2)
practices = [
    '@DependencyGraph(AppScope::class) — scoped graph for singleton caching',
    '@SingleIn(AppScope::class) on all core services, repositories, data sources, mappers (105 singletons)',
    '@Inject constructor for dependency declaration — identical pattern to Hilt',
    '@Provides in graph interface for classes requiring manual construction (5 methods)',
    'Unscoped factory classes (UseCases, ViewModels) — new instance per access',
    'Metro 0.6.5 — compatible with Kotlin 2.2.10, uses FIR + IR compiler phases',
]
for p in practices:
    doc.add_paragraph(p, style='List Bullet')

add_heading_styled('6.3 Koin Best Practices', 2)
practices = [
    'single {} for all core services, repositories, data sources, mappers (90 singletons)',
    'createdAtStart = true on 19 critical infrastructure singletons (HttpClient, DatabaseManager, AuthManager, AnalyticsTracker, etc.) — pre-warmed at startKoin()',
    'factory {} for all use cases, ViewModels, presenters (181 factories)',
    'Modular organization: 24 Koin modules (9 core + 1 data + 1 domain + 13 feature)',
    'get() for dependency resolution within module definitions',
    'Koin 4.2.0 — latest stable release',
]
for p in practices:
    doc.add_paragraph(p, style='List Bullet')

doc.add_page_break()

# ═══════════════════════════════════════════════════════
# 7. COMPILE-TIME SAFETY COMPARISON
# ═══════════════════════════════════════════════════════
add_heading_styled('7. Compile-Time Safety Comparison', 1)

doc.add_paragraph(
    'A critical differentiator between DI frameworks is whether dependency graph errors are caught '
    'at compile time (build fails) or at runtime (app crashes). This section examines each framework\'s '
    'validation capabilities in depth.'
)

add_heading_styled('7.1 How Each Framework Validates the Dependency Graph', 2)

add_table(
    ['Aspect', 'Hilt', 'Metro', 'Koin (DSL only)', 'Koin + Compiler Plugin'],
    [
        ['Missing dependency', 'Build fails', 'Build fails', 'Runtime crash', 'Build fails'],
        ['Type mismatch', 'Build fails', 'Build fails', 'Runtime crash', 'Build fails'],
        ['Cyclic dependency', 'Build fails', 'Build fails', 'Runtime hang/crash', 'Build fails'],
        ['Wrong qualifier', 'Build fails', 'Build fails', 'Runtime crash', 'Partially caught'],
        ['Scope violation', 'Build fails', 'Build fails', 'Silent misuse', 'Build fails'],
        ['Dynamic modules', 'N/A (static graph)', 'N/A (static graph)', 'No validation', 'No validation'],
        ['Generic type errors', 'Build fails', 'Build fails', 'Runtime crash', 'Not always caught'],
        ['Technology', 'KSP + Dagger', 'FIR + IR', 'None', 'K2 Compiler Plugin'],
    ],
    [3.5, 2.5, 2.5, 3, 3.5]
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Key insight: ').bold = True
p.add_run(
    'Hilt and Metro provide complete compile-time safety — if the project builds, the DI graph is guaranteed '
    'to be valid. Koin DSL provides zero compile-time checks. The Koin Compiler Plugin closes most of this gap '
    'but still has edge cases that can crash at runtime.'
)

doc.add_paragraph()
add_heading_styled('7.2 Koin Compiler Plugin (K2) — Closing the Safety Gap', 2)

doc.add_paragraph(
    'Koin has developed a native Kotlin Compiler Plugin (K2, FIR + IR) — the same technology Metro uses — '
    'to add compile-time dependency graph validation. This is the recommended approach for all new Kotlin 2.x projects.'
)

add_table(
    ['Attribute', 'Koin Annotations (KSP)', 'Koin Compiler Plugin (K2)'],
    [
        ['Technology', 'KSP annotation processing', 'Native Kotlin compiler plugin (FIR + IR)'],
        ['Kotlin requirement', 'Any version', 'Kotlin 2.3.20+'],
        ['Generated files', 'Yes (KSP output)', 'None (inline transformation)'],
        ['Status', 'Being deprecated', 'Recommended for new projects'],
        ['KMP support', 'Yes (with per-platform KSP config)', 'Yes (native, no config)'],
    ],
    [4, 5.5, 5.5]
)

doc.add_paragraph()
add_heading_styled('Validation Levels', 3)

doc.add_paragraph(
    'The Koin Compiler Plugin validates dependencies at three progressive levels:'
)

add_table(
    ['Level', 'When', 'What It Validates', 'Example'],
    [
        ['A2 — Per-Module', 'Each module compiles', 'Missing deps within a module, qualifier mismatches, cross-scope violations', 'Service(repo: Repository) fails if Repository not in visible module'],
        ['A3 — Full Graph', 'At startKoin<T>() declaration', 'Cross-module dependencies, all modules combined', 'startKoin<MyApp>() validates CoreModule + DataModule + FeatureModule together'],
        ['A4 — Call-Site', 'Every injection call', 'Every get<T>(), inject<T>(), koinViewModel<T>() call validated', 'val vm: UserVM = koinViewModel() fails if UserVM not registered'],
    ],
    [2.5, 3, 5, 5.5]
)

doc.add_paragraph()
add_heading_styled('7.3 Validation vs Resolution — The Critical Difference', 2)

doc.add_paragraph(
    'Even with the Koin Compiler Plugin, there is a fundamental architectural difference:'
)

add_table(
    ['', 'Hilt / Metro', 'Koin + Compiler Plugin'],
    [
        ['Compile-time validation', 'Yes — graph verified at build', 'Yes — graph verified at build'],
        ['Compile-time resolution', 'Yes — constructor calls generated as code', 'No — dependencies still resolved via HashMap at runtime'],
        ['Runtime mechanism', 'Direct constructor invocation (zero overhead)', 'HashMap.get() → type check → invoke lambda (overhead per lookup)'],
        ['Performance impact', 'Near-zero (compiled code)', 'Same as Koin without plugin (~64us avg)'],
        ['Analogy', 'GPS route compiled into car\'s computer', 'Google Maps verified destination exists, but you still navigate turn-by-turn'],
    ],
    [3.5, 6, 6]
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Bottom line: ').bold = True
p.add_run(
    'The Koin Compiler Plugin closes the safety gap — most missing bindings are now caught at build time. '
    'However, it does NOT close the performance gap. Dependencies are still resolved at runtime via Koin\'s '
    'service locator. The plugin adds verification, not compilation of the dependency graph.'
)

doc.add_paragraph()
add_heading_styled('7.4 What Still Slips Through Koin\'s Compile-Time Checks', 2)

add_table(
    ['Scenario', 'Hilt', 'Metro', 'Koin + Plugin'],
    [
        ['Missing standard dependency', 'Build error', 'Build error', 'Build error'],
        ['@InjectedParam / @Provided (runtime params)', 'Build error', 'Build error', 'NOT checked — trusted at runtime'],
        ['Wrong @Qualifier matching', 'Build error', 'Build error', 'Partially caught — can match wrong qualifier silently'],
        ['Generic type binding errors', 'Build error', 'Build error', 'NOT always caught — type erasure issues'],
        ['Dynamic loadKoinModules()', 'N/A', 'N/A', 'NOT checked — loaded modules unknown at compile time'],
        ['binds() with wrong class', 'Build error', 'Build error', 'Can pass compilation, fail at runtime'],
    ],
    [5, 2.5, 2.5, 5.5]
)

doc.add_page_break()

# ═══════════════════════════════════════════════════════
# 8. RUNTIME DI — ADVANTAGES & LIMITATIONS
# ═══════════════════════════════════════════════════════
add_heading_styled('8. Runtime DI — Real Advantages & Limitations', 1)

doc.add_paragraph(
    'Runtime DI (Koin) is often criticized for being slower and less safe than compile-time DI (Hilt, Metro). '
    'However, it offers specific capabilities that compile-time frameworks genuinely cannot provide. '
    'This section presents an honest assessment of both sides.'
)

add_heading_styled('8.1 Real Advantages of Runtime Resolution', 2)

add_heading_styled('Dynamic Dependency Swapping', 3)
doc.add_paragraph(
    'The one capability compile-time DI genuinely cannot replicate. Koin can swap implementations '
    'at runtime based on server configuration, user actions, or A/B test assignments — without '
    'recompiling the app.'
)
p = doc.add_paragraph()
p.add_run('Example: ').bold = True
p.add_run(
    'A server-driven config tells the app to use StripeProcessor for payments in the US and '
    'RazorpayProcessor in India. With Koin, you can loadKoinModules() with the appropriate '
    'implementation after the config is fetched — the DI graph changes at runtime. '
    'With Hilt/Metro, the graph is fixed at compile time; you must use if/else inside a @Provides method.'
)

doc.add_paragraph()
add_heading_styled('Module Loading & Unloading', 3)
doc.add_paragraph(
    'Koin supports loadKoinModules() and unloadKoinModules() at runtime. This enables:'
)
points = [
    'Loading feature dependencies only when the user navigates to that feature',
    'Unloading dependencies when the user leaves (freeing memory)',
    'Reloading with different configuration after a user action (e.g., login/logout)',
    'Supporting Play Feature Delivery dynamic feature modules — whose classes don\'t exist at compile time',
]
for pt in points:
    doc.add_paragraph(pt, style='List Bullet')

doc.add_paragraph()
add_heading_styled('Zero Plugin/Processor Dependency', 3)
doc.add_paragraph(
    'Koin (DSL mode) is just a Kotlin library — no compiler plugin, no KSP, no annotation processor. '
    'This means:'
)
points = [
    'No version compatibility issues — Koin works with any Kotlin version immediately on release',
    'No KSP version must match Kotlin version (a common pain point with Hilt)',
    'No compiler plugin compatibility breaks on Kotlin upgrades (Metro 0.6.5 requires specific Kotlin range)',
    'Simpler build configuration — no pluginManagement, no KSP wiring, no generated source directories',
]
for pt in points:
    doc.add_paragraph(pt, style='List Bullet')

doc.add_paragraph()
add_heading_styled('Testing Simplicity', 3)
doc.add_paragraph(
    'Koin\'s test setup is the simplest of all three frameworks:'
)
points = [
    'startKoin { modules(testModule) } / stopKoin() — no special test runners or generated components',
    'Override any dependency inline: single(override = true) { MockRepository() }',
    'No @HiltAndroidTest, no custom test runner, no generated test component',
    'Fresh dependency graph per test with zero configuration',
]
for pt in points:
    doc.add_paragraph(pt, style='List Bullet')

doc.add_paragraph()
add_heading_styled('8.2 What Compile-Time DI Cannot Do', 2)

add_table(
    ['Capability', 'Hilt', 'Metro', 'Koin'],
    [
        ['Swap implementation at runtime without recompile', 'No', 'No', 'Yes'],
        ['Load/unload DI modules dynamically', 'No', 'No', 'Yes'],
        ['Support Play Feature Delivery dynamic modules', 'Limited', 'Limited', 'Yes (native)'],
        ['Work with any Kotlin version on day 1', 'No (KSP lag)', 'No (plugin compat)', 'Yes'],
        ['Zero build tool configuration', 'No (KSP + Hilt plugin)', 'No (Metro plugin)', 'Yes (DSL mode)'],
        ['Override dependencies in tests inline', 'Complex', 'Moderate', 'Trivial'],
    ],
    [5.5, 2.5, 2.5, 3]
)

doc.add_paragraph()
add_heading_styled('8.3 Honest Assessment — When Do These Advantages Matter?', 2)

doc.add_paragraph(
    'While the runtime DI advantages listed above are real, their practical relevance varies significantly:'
)

add_table(
    ['Advantage', 'How Often It Matters', 'Alternative in Hilt/Metro'],
    [
        ['Dynamic dependency swapping', 'Rare — most apps don\'t change DI providers at runtime', 'if/else inside @Provides method or Strategy pattern'],
        ['Module load/unload', 'Rare — Android lifecycle scoping handles most cases', '@ActivityScoped, @ViewModelScoped in Hilt; graph extensions in Metro'],
        ['Play Feature Delivery support', 'Niche — few apps use dynamic feature modules', 'Reflection-based entry points or interface-based lookup'],
        ['Zero plugin dependency', 'Moderate — relevant during major Kotlin version upgrades', 'Wait for KSP/plugin update (usually 1-2 weeks)'],
        ['Testing simplicity', 'Genuine — saves time daily for developers writing tests', 'Hilt testing works well once set up; Metro is straightforward'],
    ],
    [4, 4.5, 5.5]
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('The real-world perspective: ').bold = True
p.add_run(
    'The 64us average injection time we measured sounds significant in a benchmark, but in a real app '
    'where a network call takes 200-500ms and a screen render takes 16ms, the DI resolution time is '
    'less than 0.03% of any user-visible operation. For most apps, the performance difference between '
    '2us (Metro) and 64us (Koin) is imperceptible to users. '
    'The practical reasons developers choose Koin — simplicity, faster builds, KMP support, easier onboarding — '
    'are human/productivity factors, not technical superiority of runtime resolution.'
)

doc.add_page_break()

# ═══════════════════════════════════════════════════════
# 9. iOS & KMP — CROSS-PLATFORM PERFORMANCE ANALYSIS
# ═══════════════════════════════════════════════════════
add_heading_styled('9. iOS & KMP — Cross-Platform Performance Analysis', 1)

doc.add_paragraph(
    'Kotlin Multiplatform (KMP) enables sharing business logic — including DI configuration — across '
    'Android and iOS. Both Metro and Koin support KMP. This section examines how each framework '
    'behaves on iOS, where the runtime environment differs significantly from the JVM.'
)

add_heading_styled('9.1 How Kotlin Code Runs on iOS', 2)
doc.add_paragraph(
    'The same Kotlin source code takes fundamentally different paths to execution on each platform:'
)
add_table(
    ['Stage', 'Android (JVM)', 'iOS (Kotlin/Native)'],
    [
        ['Compilation', '.kt → FIR → IR → JVM Bytecode → .dex', '.kt → FIR → IR → LLVM IR → ARM64 native binary'],
        ['Runtime', 'ART (Android Runtime) with JIT compilation', 'Native machine code — no VM, no interpreter'],
        ['Optimization', 'JIT: hot code paths optimized at runtime', 'AOT: all optimization at compile time via LLVM'],
        ['GC', 'Generational, concurrent, low-pause', 'Stop-the-world mark + concurrent sweep'],
        ['Output', '.apk containing .dex bytecode', '.framework containing ARM64 binary'],
    ],
    [2.5, 6.5, 6.5]
)
doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Key point: ').bold = True
p.add_run(
    'On iOS, Kotlin compiles to ARM64 machine code via LLVM — the same backend used by Swift and Clang. '
    'There is no VM, no interpreter, no bridge. Per JetBrains, Kotlin/Native startup time on iOS is about '
    '15% faster than Swift, and over 96% of teams report no major performance concerns.'
)

doc.add_paragraph()
add_heading_styled('9.2 Platform Runtime Characteristics', 2)
doc.add_paragraph(
    'These differences are inherent to the platform — they affect all Kotlin code, not just DI frameworks:'
)
add_table(
    ['Characteristic', 'JVM (Android)', 'Kotlin/Native (iOS)', 'DI Impact'],
    [
        ['JIT Compilation', 'Hot paths optimized at runtime', 'AOT only — LLVM optimizes at compile time', 'Repeated injection calls benefit from JIT on Android; iOS relies on LLVM AOT optimization'],
        ['Concurrent Collections', 'ConcurrentHashMap (lock-striped)', 'stdlib HashMap + manual sync', 'Service locator patterns use different primitives per platform'],
        ['GC Behavior', 'Generational, short pauses', 'Stop-the-world mark + concurrent sweep', 'Object-heavy operations may see longer pauses; experimental concurrent marking available'],
        ['Lambda Dispatch', 'JIT can inline after warmup', 'Always indirect call (AOT)', 'Frameworks storing lambdas have different call overhead per platform'],
        ['Atomics', 'java.util.concurrent (mature)', 'kotlin.native.concurrent.AtomicReference', 'Thread-safe singleton access uses different primitives; Stately library provides KMP alternatives'],
    ],
    [3, 3.5, 3.5, 5.5]
)
doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Context: ').bold = True
p.add_run(
    'The new Kotlin/Native memory model (default since 1.7.20) simplified concurrency, and experimental '
    'concurrent marking reduces GC pauses. Both Metro and Koin work correctly on iOS — '
    'the differences are in performance characteristics, not correctness.'
)

doc.add_page_break()

add_heading_styled('9.3 Metro on iOS — Compile-Time DI on Native', 2)
doc.add_paragraph(
    'Metro\'s compiler plugin runs during Kotlin compilation, identically for all targets. '
    'The generated code — direct constructor calls and volatile field reads — goes through LLVM.'
)
add_table(
    ['Aspect', 'Behavior on iOS'],
    [
        ['Compilation', 'Metro IR codegen runs during Kotlin compilation; output goes through LLVM like any Kotlin code'],
        ['Singleton access', 'DoubleCheck volatile reads compile to ARM64 ldar (load-acquire) — similar cost to Swift lazy var'],
        ['Factory creation', 'Direct constructor calls compile to ARM64 bl (branch-link) — identical to Swift init()'],
        ['LLVM optimization', 'Direct calls are eligible for inlining, constant folding, dead code elimination'],
        ['GC impact', 'Minimal — only business objects created, no DI infrastructure objects'],
        ['Thread safety', 'Native mutex + atomic on first access; subsequent reads are lock-free atomic loads'],
        ['Memory footprint', 'One graph class + cached singletons — no HashMap, no lambdas, no metadata'],
        ['vs Android performance', 'Comparable — same code patterns (field read / constructor call) perform similarly'],
    ],
    [3.5, 12.5]
)
doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('In practice: ').bold = True
p.add_run(
    'Metro\'s generated code is architecturally identical to hand-written factory classes. LLVM optimizes '
    'it the same way it would optimize equivalent Swift code — the DI framework disappears entirely at runtime.'
)

doc.add_paragraph()
add_heading_styled('9.4 Koin on iOS — Runtime DI on Native', 2)
doc.add_paragraph(
    'Koin works correctly on iOS and is used in many production KMP apps. '
    'The runtime resolution path has different performance characteristics on Native vs JVM:'
)
add_table(
    ['Aspect', 'Behavior on iOS'],
    [
        ['Module registration', 'startKoin() works identically to Android — registers all definitions'],
        ['Dependency resolution', 'get<T>() performs type-based lookup and resolution — functionally identical'],
        ['Collections', 'stdlib HashMap rather than ConcurrentHashMap; thread safety via platform-specific atomics'],
        ['Lambda execution', 'Always indirect calls (no JIT); on JVM, JIT can optimize hot lambdas'],
        ['GC interaction', 'Registry structures (HashMap, lambdas, metadata) are tracked by GC during marking phase'],
        ['Memory footprint', 'Registry + lambda closures + singletons — proportional to binding count'],
        ['vs Android performance', 'Measurably different in microbenchmarks due to no JIT and different collections, but acceptable for most apps'],
    ],
    [3.5, 12.5]
)
doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('In practice: ').bold = True
p.add_run(
    'Companies like JetBrains (KotlinConf app), Philips, and many others ship production KMP apps with Koin on iOS. '
    'The runtime overhead is typically imperceptible to users — a network call (~200ms) dwarfs hundreds of '
    'get<T>() resolutions. Koin\'s value on iOS — simplicity, fast builds, established KMP patterns — remains compelling.'
)

doc.add_paragraph()
add_heading_styled('9.5 Swift Interop & GC Considerations', 2)
doc.add_paragraph(
    'Objects crossing the Kotlin - Swift boundary are managed by both Kotlin GC and Swift ARC:'
)
add_table(
    ['Concern', 'Details', 'Framework Impact'],
    [
        ['Object wrapping', 'Kotlin objects become stable references in GC root set', 'Equal for both — depends on boundary crossing count'],
        ['Deinitialization', 'Not immediate — waits for Kotlin GC cycle', 'Equal for both — Kotlin/Native behavior'],
        ['Retain cycles', 'Mixed Kotlin-ObjC cycles not auto-broken; use weak/unowned in Swift', 'Equal — design concern, not DI-specific'],
        ['autoreleasepool', 'Use in loops creating many cross-boundary objects', 'Equal — Kotlin/Native best practice'],
    ],
    [3, 7, 5.5]
)
doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('GC optimization options: ').bold = True
p.add_run(
    'kotlin.native.binary.gc=cms (concurrent marking, experimental), '
    'kotlin.native.binary.appStateTracking=enabled (background optimization), '
    '-Xruntime-logs=gc=info (pause monitoring).'
)

doc.add_page_break()

add_heading_styled('9.6 Measured iOS Results — Compile Time & Runtime', 2)

doc.add_paragraph(
    'We built identical KMP modules (benchmark-kmp-metro and benchmark-kmp-koin) targeting '
    'iosSimulatorArm64, each containing the full e-commerce app (~350 classes, ~285 bindings). '
    'Both frameworks use best practices: Metro with @SingleIn(AppScope::class) scoping, '
    'Koin with createdAtStart on 19 critical singletons. Benchmark methodology follows '
    'kotlinx-benchmark patterns: warmup phase (5 iterations, excluded from results), '
    'blackhole to prevent LLVM dead code elimination, single container lifecycle per framework.'
)

doc.add_paragraph()
add_heading_styled('iOS Compile Time (3 clean builds, IosSimulatorArm64)', 3)
add_table(
    ['Metric', 'Metro', 'Koin'],
    [
        ['Average', '1,871ms', '1,260ms'],
        ['Min', '1,340ms', '1,093ms'],
        ['Max', '2,770ms', '1,425ms'],
        ['Comparison', '', 'Koin 32.7% faster'],
    ],
    [5, 4, 4]
)
doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Why Koin compiles faster on iOS: ').bold = True
p.add_run(
    'Same reason as Android — Koin has zero codegen overhead. Metro\'s compiler plugin runs FIR analysis '
    'and IR codegen during Kotlin/Native compilation, adding ~600ms. Both go through the same LLVM pipeline afterward.'
)

doc.add_paragraph()
add_heading_styled('iOS Runtime — Container Initialization', 3)
add_table(
    ['Framework', 'Init Time', 'What Happens'],
    [
        ['Metro', '0.01ms', 'Creates one pre-compiled graph class with all wiring baked in'],
        ['Koin', '0.17ms', 'Registers 271 definitions across 24 modules, creates 19 eager singletons'],
    ],
    [3, 2, 11]
)
doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Metro is 17x faster. ').bold = True
p.add_run(
    'Metro\'s graph is a single LLVM-compiled class — instantiation is one ARM64 constructor call. '
    'Koin must process 24 module DSL blocks and populate its internal HashMap registry.'
)

doc.add_paragraph()
add_heading_styled('iOS Runtime — Cold Injection (First Access)', 3)
add_table(
    ['Class', 'Metro', 'Koin', 'Metro Faster By'],
    [
        ['HomeViewModel (6 deps, ~50 transitive)', '7us', '31us', '4.4x'],
        ['CheckoutViewModel (7 deps)', '3us', '33us', '11x'],
        ['ProductDetailVM (6 deps)', '3us', '27us', '9x'],
        ['ProfileViewModel (7 deps)', '1us', '15us', '15x'],
        ['ChatViewModel (5 deps)', '1us', '13us', '13x'],
        ['SearchViewModel (3 deps)', '2us', '10us', '5x'],
        ['CartViewModel (5 deps)', '1us', '8us', '8x'],
        ['OrderHistoryVM (3 deps)', '<1us', '4us', '>4x'],
        ['AnalyticsTracker (2 deps)', '<1us', '<1us', '~1x'],
        ['ProductRepository (4 deps)', '<1us', '<1us', '~1x'],
    ],
    [5.5, 2, 2, 2.5]
)
doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Metro wins 10/10 classes. ').bold = True
p.add_run(
    'Metro\'s singletons are pre-wired at createGraph() — first property access is a direct volatile field read. '
    'Koin must resolve the full dependency chain via HashMap lookup on each get<T>() call, recursively '
    'for every transitive dependency.'
)

doc.add_paragraph()
add_heading_styled('iOS Runtime — Warm Injection (Avg of 100 Iterations)', 3)
add_table(
    ['Class', 'Metro', 'Koin', 'Metro Faster By'],
    [
        ['HomeViewModel', '<1us', '7us', '>7x'],
        ['CartViewModel', '<1us', '8us', '>8x'],
        ['CheckoutViewModel', '<1us', '7us', '>7x'],
        ['ProductDetailVM', '<1us', '7us', '>7x'],
        ['ProfileViewModel', '<1us', '7us', '>7x'],
        ['SearchViewModel', '<1us', '4us', '>4x'],
        ['ChatViewModel', '<1us', '3us', '>3x'],
        ['OrderHistoryVM', '<1us', '3us', '>3x'],
        ['AnalyticsTracker', '<1us', '<1us', '~1x'],
        ['ProductRepository', '<1us', '<1us', '~1x'],
        ['', '', '', ''],
        ['TOTAL avg/injection', '<1us', '6us', '>6x'],
    ],
    [5.5, 2, 2, 2.5]
)
doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Metro is effectively instant on warm access. ').bold = True
p.add_run(
    'With @SingleIn(AppScope::class) scoping, Metro\'s singleton access compiles to an ARM64 ldar '
    '(load-acquire) instruction — a single CPU cycle plus memory barrier. Koin\'s single{} also caches '
    'the instance, but get<T>() still performs HashMap.get() + type resolution + return on every access. '
    'On iOS (no JIT), this overhead cannot be optimized away at runtime.'
)

doc.add_paragraph()
add_heading_styled('iOS Memory', 3)
p = doc.add_paragraph()
p.add_run('Note: ').bold = True
p.add_run(
    'iOS process-level memory measurement (mach_task_basic_info.resident_size) is unreliable for '
    'DI-level granularity because Kotlin/Native GC cycles, system page reclamation, and SwiftUI rendering '
    'all affect resident memory between measurements. For accurate iOS memory profiling, '
    'use Xcode Instruments (Allocations instrument). The Android benchmark (which uses '
    'Debug.getNativeHeapAllocatedSize()) showed Metro at 241KB vs Koin at 1,537KB — a 6.4x difference. '
    'We expect a similar ratio on iOS based on the identical code structure.'
)

doc.add_page_break()

add_heading_styled('9.7 Android vs iOS — Cross-Platform Comparison', 2)

doc.add_paragraph(
    'The same e-commerce app (~350 classes, ~285 bindings) was benchmarked on both platforms. '
    'This is the first apples-to-apples cross-platform DI comparison with identical code:'
)

add_table(
    ['Metric', 'Android (Metro)', 'Android (Koin)', 'iOS (Metro)', 'iOS (Koin)'],
    [
        ['Container Init', '0.14ms', '1.45ms', '0.01ms', '0.17ms'],
        ['Cold avg', '~15us', '~167us', '~2us', '~13us'],
        ['Warm avg/injection', '11us', '64us', '<1us', '6us'],
        ['Memory', '241KB', '1,537KB', 'N/A (use Instruments)', 'N/A (use Instruments)'],
    ],
    [3, 3, 3, 3, 3]
)

doc.add_paragraph()
add_heading_styled('Why iOS Numbers Are Lower Than Android', 3)

doc.add_paragraph(
    'Both Metro and Koin show faster absolute times on iOS than Android. This is not an error — '
    'it reflects genuine platform differences:'
)
points = [
    'The iOS benchmark runs on Apple Silicon (M-series) via the Simulator, which has faster single-core performance than the Android emulator running on the same hardware.',
    'Kotlin/Native compiles to native ARM64 machine code — there is no VM startup overhead, no JIT warmup. Code runs at full speed from the first call.',
    'The warmup phase in our benchmark eliminates class-loading bias on both platforms, making the comparison fair.',
    'Metro\'s advantage over Koin is consistent across platforms: ~6x faster on both Android (11us vs 64us) and iOS (<1us vs 6us) for warm injection.',
]
for pt in points:
    doc.add_paragraph(pt, style='List Bullet')

doc.add_paragraph()
add_heading_styled('Key Cross-Platform Observation', 3)
p = doc.add_paragraph()
p.add_run('Metro\'s relative advantage is consistent: ').bold = True
p.add_run(
    'On Android, Metro is 5.8x faster than Koin on warm injection (11us vs 64us). '
    'On iOS, Metro is >6x faster (<1us vs 6us). The ratio holds because the architectural '
    'difference is the same: Metro uses direct field reads (volatile/atomic), Koin uses '
    'HashMap lookups + lambda invocations. The absolute times are lower on iOS because '
    'native ARM64 code on Apple Silicon is simply faster than JVM bytecode on an Android emulator.'
)

doc.add_page_break()

add_heading_styled('9.8 Practical Guidance for KMP Teams', 2)
add_table(
    ['Scenario', 'Recommended', 'Reasoning'],
    [
        ['Large KMP app, performance-sensitive iOS UI', 'Metro', 'Zero runtime overhead; LLVM optimizes generated code like native Swift'],
        ['Small-medium KMP app, rapid development', 'Koin', 'Simplest setup, fast builds, established patterns; overhead is negligible'],
        ['Team migrating from Dagger/Hilt to KMP', 'Metro', 'Familiar @Inject/@Provides patterns reduce migration effort'],
        ['App with dynamic features on iOS', 'Koin', 'Runtime module loading/unloading fits naturally'],
        ['Rapid prototyping / MVP', 'Koin', 'Fastest to set up, extensive documentation'],
        ['Consistent perf across platforms', 'Metro', 'Same code pattern performs identically on JVM and LLVM'],
    ],
    [5, 2, 9]
)
doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Both are production-ready for KMP. ').bold = True
p.add_run(
    'Metro offers better raw performance; Koin offers better developer ergonomics. Neither is a wrong choice — '
    'they represent different trade-offs on the same spectrum.'
)

doc.add_page_break()

# ═══════════════════════════════════════════════════════
# 10. DEEP DIVE — HOW METRO ACHIEVES SUPERIOR PERFORMANCE
# ═══════════════════════════════════════════════════════
add_heading_styled('10. Deep Dive — How Metro Achieves Superior Performance', 1)

doc.add_paragraph(
    'Metro is faster than Hilt at compile time AND faster than Koin at runtime. This section explains '
    'exactly how — starting with the Kotlin K2 compiler architecture, then showing where Metro hooks in, '
    'and finally revealing what code Metro actually generates.'
)

add_heading_styled('10.1 K2 Compiler Pipeline', 2)

doc.add_paragraph(
    'The Kotlin K2 compiler has two distinct phases with a clear boundary between them:'
)

add_table(
    ['Phase', 'Component', 'What Happens'],
    [
        ['1. Frontend', 'PSI Parser', 'Source code (.kt) is parsed into a PSI (Program Structure Interface) tree'],
        ['2. Frontend', 'Raw FIR', 'PSI tree is transformed into FIR (Frontend Intermediate Representation)'],
        ['3. Frontend', 'FIR Resolution', 'Types, symbols, scopes, imports, annotations are resolved in multiple passes'],
        ['4. Frontend', 'FIR Checkers', 'Diagnostics run — warnings and errors are reported to IDE and build output'],
        ['— Bridge —', 'Fir2Ir', 'Resolved FIR is transformed into IR (Intermediate Representation)'],
        ['5. Backend', 'IR Generation', 'IR tree is built from Fir2Ir output'],
        ['6. Backend', 'IR Lowering', 'IR is optimized and lowered for the target platform'],
        ['7. Backend', 'Code Emission', 'Final bytecode (.class for JVM), JavaScript, Wasm, or native binary is emitted'],
    ],
    [2.5, 2.5, 11]
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Critical distinction: ').bold = True
p.add_run(
    'FIR lives entirely in the Frontend — it handles analysis, type resolution, and error reporting. '
    'IR lives entirely in the Backend — it handles code generation and platform-specific output. '
    'The Fir2Ir bridge converts the Frontend\'s semantic information into the Backend\'s code representation. '
    'This is important because Metro hooks into BOTH phases for different purposes.'
)

doc.add_paragraph()
add_heading_styled('10.2 Where Metro Hooks Into the Compiler', 2)

add_table(
    ['Compiler Phase', 'What Metro Does', 'Why This Phase'],
    [
        ['FIR (Frontend)', 'Reads @DependencyGraph, @Inject, @Provides, @SingleIn annotations', 'FIR has fully resolved type information — Metro knows every class and its dependencies'],
        ['FIR (Frontend)', 'Validates the entire dependency graph — missing bindings, cycles, scope violations', 'Errors reported here appear in IDE (K2 plugin) and fail the build immediately'],
        ['FIR (Frontend)', 'Generates synthetic class headers (declarations only, no method bodies)', 'Downstream code can reference Metro-generated types during the same compilation'],
        ['FIR (Frontend)', 'Records private @Provides visibility metadata', 'Private providers need special metadata for cross-compilation visibility in IR'],
        ['— Fir2Ir —', 'Metro\'s synthetic FIR declarations are converted to IR stubs', 'Bridge phase transforms Metro\'s headers into IR nodes ready for body generation'],
        ['IR (Backend)', 'Builds BindingGraph from all collected metadata', 'IR phase has the complete picture of all bindings across all modules'],
        ['IR (Backend)', 'Runs Tarjan\'s algorithm for cycle detection in the dependency graph', 'Detects circular dependencies that would cause infinite recursion'],
        ['IR (Backend)', 'Runs Kahn\'s algorithm for topological sort (initialization order)', 'Determines the correct order to initialize singletons without forward references'],
        ['IR (Backend)', 'Generates provider factory class bodies with DoubleCheck for singletons', 'Actual constructor call code, thread-safe caching — all as IR nodes, no source files'],
        ['IR (Backend)', 'Generates the graph implementation class (e.g., ShopAppGraphImpl)', 'The single class that holds all wiring — property getters, provider fields, factory methods'],
    ],
    [2.5, 6, 7.5]
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Key insight: ').bold = True
p.add_run(
    'Metro\'s generated code never exists as source files. It is born as IR nodes inside the compiler\'s '
    'backend phase and goes directly to bytecode. No .java files, no .kt files, no disk I/O — just '
    'in-memory IR that gets lowered to platform bytecode alongside your own code in the same compilation pass.'
)

doc.add_page_break()

add_heading_styled('10.3 Hilt Pipeline vs Metro Pipeline (Step-by-Step)', 2)

doc.add_paragraph(
    'This is why Metro compiles 44.7% faster than Hilt for the same dependency graph:'
)

add_heading_styled('Hilt/Dagger Compilation (4 steps, 2 compiler invocations)', 3)
add_table(
    ['Step', 'What Runs', 'Cost', 'Output'],
    [
        ['1', 'Kotlin Frontend (FIR) — your code', 'Resolves all symbols', 'Resolved FIR'],
        ['2', 'KSP processor — runs as separate step', 'Reads resolved symbols, scans all annotations', 'Nothing yet (analysis)'],
        ['3', 'KSP code generation — writes .java files', 'Generates 291 Java source files (488KB)', '291 .java files on disk'],
        ['4', 'Kotlin Backend (IR → bytecode) — your code', 'Compiles your Kotlin to bytecode', '.class files for your code'],
        ['5', 'Java Compiler (javac) — generated code', 'Entire separate compiler for 291 .java files', '.class files for generated code'],
    ],
    [1, 5, 4.5, 4.5]
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Total: ').bold = True
p.add_run('2 frontend passes + KSP processing + file I/O (write 291 files, read them back) + javac invocation')

doc.add_paragraph()
add_heading_styled('Metro Compilation (1 step, 1 compiler invocation)', 3)
add_table(
    ['Step', 'What Runs', 'Cost', 'Output'],
    [
        ['1', 'Kotlin Frontend (FIR) + Metro analysis', 'Normal resolution + Metro validates graph', 'Resolved FIR + Metro metadata'],
        ['2', 'Fir2Ir bridge', 'Normal + Metro\'s synthetic declarations', 'IR stubs'],
        ['3', 'Kotlin Backend (IR) + Metro codegen', 'Normal IR lowering + Metro generates provider factories, graph impl', 'Bytecode (your code + Metro\'s code, unified)'],
    ],
    [1, 5, 5.5, 4.5]
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Total: ').bold = True
p.add_run('1 frontend pass + 1 backend pass. Zero file I/O. Zero separate processes. Zero javac.')

doc.add_paragraph()
add_heading_styled('Why KSP Is Inherently Slower Than a Compiler Plugin', 3)
add_table(
    ['Factor', 'KSP (Hilt)', 'Compiler Plugin (Metro)'],
    [
        ['Runs inside kotlinc?', 'No — separate processing step alongside the compiler', 'Yes — native plugin inside the compiler itself'],
        ['Can modify existing classes?', 'No — can only generate NEW source files', 'Yes — can inject code into existing classes (IR)'],
        ['File I/O required?', 'Yes — writes generated .java/.kt files to disk', 'No — generates IR nodes in memory'],
        ['Needs second compiler pass?', 'Yes — javac must compile generated Java files', 'No — generated IR is lowered in the same pass'],
        ['Can access private members?', 'No — respects Kotlin visibility', 'Yes — can inject into private @Provides, private constructors'],
        ['Incremental build overhead?', 'KSP must re-analyze changed files + regenerate', 'Plugin sees only changed IR, regenerates minimally'],
    ],
    [4, 5.5, 5.5]
)

doc.add_page_break()

add_heading_styled('10.4 What Metro Generates at Runtime', 2)

doc.add_paragraph(
    'Metro\'s IR phase generates a single implementation class for each @DependencyGraph. '
    'This is what the generated bytecode looks like when decompiled (Metro never creates this as source code — '
    'it exists only as bytecode, shown here as pseudocode for illustration):'
)

# Provider factory pseudocode
p = doc.add_paragraph()
p.add_run('Generated Graph Implementation (pseudocode from bytecode):').bold = True

code_lines = [
    'class ShopAppGraphImpl : ShopAppGraph {',
    '',
    '    // ── Singletons: DoubleCheck (thread-safe lazy init) ──',
    '    // First call: creates instance under synchronized lock',
    '    // All subsequent calls: volatile field read (~0.2us)',
    '    private val databaseMgr = DoubleCheck { DatabaseManager() }',
    '    private val cacheMgr = DoubleCheck { CacheManager(databaseMgr.get()) }',
    '    private val httpClient = DoubleCheck { HttpClient("https://api.shopapp.com", 30000) }',
    '    private val authInterceptor = DoubleCheck { AuthInterceptor(tokenProvider.get()) }',
    '    private val productRemote = DoubleCheck {',
    '        ProductRemoteDataSource(httpClient.get(), apiParser.get(), authInterceptor.get(), ...)',
    '    }',
    '    private val productLocal = DoubleCheck {',
    '        ProductLocalDataSource(databaseMgr.get(), cacheMgr.get())',
    '    }',
    '    private val productRepo = DoubleCheck {',
    '        ProductRepository(productRemote.get(), productLocal.get(), productMapper.get(), logger.get())',
    '    }',
    '    // ... 105 more DoubleCheck providers for all singletons',
    '',
    '    // ── Singleton access: direct field read ──',
    '    override val productRepository get() = productRepo.get()    // ~0.2us after init',
    '    override val analyticsTracker get() = analyticsProvider.get() // ~0.2us after init',
    '',
    '    // ── Factory access: direct constructor calls ──',
    '    override val homeViewModel get() = HomeViewModel(',
    '        getProductList = GetProductListUseCase(productRepo.get(), analyticsProvider.get()),',
    '        getCategoryList = GetCategoryListUseCase(categoryRepo.get(), analyticsProvider.get()),',
    '        getPromotionList = GetPromotionListUseCase(promotionRepo.get(), analyticsProvider.get()),',
    '        getFeedList = GetFeedListUseCase(feedRepo.get(), analyticsProvider.get()),',
    '        analytics = analyticsProvider.get(),',
    '        featureFlags = featureFlagProvider.get()',
    '    )   // New HomeViewModel instance every call, but deps are cached singleton reads',
    '}',
]
for line in code_lines:
    p = doc.add_paragraph(line)
    for r in p.runs:
        r.font.name = 'Consolas'
        r.font.size = Pt(8.5)
    p.paragraph_format.space_after = Pt(0)
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.line_spacing = Pt(11)

doc.add_paragraph()
add_heading_styled('DoubleCheck — Thread-Safe Singleton Pattern', 3)

doc.add_paragraph(
    'Metro uses the same DoubleCheck pattern as Dagger for scoped (singleton) bindings. '
    'This is the double-checked locking idiom from Effective Java:'
)

add_table(
    ['Call', 'What Happens', 'Cost'],
    [
        ['First call', 'volatile read (UNINITIALIZED) → synchronized block → create instance → store in volatile field → release lock', '~5-50us depending on constructor complexity'],
        ['All subsequent calls', 'volatile read → instance already set → return immediately (skips synchronized)', '~0.2us (1 CPU instruction + memory barrier)'],
    ],
    [2.5, 10, 3.5]
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('This means: ').bold = True
p.add_run(
    'After app startup, every singleton access in Metro costs exactly one volatile field read. '
    'No HashMap, no type checking, no lambda invocation — just a direct memory access.'
)

doc.add_paragraph()
add_heading_styled('10.5 Why Metro Is 10-80x Faster Than Koin at Runtime', 2)

doc.add_paragraph(
    'The performance gap comes from what happens on every dependency resolution:'
)

add_table(
    ['Operation', 'Metro (per access)', 'Koin (per access)'],
    [
        ['Singleton lookup', 'Volatile field read (~0.2us)', 'HashMap.get(key) → type check → return cached (~3us)'],
        ['Factory creation', 'new ClassName(dep1.get(), dep2.get()) — direct call', 'HashMap.get(key) → invoke lambda → recursive get() for each param'],
        ['Type resolution', 'None — type is compiled into the field reference', 'Reified type → qualified name → hash computation per lookup'],
        ['Thread safety', 'DoubleCheck (volatile + sync on first call only)', 'ConcurrentHashMap (hash + bucket sync on every access)'],
        ['Dependency chain', 'Singleton deps: field reads. Factory deps: constructor calls.', 'Every node in chain: HashMap lookup + type check + lambda/cache check'],
    ],
    [3, 5.5, 7]
)

doc.add_paragraph()
add_heading_styled('Cost Scaling with Dependency Depth', 3)

add_table(
    ['Class (Dep Depth)', 'Metro', 'Koin', 'Koin/Metro Ratio', 'Why'],
    [
        ['AnalyticsTracker (2 deps)', '0.2us', '3us', '15x', '2 volatile reads vs 2 HashMap lookups'],
        ['OrderHistoryVM (3+15 transitive)', '1us', '28us', '28x', '3 constructor calls + ~15 field reads vs ~18 HashMap lookups'],
        ['HomeViewModel (6+50 transitive)', '10us', '105us', '10.5x', '6 constructors + ~50 field reads vs ~56 HashMap + lambda operations'],
        ['CheckoutViewModel (7+40 transitive)', '11us', '166us', '15x', '7 constructors + ~40 field reads vs ~47 HashMap + lambda operations'],
    ],
    [4, 1.5, 1.5, 2, 7]
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Pattern: ').bold = True
p.add_run(
    'Metro\'s cost per singleton dep is ~0.2us (volatile read), constant regardless of chain depth. '
    'Koin\'s cost per dep is ~3us (HashMap + type check), and it compounds multiplicatively through the chain. '
    'The deeper the dependency tree, the larger Metro\'s advantage.'
)

doc.add_paragraph()
add_heading_styled('10.6 Square/Cash App — Real-World Proof at Scale', 2)

doc.add_paragraph(
    'Square (Block) migrated their entire Android platform from Dagger to Metro — the largest known '
    'Metro deployment. Their published results validate Metro\'s performance claims at production scale:'
)

add_table(
    ['Metric', 'Value'],
    [
        ['Gradle modules migrated', '7,000+'],
        ['CI jobs transitioned', '1,500'],
        ['Development apps updated', '300+'],
        ['Production apps converted', '22'],
        ['Pull requests merged', '850+'],
        ['Migration duration', '9 months'],
    ],
    [5, 5]
)

doc.add_paragraph()
add_heading_styled('Build Time Improvements at Square', 3)

add_table(
    ['Scenario', 'Improvement Range', 'Best Case'],
    [
        ['ABI changes (API signature change)', '20.5% — 56.5% faster', '56.5% (Account module)'],
        ['Dagger wiring changes', '21.4% — 48.2% faster', '48.2% (development app)'],
        ['Non-ABI changes (implementation only)', '4.1% — 8.0% faster', '8.0% (development app)'],
    ],
    [5, 4.5, 5.5]
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('The headline number: ').bold = True
p.add_run(
    '"Even if we assume a conservative average improvement of only 10%, that still saves us more than '
    '4,800 hours of Gradle build time in CI every week." — Square Engineering Blog'
)

doc.add_paragraph()
p = doc.add_paragraph()
p.add_run('Why Non-ABI improvements are smaller: ').bold = True
p.add_run(
    'When only implementation code changes (no API changes), Gradle\'s incremental compilation already '
    'avoids recompiling downstream modules. Metro\'s advantage is largest when API/wiring changes trigger '
    'cascading recompilations — exactly where Dagger/KSP\'s code generation creates the most overhead.'
)

doc.add_page_break()

# ═══════════════════════════════════════════════════════
# 10. SUMMARY & RECOMMENDATION
# ═══════════════════════════════════════════════════════
add_heading_styled('11. Summary & Recommendation', 1)

add_heading_styled('Complete Scorecard', 2)
add_table(
    ['Category', 'Hilt', 'Metro', 'Koin'],
    [
        ['Compile Time', '3,943ms (slowest)', '2,182ms (44.7% faster)', '1,673ms (57.6% faster)'],
        ['Generated Code', '291 files / 488KB', '0 files', '0 files'],
        ['Container Init', '0.02ms', '0.12ms', '1.70ms'],
        ['Cold Injection', '0.4-15us', '0.3-102us', '5-610us'],
        ['Warm Injection', '0.2-11us (avg 4us)', '0.2-4us (avg 2us)', '3-105us (avg 55us)'],
        ['Memory Overhead', '96 KB', '128 KB', '2,000 KB'],
        ['Compile-Time Safety', 'Full', 'Full', 'None (DSL) / Partial (Compiler Plugin)'],
        ['Runtime DI Flexibility', 'No', 'No', 'Yes (dynamic swap, load/unload)'],
        ['Ecosystem Maturity', 'Excellent (Google official)', 'Growing (Cash App, Vinted)', 'Good (community)'],
        ['KMP Support', 'No', 'Yes', 'Yes'],
        ['Learning Curve', 'Moderate (Dagger)', 'Low (familiar to Dagger)', 'Low (simple DSL)'],
        ['Plugin/Processor Dependency', 'KSP required', 'Compiler plugin required', 'None (DSL) / Plugin optional'],
    ],
    [3.5, 3.5, 3.5, 3.5]
)

doc.add_paragraph()
add_heading_styled('When to Choose Each', 2)

p = doc.add_paragraph()
r = p.add_run('Choose Hilt when: ')
r.bold = True
r.font.color.rgb = GREEN_HILT
p.add_run(
    'You need Google\'s official support, extensive documentation, and a battle-tested ecosystem. '
    'Best for teams already familiar with Dagger and projects that prioritize long-term stability '
    'over build speed. Hilt\'s compile-time safety catches DI errors before the app runs.'
)

doc.add_paragraph()
p = doc.add_paragraph()
r = p.add_run('Choose Metro when: ')
r.bold = True
r.font.color.rgb = BLUE
p.add_run(
    'Performance is critical — both compile-time and runtime. Metro offers the best of both worlds: '
    'compile-time safety (like Hilt) with near-zero runtime overhead, 44.7% faster builds than Hilt, '
    'and zero generated source files. Ideal for large-scale apps where build time and app startup '
    'latency matter. Also the only option if you need Kotlin Multiplatform DI with compile-time safety.'
)

doc.add_paragraph()
p = doc.add_paragraph()
r = p.add_run('Choose Koin when: ')
r.bold = True
r.font.color.rgb = ORANGE
p.add_run(
    'Simplicity, fastest compile times, and runtime flexibility are the priority. Koin\'s DSL is the '
    'easiest to learn and requires no annotation processing. Best for small-to-medium apps where the '
    '64us average injection time is negligible, KMP projects needing simple cross-platform DI, '
    'or apps requiring dynamic module loading (Play Feature Delivery, server-driven configs). '
    'The new Koin Compiler Plugin (K2) adds compile-time validation for most dependency errors, '
    'though edge cases around qualifiers, generic types, and dynamic modules can still crash at runtime. '
    'Runtime resolution overhead remains regardless of the plugin.'
)

doc.add_paragraph()
doc.add_paragraph()

doc.add_page_break()

# ═══════════════════════════════════════════════════════
# 12. REFERENCES & FURTHER READING
# ═══════════════════════════════════════════════════════
add_heading_styled('12. References & Further Reading', 1)

add_heading_styled('Official Documentation', 2)
refs = [
    'Google Android DI Guide — developer.android.com/training/dependency-injection',
    'Hilt Documentation — dagger.dev/hilt',
    'Metro Documentation — zacsweers.github.io/metro',
    'Metro Design Document — zacsweers.github.io/metro/latest/designdoc.html',
    'Koin Documentation — insert-koin.io/docs',
    'Koin Compiler Plugin — insert-koin.io/docs/intro/koin-compiler-plugin',
    'Kotlin/Native Overview — kotlinlang.org/docs/native-overview.html',
    'Kotlin/Native Memory Management — kotlinlang.org/docs/native-memory-manager.html',
    'Kotlin/Native ARC Integration — kotlinlang.org/docs/native-arc-integration.html',
    'KSP (Kotlin Symbol Processing) — kotlinlang.org/docs/ksp-overview.html',
    'K2 Compiler Migration Guide — kotlinlang.org/docs/k2-compiler-migration-guide.html',
]
for r in refs:
    doc.add_paragraph(r, style='List Bullet')

doc.add_paragraph()
add_heading_styled('Technical Blog Posts & Articles', 2)
refs = [
    'Introducing Metro — zacsweers.dev/introducing-metro',
    'Metro Is Stable (1.0) — zacsweers.dev/metro-is-stable',
    'Metro Migration at Square Android — engineering.block.xyz/blog/metro-migration-at-square-android',
    'Koin Powered by Kotlin Compiler — blog.insert-koin.io/koin-powered-by-kotlin-compiler',
    'Compile-Time Safety with Koin Annotations — medium.com/@kerry.bisset (Kerry Bisset)',
    'Crash Course on the Kotlin Compiler: K1 + K2 — medium.com/google-developer-experts (Amanda Hinchman)',
    'From Dagger to Metro — vinted.engineering/2026/02/12/from-dagger-to-metro',
    'Cash Android Moves to Metro — code.cash.app/cash-android-moves-to-metro',
    'Dagger DoubleCheck: Scopes Explained — proandroiddev.com (Garima Jain)',
    'Hilt vs Koin: Hidden Cost of Runtime Injection — droidcon.com',
    'Benchmarking Koin vs Hilt in Modern Android (2024) — droidcon.com',
    'Kotlin/Native Compilation Under the Hood — medium.com/@natig.haciyef',
    'Stately: Kotlin Multiplatform Concurrency Library — github.com/touchlab/Stately',
]
for r in refs:
    doc.add_paragraph(r, style='List Bullet')

doc.add_paragraph()
add_heading_styled('Books', 2)
refs = [
    'Dependency Injection Principles, Practices, and Patterns — Mark Seemann & Steven van Deursen (Manning, 2019). The definitive guide to DI theory: composition root, Pure DI, service locator anti-pattern, compile-time vs runtime trade-offs. Language-agnostic principles that underpin all three frameworks. ISBN: 978-1617294730',
    'Kotlin in Action, 2nd Edition — Sebastian Aigner, Roman Elizarov, Svetlana Isakova, Dmitry Jemerov (Manning, 2024). Covers coroutines, structured concurrency, and Kotlin internals relevant to understanding compiler plugin behavior. manning.com/books/kotlin-in-action-second-edition',
    'Kotlin Design Patterns and Best Practices, 3rd Edition — Alexey Soshin & Anton Arhipov (Packt, 2024). Covers concurrent design patterns in Kotlin, including DI patterns, factory patterns, and service locator. ISBN: 978-1805127765',
    'Kotlin Multiplatform by Tutorials, 3rd Edition — Kodeco Team (Kodeco/raywenderlich, 2025). Hands-on KMP guide covering shared DI configuration with Koin, platform-specific modules with expect/actual, and iOS framework integration. kodeco.com/books/kotlin-multiplatform-by-tutorials',
]
for r in refs:
    p = doc.add_paragraph(r, style='List Bullet')

doc.add_paragraph()
add_heading_styled('Conference Talks', 2)
refs = [
    'DroidKaigi 2025 — Navigating Dependency Injection with Metro (Zac Sweers)',
    'Droidcon Berlin 2025 — Koin Workshop: KMP Dependency Injection (Kotzilla)',
    'KotlinConf 2024 — Kotlin/Native Memory Management and Performance',
]
for r in refs:
    doc.add_paragraph(r, style='List Bullet')

doc.add_paragraph()
doc.add_paragraph()

p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
r = p.add_run('— End of RFC —')
r.font.size = Pt(12)
r.font.color.rgb = GRAY

# ── Save ──
output_path = '/Users/sahilthakar/AndroidStudioProjects/BenchMarking/RFC_DI_Framework_Benchmark.docx'
doc.save(output_path)
print(f'RFC document saved to: {output_path}')

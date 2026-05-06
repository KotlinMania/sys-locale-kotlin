# sys-locale-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Fsys--locale--kotlin-blue.svg)](https://github.com/KotlinMania/sys-locale-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/sys-locale-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/sys-locale-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/sys-locale-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/sys-locale-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`1Password/sys-locale`](https://github.com/1Password/sys-locale).

**Original Project:** This port is based on [`1Password/sys-locale`](https://github.com/1Password/sys-locale). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `1Password/sys-locale`

> The text below is reproduced and lightly edited from [`https://github.com/1Password/sys-locale`](https://github.com/1Password/sys-locale). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs so they continue to resolve from this repository.

## sys-locale

[![crates.io version](https://img.shields.io/crates/v/sys-locale.svg)](https://crates.io/crates/sys-locale)
[![crate documentation](https://docs.rs/sys-locale/badge.svg)](https://docs.rs/sys-locale)
![MSRV](https://img.shields.io/badge/rustc-1.56+-blue.svg)
[![crates.io downloads](https://img.shields.io/crates/d/sys-locale.svg)](https://crates.io/crates/sys-locale)
![CI](https://github.com/1Password/sys-locale/workflows/CI/badge.svg)

A small and lightweight Rust library to get the current active locale on the system.

`sys-locale` is small library to get the current locale set for the system or application with the relevant platform APIs. The library is also `no_std` compatible, relying only on `alloc`, except on Linux and BSD.

Platform support currently includes:
- Android
- iOS (and derivatives such as watchOS, tvOS, and visionOS)
- macOS
- Linux, BSD, and other UNIX variations
- WebAssembly, for the following platforms:
    - Inside of a web browser (via the `js` feature)
    - Emscripten (via the `UNIX` backend)
    Further support for other WASM targets is dependent on upstream
    support in those target's runtimes and specifications.
- Windows

```rust
use sys_locale::get_locale;

let locale = get_locale().unwrap_or_else(|| String::from("en-US"));

println!("The current locale is {}", locale);
```

## MSRV

The Minimum Supported Rust Version is currently 1.56.0. This will be bumped to a newer stable version of Rust when needed.

## Credits

Made with ❤️ by the [1Password](https://1password.com/) team.

#### License

<sup>
Licensed under either of <a href="LICENSE-APACHE">Apache License, Version
2.0</a> or <a href="LICENSE-MIT">MIT license</a> at your option.
</sup>

<br>

<sub>
Unless you explicitly state otherwise, any contribution intentionally submitted
for inclusion in this crate by you, as defined in the Apache-2.0 license, shall
be dual licensed as above, without any additional terms or conditions.
</sub>

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:sys-locale-kotlin:0.1.0-SNAPSHOT")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same MIT license as the upstream [`1Password/sys-locale`](https://github.com/1Password/sys-locale). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the sys-locale authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`1Password/sys-locale`](https://github.com/1Password/sys-locale) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.

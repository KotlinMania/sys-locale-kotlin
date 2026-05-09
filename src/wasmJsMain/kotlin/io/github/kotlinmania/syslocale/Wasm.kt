// port-lint: source src/wasm.rs
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.syslocale

private const val SEPARATOR = ''

/**
 * Returns the navigator.languages of the current global scope (window or worker)
 * joined by ``, or `null` if no navigator/languages are reachable.
 *
 * Simplified version of https://github.com/rustwasm/wasm-bindgen/blob/main/crates/js-sys/src/lib.rs,
 * which we can't use directly because it discards information about how it
 * retrieved the global.
 */
private fun navigatorLanguagesJoined(): String? = js(
    "(() => { var g = (typeof window !== 'undefined') ? window : ((typeof self !== 'undefined') ? self : ((typeof globalThis !== 'undefined') ? globalThis : null)); if (!g || !g.navigator || !g.navigator.languages) return null; return g.navigator.languages.join('\\u0001'); })()"
)

internal actual fun providerGet(): Iterator<String> {
    val joined = navigatorLanguagesJoined() ?: return emptyList<String>().iterator()
    if (joined.isEmpty()) return emptyList<String>().iterator()
    return joined.split(SEPARATOR).filter { it.isNotEmpty() }.iterator()
}

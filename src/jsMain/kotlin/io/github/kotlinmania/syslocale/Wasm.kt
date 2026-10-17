// port-lint: source wasm.rs
package io.github.kotlinmania.syslocale

private sealed interface GlobalType {
    val navigator: dynamic

    class Window(
        val window: dynamic,
    ) : GlobalType {
        override val navigator: dynamic get() = window.navigator
    }

    class Worker(
        val worker: dynamic,
    ) : GlobalType {
        override val navigator: dynamic get() = worker.navigator
    }
}

/**
 * Returns a handle to the global scope object.
 */
private fun global(): GlobalType? {
    val win: dynamic = js("(typeof window !== 'undefined') ? window : null")
    if (win != null) return GlobalType.Window(win)
    val self: dynamic = js("(typeof self !== 'undefined') ? self : null")
    if (self != null) return GlobalType.Worker(self)
    val gt: dynamic = js("(typeof globalThis !== 'undefined' && globalThis && globalThis.navigator) ? globalThis : null")
    if (gt != null) return GlobalType.Worker(gt)
    return null
}

private fun get(): Iterator<String> {
    val scope = global() ?: return emptyList<String>().iterator()
    val nav: dynamic = scope.navigator
    if (nav == null) return emptyList<String>().iterator()
    val languages: dynamic = nav.languages
    if (languages == null) return emptyList<String>().iterator()
    val len = (languages.length as Int)
    val out = mutableListOf<String>()
    for (i in 0 until len) {
        val v: dynamic = languages[i]
        if (v != null) out.add(v.toString())
    }
    return out.iterator()
}

internal actual fun providerGet(): Iterator<String> = get()

// port-lint: source src/unix.rs
package io.github.kotlinmania.syslocale

private const val LANGUAGE = "LANGUAGE"
private const val LC_ALL = "LC_ALL"
private const val LC_MESSAGES = "LC_MESSAGES"
private const val LANG = "LANG"

private interface EnvAccess {
    fun get(key: String): String?
}

private object StdEnv : EnvAccess {
    override fun get(key: String): String? = System.getenv(key)
}

internal actual fun providerGet(): Iterator<String> = innerGet(StdEnv)

private fun innerGet(env: EnvAccess): Iterator<String> {
    val locales = mutableListOf<String>()

    val languageVal = env.get(LANGUAGE)?.takeIf { it.isNotEmpty() }
    if (languageVal != null) {
        for (part in languageVal.split(':')) {
            val locale = posixToBcp47(part)
            if (locale !in locales) {
                locales.add(locale)
            }
        }
    }

    for (variable in arrayOf(LC_ALL, LC_MESSAGES, LANG)) {
        val value = env.get(variable)?.takeIf { it.isNotEmpty() } ?: continue
        val locale = posixToBcp47(value)
        if (locale !in locales) {
            locales.add(locale)
        }
    }

    return locales.iterator()
}

private fun posixToBcp47(locale: String): String {
    val out = StringBuilder()
    for (c in locale) {
        if (c == '.' || c == '@') break
        out.append(if (c == '_') '-' else c)
    }
    return out.toString()
}

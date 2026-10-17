// port-lint: source unix.rs
package io.github.kotlinmania.syslocale

import io.github.kotlinmania.libc.unix.getenv

internal const val LANGUAGE = "LANGUAGE"
internal const val LC_ALL = "LC_ALL"
internal const val LC_MESSAGES = "LC_MESSAGES"
internal const val LANG = "LANG"

/**
 * Environment variable access abstraction to allow testing without
 * mutating env variables.
 *
 * Use [StdEnv] to query the process environment.
 */
internal interface EnvAccess {
    /** Looks up an environment variable by `key`, returning its value or `null` when unset. */
    fun get(key: String): String?
}

/** Proxy to the process environment via `getenv`. */
internal object StdEnv : EnvAccess {
    override fun get(key: String): String? = getenv(key)
}

internal actual fun providerGet(): Iterator<String> = get()

internal fun get(): Iterator<String> = get(StdEnv)

/**
 * Retrieves a list of unique locales by checking specific environment variables
 * in a predefined order: `LANGUAGE`, `LC_ALL`, `LC_MESSAGES`, and `LANG`.
 *
 * The function first checks the `LANGUAGE` environment variable, which can contain
 * one or more locales separated by a colon (`:`). It then splits these values,
 * converts them from POSIX to BCP 47 format, and adds them to the list of locales
 * if they are not already included.
 *
 * Next, the function checks the `LC_ALL`, `LC_MESSAGES`, and `LANG` environment
 * variables. Each of these variables contains a single locale. If a locale is found,
 * and it is not empty, it is converted to BCP 47 format and added to the list if
 * it is not already included.
 *
 * The function ensures that locales are returned in the order of precedence
 * and without duplicates. The final list of locales is returned as an iterator.
 *
 * # Returns
 *
 * An iterator over the unique locales found in the environment variables.
 *
 * # Environment Variables Checked
 *
 * 1. `LANGUAGE` - Can contain multiple locales, each separated by a colon (`:`), highest priority.
 * 2. `LC_ALL` - Contains a single locale, high priority.
 * 3. `LC_MESSAGES` - Contains a single locale, medium priority.
 * 4. `LANG` - Contains a single locale, low priority.
 *
 * # Example
 *
 * ```
 * val locales = get(env).asSequence().toList()
 * for (locale in locales) {
 *     println("User's preferred locales: $locale")
 * }
 * ```
 */
internal fun get(env: EnvAccess): Iterator<String> {
    val locales = mutableListOf<String>()

    // LANGUAGE contains one or multiple locales separated by colon (':')
    val languageVal = env.get(LANGUAGE)?.takeIf { it.isNotEmpty() }
    if (languageVal != null) {
        for (part in languageVal.split(':')) {
            val locale = posixToBcp47(part)
            if (locale !in locales) {
                locales.add(locale)
            }
        }
    }

    // LC_ALL, LC_MESSAGES and LANG contain one locale
    for (variable in arrayOf(LC_ALL, LC_MESSAGES, LANG)) {
        val value = env.get(variable)?.takeIf { it.isNotEmpty() } ?: continue
        val locale = posixToBcp47(value)
        if (locale !in locales) {
            locales.add(locale)
        }
    }

    return locales.iterator()
}

/**
 * Converts a POSIX locale string to a BCP 47 locale string.
 *
 * This function processes the input locale by removing any character encoding
 * (the part after the `.` character) and any modifiers (the part after the `@` character).
 * It replaces underscores with hyphens to conform to BCP 47 formatting.
 *
 * If the locale is already in the BCP 47 format, no changes are made.
 */
internal fun posixToBcp47(locale: String): String {
    val out = StringBuilder()
    for (c in locale) {
        if (c == '.' || c == '@') break
        out.append(if (c == '_') '-' else c)
    }
    return out.toString()
}

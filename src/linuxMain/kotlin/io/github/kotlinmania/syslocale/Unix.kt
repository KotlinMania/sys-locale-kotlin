// port-lint: source src/unix.rs
@file:OptIn(ExperimentalForeignApi::class)

package io.github.kotlinmania.syslocale

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

internal const val LANGUAGE: String = "LANGUAGE"
internal const val LC_ALL: String = "LC_ALL"
internal const val LC_MESSAGES: String = "LC_MESSAGES"
internal const val LANG: String = "LANG"

/**
 * Environment variable access abstraction to allow testing without
 * mutating env variables.
 *
 * Use [StdEnv] to query the process environment.
 */
internal interface EnvAccess {
    /**
     * Returns the value of the environment variable named [key], or `null`
     * if no such variable is set.
     */
    fun get(key: String): String?
}

/** Proxy to the process environment. */
internal object StdEnv : EnvAccess {
    override fun get(key: String): String? = getenv(key)?.toKString()
}

internal fun get(): Sequence<String> = innerGet(StdEnv)

/**
 * Retrieves a list of unique locales by checking specific environment variables
 * in a predefined order: LANGUAGE, LC_ALL, LC_MESSAGES, and LANG.
 *
 * The function first checks the `LANGUAGE` environment variable, which can contain
 * one or more locales separated by a colon (`:`). It then splits these values,
 * converts them from [POSIX](https://pubs.opengroup.org/onlinepubs/9799919799/basedefs/V1_chap08.html)
 * to [BCP 47](https://www.ietf.org/rfc/bcp/bcp47.html) format, and adds them to the list of locales
 * if they are not already included.
 *
 * Next, the function checks the `LC_ALL`, `LC_MESSAGES`, and `LANG` environment
 * variables. Each of these variables contains a single locale. If a locale is found,
 * and it's not empty, it is converted to BCP 47 format and added to the list if
 * it is not already included.
 *
 * For more information check this issue: https://github.com/1Password/sys-locale/issues/14.
 *
 * The function ensures that locales are returned in the order of precedence
 * and without duplicates. The final list of locales is returned as a sequence.
 *
 * # Returns
 *
 * A sequence over the unique locales found in the environment variables.
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
 * ```kotlin
 * val locales: List<String> = innerGet(env).toList()
 * for (locale in locales) {
 *     println("User's preferred locales: $locale")
 * }
 * ```
 */
internal fun innerGet(env: EnvAccess): Sequence<String> {
    val locales = mutableListOf<String>()

    // LANGUAGE contains one or multiple locales separated by colon (':')
    val languageVal = env.get(LANGUAGE)?.takeIf { it.isNotEmpty() }
    if (languageVal != null) {
        for (part in languageVal.split(':')) {
            val locale = posixToBcp47(part)
            if (!locales.contains(locale)) {
                locales.add(locale)
            }
        }
    }

    // LC_ALL, LC_MESSAGES and LANG contain one locale
    for (variable in arrayOf(LC_ALL, LC_MESSAGES, LANG)) {
        val value = env.get(variable)?.takeIf { it.isNotEmpty() }
        if (value != null) {
            val locale = posixToBcp47(value)
            if (!locales.contains(locale)) {
                locales.add(locale)
            }
        }
    }

    return locales.asSequence()
}

/**
 * Converts a POSIX locale string to a BCP 47 locale string.
 *
 * This function processes the input [locale] by removing any character encoding
 * (the part after the `.` character) and any modifiers (the part after the `@` character).
 * It replaces underscores (`_`) with hyphens (`-`) to conform to BCP 47 formatting.
 *
 * If the locale is already in the BCP 47 format, no changes are made.
 *
 * Useful links:
 * - [The Open Group Base Specifications Issue 8 - 7. Locale](https://pubs.opengroup.org/onlinepubs/9799919799/basedefs/V1_chap07.html)
 * - [The Open Group Base Specifications Issue 8 - 8. Environment Variables](https://pubs.opengroup.org/onlinepubs/9799919799/basedefs/V1_chap08.html)
 * - [BCP 47 specification](https://www.ietf.org/rfc/bcp/bcp47.html)
 *
 * # Examples
 *
 * ```kotlin
 * val bcp47 = posixToBcp47("en-US") // already BCP 47
 * check(bcp47 == "en-US") // no changes
 *
 * val bcp47 = posixToBcp47("en_US")
 * check(bcp47 == "en-US")
 *
 * val bcp47 = posixToBcp47("ru_RU.UTF-8")
 * check(bcp47 == "ru-RU")
 *
 * val bcp47 = posixToBcp47("fr_FR@dict")
 * check(bcp47 == "fr-FR")
 *
 * val bcp47 = posixToBcp47("de_DE.UTF-8@euro")
 * check(bcp47 == "de-DE")
 * ```
 *
 * # TODO
 *
 * 1. Implement POSIX to BCP 47 modifier conversion (see https://github.com/1Password/sys-locale/issues/32).
 * 2. Optimize to avoid creating a new buffer (see https://github.com/1Password/sys-locale/pull/33).
 */
internal fun posixToBcp47(locale: String): String {
    val builder = StringBuilder()
    for (c in locale) {
        if (c == '.' || c == '@') break
        builder.append(if (c == '_') '-' else c)
    }
    return builder.toString()
}

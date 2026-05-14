// port-lint: source src/lib.rs
package io.github.kotlinmania.syslocale

/**
 * A library to safely and easily obtain the current locale on the system or for an application.
 *
 * This library currently supports the following platforms:
 * - Android
 * - iOS (and derivatives such as watchOS, tvOS, and visionOS)
 * - macOS
 * - Linux, BSD, and other UNIX variations
 * - WebAssembly on the web (via the JS-aware target)
 * - Windows
 */

internal expect fun providerGet(): Iterator<String>

/**
 * Returns the most preferred locale for the system or application.
 *
 * This is equivalent to `getLocales().next()` (the first entry).
 *
 * # Returns
 *
 * Returns a non-null `String` with a BCP 47 language tag inside.
 * If the locale couldn't be obtained, `null` is returned instead.
 *
 * # Example
 *
 * ```
 * val currentLocale = getLocale() ?: "en-US"
 * println("The locale is $currentLocale")
 * ```
 */
public fun getLocale(): String? {
    val iter = getLocales()
    return if (iter.hasNext()) iter.next() else null
}

/**
 * Returns the preferred locales for the system or application, in descending order of preference.
 *
 * # Returns
 *
 * Returns an `Iterator` with any number of BCP 47 language tags inside.
 * If no locale preferences could be obtained, the iterator will be empty.
 *
 * # Example
 *
 * ```
 * val locales = getLocales()
 * println("The most preferred locale is ${locales.asSequence().firstOrNull() ?: "en-US"}")
 * ```
 */
public fun getLocales(): Iterator<String> = providerGet()

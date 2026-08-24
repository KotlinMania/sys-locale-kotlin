// port-lint: source android.rs
package io.github.kotlinmania.syslocale

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

private const val PROP_VALUE_MAX = 92

private const val LOCALE_KEY = "persist.sys.locale"
private const val PRODUCT_LOCALE_KEY = "ro.product.locale"
private const val PRODUCT_LANGUAGE_KEY = "ro.product.locale.language"
private const val PRODUCT_REGION_KEY = "ro.product.locale.region"
private const val LANG_KEY = "persist.sys.language"
private const val COUNTRY_KEY = "persist.sys.country"
private const val LOCALEVAR_KEY = "persist.sys.localevar"

@OptIn(ExperimentalForeignApi::class)
private fun getProperty(name: String): String? =
    getenv(name)?.toKString()?.takeIf { it.isNotEmpty() }

private fun readLocale(): String? {
    getProperty(LOCALE_KEY)?.let { return it }

    val lang = getProperty(LANG_KEY)
    if (lang != null) {
        val country = getProperty(COUNTRY_KEY)
        if (country != null) {
            return "$lang-$country"
        }
        val variant = getProperty(LOCALEVAR_KEY)
        if (variant != null) {
            return "$lang-$variant"
        }
        return lang
    }

    getProperty(PRODUCT_LOCALE_KEY)?.let { return it }

    val prodLang = getProperty(PRODUCT_LANGUAGE_KEY)
    val prodRegion = getProperty(PRODUCT_REGION_KEY)
    if (prodLang != null && prodRegion != null) {
        return "$prodLang-$prodRegion"
    }

    return null
}

internal actual fun providerGet(): Iterator<String> {
    val loc = readLocale()
    return if (loc != null) listOf(loc).iterator() else emptyList<String>().iterator()
}

// port-lint: source android.rs
package io.github.kotlinmania.syslocale

import io.github.kotlinmania.libc.unix.getenv
import io.github.kotlinmania.libc.unix.linuxlike.android.PROP_VALUE_MAX

private const val LOCALE_KEY = "persist.sys.locale"
private const val PRODUCT_LOCALE_KEY = "ro.product.locale"
private const val PRODUCT_LANGUAGE_KEY = "ro.product.locale.language"
private const val PRODUCT_REGION_KEY = "ro.product.locale.region"
private const val LANG_KEY = "persist.sys.language"
private const val COUNTRY_KEY = "persist.sys.country"
private const val LOCALEVAR_KEY = "persist.sys.localevar"

private fun getProperty(name: String): String? =
    getenv(name)?.takeIf { it.isNotEmpty() && it.length <= PROP_VALUE_MAX }

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

private fun get(): Iterator<String> {
    val loc = readLocale()
    return if (loc != null) listOf(loc).iterator() else emptyList<String>().iterator()
}

internal actual fun providerGet(): Iterator<String> = get()

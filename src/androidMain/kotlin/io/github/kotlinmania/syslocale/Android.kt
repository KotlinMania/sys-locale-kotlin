// port-lint: source src/android.rs
package io.github.kotlinmania.syslocale

import android.os.LocaleList

// Upstream Android property keys, retained for traceability:
//   persist.sys.locale          -> LOCALE_KEY
//   ro.product.locale           -> PRODUCT_LOCALE_KEY
//   ro.product.locale.language  -> PRODUCT_LANGUAGE_KEY
//   ro.product.locale.region    -> PRODUCT_REGION_KEY
//   persist.sys.language        -> LANG_KEY        (Android 4.0 and below)
//   persist.sys.country         -> COUNTRY_KEY     (Android 4.0 and below)
//   persist.sys.localevar       -> LOCALEVAR_KEY   (Android 4.0 and below)
//
// On a Kotlin Multiplatform Android target the libc property API surfaced by
// `__system_property_get` is not part of the public SDK. Modern Android already
// merges those properties into `android.os.LocaleList`, which is the same value
// the upstream reader (`readLocale`, ported from AndroidRuntime.cpp#431) tries
// to recover.

// Ported from https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/core/jni/AndroidRuntime.cpp#431
private fun readLocale(): List<String> {
    val list = LocaleList.getDefault()
    val out = mutableListOf<String>()
    for (i in 0 until list.size()) {
        val tag = list[i].toLanguageTag()
        if (tag.isNotEmpty()) out.add(tag)
    }
    return out
}

internal actual fun providerGet(): Iterator<String> = readLocale().iterator()

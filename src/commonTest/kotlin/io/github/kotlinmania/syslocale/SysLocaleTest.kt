package io.github.kotlinmania.syslocale

import kotlin.test.Test
import kotlin.test.assertFalse

class SysLocaleTest {
    @Test
    fun canObtainLocale() {
        val locale = getLocale()
        if (locale != null) {
            assertFalse(locale.isEmpty(), "locale string was empty")
            assertFalse(locale.endsWith('\u0000'), "locale contained trailing NUL")
        }
        val locales = getLocales()
        var i = 0
        while (locales.hasNext()) {
            val l = locales.next()
            assertFalse(l.isEmpty(), "locale string $i was empty")
            assertFalse(l.endsWith('\u0000'), "locale $i contained trailing NUL")
            i++
        }
    }
}

// port-lint: source windows.rs
package io.github.kotlinmania.syslocale

import io.github.kotlinmania.windowssys.windows.win32.foundation.TRUE
import io.github.kotlinmania.windowssys.windows.win32.globalization.GetUserPreferredUILanguages
import io.github.kotlinmania.windowssys.windows.win32.globalization.MUI_LANGUAGE_NAME

private fun get(): Iterator<String> {
    val numLanguages = uintArrayOf(0u)
    val bufferLength = uintArrayOf(0u)

    // Calling this with null buffer will retrieve the required buffer length
    val firstSuccess =
        GetUserPreferredUILanguages(
            MUI_LANGUAGE_NAME,
            numLanguages,
            null,
            bufferLength,
        ) == TRUE
    if (!firstSuccess || bufferLength[0] == 0u) {
        return emptyList<String>().iterator()
    }

    val buffer = UShortArray(bufferLength[0].toInt())

    // Now that we have an appropriate buffer, we can query the names
    val result = mutableListOf<String>()
    val secondSuccess =
        GetUserPreferredUILanguages(
            MUI_LANGUAGE_NAME,
            numLanguages,
            buffer,
            bufferLength,
        ) == TRUE

    if (secondSuccess) {
        // Windows wrote the required length worth of UTF-16 into our buffer.
        // The buffer contains names split by null char (0), and ends with two null chars (00)
        val total = bufferLength[0].toInt()
        var start = 0
        var i = 0
        while (i < total) {
            if (buffer[i].toInt() == 0) {
                if (i > start) {
                    val chars = CharArray(i - start) { k -> buffer[start + k].toInt().toChar() }
                    result.add(chars.concatToString())
                }
                start = i + 1
            }
            i++
        }
    }

    return result.iterator()
}

internal actual fun providerGet(): Iterator<String> = get()

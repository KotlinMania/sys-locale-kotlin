// port-lint: source src/windows.rs
package io.github.kotlinmania.syslocale

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.windows.GetUserPreferredUILanguages
import platform.windows.MUI_LANGUAGE_NAME
import platform.windows.TRUE
import platform.windows.WCHARVar

@OptIn(ExperimentalForeignApi::class)
internal actual fun providerGet(): Iterator<String> = memScoped {
    val numLanguages = alloc<UIntVar>().apply { value = 0u }
    val bufferLength = alloc<UIntVar>().apply { value = 0u }

    // Calling this with null buffer will retrieve the required buffer length
    val firstSuccess = GetUserPreferredUILanguages(
        MUI_LANGUAGE_NAME.toUInt(),
        numLanguages.ptr,
        null,
        bufferLength.ptr,
    ) == TRUE
    if (!firstSuccess) {
        return@memScoped emptyList<String>().iterator()
    }

    val buffer = allocArray<WCHARVar>(bufferLength.value.toInt())

    // Now that we have an appropriate buffer, we can query the names
    val result = mutableListOf<String>()
    val secondSuccess = GetUserPreferredUILanguages(
        MUI_LANGUAGE_NAME.toUInt(),
        numLanguages.ptr,
        buffer,
        bufferLength.ptr,
    ) == TRUE

    if (secondSuccess) {
        // Windows wrote the required length worth of UTF-16 into our buffer.
        // The buffer contains names split by null char (0), and ends with two null chars (00)
        val total = bufferLength.value.toInt()
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

    result.iterator()
}

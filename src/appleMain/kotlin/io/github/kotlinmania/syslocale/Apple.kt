// port-lint: source src/apple.rs
package io.github.kotlinmania.syslocale

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.cValue
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import platform.CoreFoundation.CFArrayGetCount
import platform.CoreFoundation.CFArrayGetValueAtIndex
import platform.CoreFoundation.CFIndexVar
import platform.CoreFoundation.CFLocaleCopyPreferredLanguages
import platform.CoreFoundation.CFRange
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringGetBytes
import platform.CoreFoundation.CFStringGetLength
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.kCFStringEncodingUTF8

@OptIn(ExperimentalForeignApi::class)
internal actual fun providerGet(): Iterator<String> = iterator {
    val preferredLangs = getLanguages() ?: return@iterator
    try {
        val numLangs = preferredLangs.count
        var idx: Long = 0L

        // 0 to N-1 inclusive
        while (idx < numLangs) {
            val raw = CFArrayGetValueAtIndex(preferredLangs.array, idx)
            if (raw == null) {
                idx++
                continue
            }

            // The current index has been checked that its still within bounds of the array.
            // We don't retain the strings because we know we have total ownership of the backing array.
            val locale: CFStringRef = raw.reinterpret()
            idx++

            // `locale` is a valid CFString pointer because the array will always contain a value.
            val strLen = CFStringGetLength(locale)

            val rangeArg: CValue<CFRange> = cValue {
                location = 0
                length = strLen
            }

            val emitted = memScoped {
                val capacityVar = alloc<CFIndexVar>().apply { value = 0 }
                // - `locale` is a valid CFString
                // - The supplied range is within the length of the string.
                // - `capacity` is writable.
                // Passing NULL and `0` is correct for the buffer to get the
                // encoded output length.
                CFStringGetBytes(
                    locale,
                    rangeArg,
                    kCFStringEncodingUTF8,
                    0u,
                    false,
                    null,
                    0,
                    capacityVar.ptr,
                )

                val capacity = capacityVar.value
                // Guard against a zero-sized allocation, if that were to somehow occur.
                if (capacity == 0L) return@memScoped null

                // This is the number of bytes that will be written to
                // the buffer, not the number of codepoints they would contain.
                val buffer = allocArray<UByteVar>(capacity)

                val outLenVar = alloc<CFIndexVar>().apply { value = 0 }
                // - `locale` is a valid CFString
                // - The supplied range is within the length of the string.
                // - `buffer` is writable and has sufficent capacity to receive the data.
                // - `maxBufLen` is correctly based on `buffer`'s available capacity.
                // - `outLen` is writable.
                CFStringGetBytes(
                    locale,
                    rangeArg,
                    kCFStringEncodingUTF8,
                    0u,
                    false,
                    buffer,
                    capacity,
                    outLenVar.ptr,
                )

                // Sanity check that both calls to `CFStringGetBytes`
                // were equivalent. If they weren't, the system is doing
                // something very wrong...
                check(outLenVar.value <= capacity)

                // The system has written `outLen` elements, so they are
                // initialized and inside the buffer's capacity bounds.
                val outLen = outLenVar.value.toInt()
                val bytes = ByteArray(outLen) { i -> buffer[i].toByte() }

                // This should always contain UTF-8 since we told the system to
                // write UTF-8 into the buffer.
                bytes.decodeToString()
            }

            if (emitted != null) yield(emitted)
        }
    } finally {
        CFRelease(preferredLangs.array)
    }
}

@OptIn(ExperimentalForeignApi::class)
private class CFArrayHandle(val array: platform.CoreFoundation.CFArrayRef, val count: Long)

@OptIn(ExperimentalForeignApi::class)
private fun getLanguages(): CFArrayHandle? {
    // This function is safe to call and has no invariants. Any value inside the
    // array will be owned by us.
    val langs = CFLocaleCopyPreferredLanguages() ?: return null
    // The returned array is a valid CFArray object.
    val count = CFArrayGetCount(langs)
    return if (count != 0L) CFArrayHandle(langs, count) else {
        CFRelease(langs)
        null
    }
}

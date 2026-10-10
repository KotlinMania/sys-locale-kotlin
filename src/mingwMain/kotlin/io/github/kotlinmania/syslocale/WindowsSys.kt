// port-lint: source src/windows_sys.rs
@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.kotlinmania.syslocale

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UShortVar

// Bindings corresponding to upstream windows_sys.rs declarations.
internal typealias Bool = Int

internal const val MUI_LANGUAGE_NAME_VAL: UInt = 8u
internal typealias Pwstr = CPointer<UShortVar>

internal const val TRUE_VAL: Bool = 1

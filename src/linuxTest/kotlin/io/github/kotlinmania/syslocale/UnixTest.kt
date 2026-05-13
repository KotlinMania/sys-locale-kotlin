// port-lint: source src/unix.rs (tests module)
package io.github.kotlinmania.syslocale

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private class MockEnv : EnvAccess {
    private val map = HashMap<String, String>()
    operator fun set(key: String, value: String) {
        map[key] = value
    }

    override fun get(key: String): String? = map[key]
}

private const val BCP_47: String = "fr-FR"
private const val POSIX: String = "fr_FR"
private const val POSIX_ENC: String = "fr_FR.UTF-8"
private const val POSIX_MOD: String = "fr_FR@euro"
private const val POSIX_ENC_MOD: String = "fr_FR.UTF-8@euro"

class UnixTest {
    @Test
    fun parseIdentifier() {
        assertEquals(BCP_47, posixToBcp47(BCP_47))
        assertEquals(BCP_47, posixToBcp47(POSIX))
        assertEquals(BCP_47, posixToBcp47(POSIX_ENC))
        assertEquals(BCP_47, posixToBcp47(POSIX_MOD))
        assertEquals(BCP_47, posixToBcp47(POSIX_ENC_MOD))
    }

    @Test
    fun envGet() {
        fun case(
            env: MockEnv,
            language: String,
            lcAll: String,
            lcMessages: String,
            lang: String,
            expected: List<String>,
        ) {
            env[LANGUAGE] = language
            env[LC_ALL] = lcAll
            env[LC_MESSAGES] = lcMessages
            env[LANG] = lang
            assertEquals(expected, innerGet(env).toList())
        }

        val env = MockEnv()
        assertNull(innerGet(env).firstOrNull())

        // Empty
        case(env, "", "", "", "", emptyList())

        // Constants
        case(
            env,
            POSIX_ENC_MOD,
            POSIX_ENC,
            POSIX_MOD,
            POSIX,
            listOf(BCP_47),
        )

        // Only one variable
        case(env, "en_US", "", "", "", listOf("en-US"))
        case(env, "", "en_US", "", "", listOf("en-US"))
        case(env, "", "", "en_US", "", listOf("en-US"))
        case(env, "", "", "", "en_US", listOf("en-US"))

        // Duplicates
        case(env, "en_US", "en_US", "en_US", "en_US", listOf("en-US"))
        case(
            env,
            "en_US",
            "en_US",
            "ru_RU",
            "en_US",
            listOf("en-US", "ru-RU"),
        )
        case(
            env,
            "en_US",
            "ru_RU",
            "ru_RU",
            "en_US",
            listOf("en-US", "ru-RU"),
        )
        case(
            env,
            "en_US",
            "es_ES",
            "ru_RU",
            "en_US",
            listOf("en-US", "es-ES", "ru-RU"),
        )
        case(
            env,
            "en_US:ru_RU:es_ES:en_US",
            "es_ES",
            "ru_RU",
            "en_US",
            listOf("en-US", "ru-RU", "es-ES"),
        )

        // Duplicates with different case
        case(
            env,
            "en_US:fr_fr",
            "EN_US",
            "fR_Fr",
            "En_US",
            listOf("en-US", "fr-fr", "EN-US", "fR-Fr", "En-US"),
        )

        // More complicated cases
        case(
            env,
            "ru_RU:ru:en_US:en",
            "ru_RU.UTF-8",
            "ru_RU.UTF-8",
            "ru_RU.UTF-8",
            listOf("ru-RU", "ru", "en-US", "en"),
        )
        case(
            env,
            "fr_FR.UTF-8@euro:fr_FR.UTF-8:fr_FR:fr:en_US.UTF-8:en_US:en",
            "es_ES.UTF-8@euro",
            "fr_FR.UTF-8@euro",
            "fr_FR.UTF-8@euro",
            listOf("fr-FR", "fr", "en-US", "en", "es-ES"),
        )
        case(
            env,
            "",
            "es_ES.UTF-8@euro",
            "fr_FR.UTF-8@euro",
            "fr_FR.UTF-8@euro",
            listOf("es-ES", "fr-FR"),
        )
        case(
            env,
            "fr_FR@euro",
            "fr_FR.UTF-8",
            "en_US.UTF-8",
            "en_US.UTF-8@dict",
            listOf("fr-FR", "en-US"),
        )

        // Already BCP 47
        case(env, BCP_47, BCP_47, BCP_47, POSIX, listOf(BCP_47))
        case(
            env,
            "fr-FR",
            "es-ES",
            "de-DE",
            "en-US",
            listOf("fr-FR", "es-ES", "de-DE", "en-US"),
        )
    }
}

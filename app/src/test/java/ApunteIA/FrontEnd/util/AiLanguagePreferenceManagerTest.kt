package ApunteIA.FrontEnd.util

import org.junit.Assert.assertEquals
import org.junit.Test

class AiLanguagePreferenceManagerTest {

    @Test
    fun `AiLanguageOption fromString parses correctly and case insensitively`() {
        assertEquals(AiLanguageOption.SAME_AS_APP, AiLanguageOption.fromString(null))
        assertEquals(AiLanguageOption.SAME_AS_APP, AiLanguageOption.fromString(""))
        assertEquals(AiLanguageOption.SAME_AS_APP, AiLanguageOption.fromString("INVALID"))
        assertEquals(AiLanguageOption.ES, AiLanguageOption.fromString("ES"))
        assertEquals(AiLanguageOption.ES, AiLanguageOption.fromString("es"))
        assertEquals(AiLanguageOption.EN, AiLanguageOption.fromString("EN"))
        assertEquals(AiLanguageOption.EN, AiLanguageOption.fromString("en"))
        assertEquals(AiLanguageOption.PT, AiLanguageOption.fromString("PT"))
        assertEquals(AiLanguageOption.PT, AiLanguageOption.fromString("pt"))
        assertEquals(AiLanguageOption.IT, AiLanguageOption.fromString("IT"))
        assertEquals(AiLanguageOption.IT, AiLanguageOption.fromString("it"))
    }

    @Test
    fun `getEffectiveLanguageCode returns explicit codes for ES EN PT IT`() {
        val manager = AiLanguagePreferenceManager(null)

        manager.aiLanguageOption = AiLanguageOption.ES
        assertEquals("es", manager.getEffectiveLanguageCode())

        manager.aiLanguageOption = AiLanguageOption.EN
        assertEquals("en", manager.getEffectiveLanguageCode())

        manager.aiLanguageOption = AiLanguageOption.PT
        assertEquals("pt", manager.getEffectiveLanguageCode())

        manager.aiLanguageOption = AiLanguageOption.IT
        assertEquals("it", manager.getEffectiveLanguageCode())
    }

    @Test
    fun `change languages immediately without restart for PT IT EN without app restart`() {
        val manager = AiLanguagePreferenceManager(null)

        // PT seleccionado -> effective = pt
        manager.aiLanguageOption = AiLanguageOption.PT
        assertEquals("pt", manager.getEffectiveLanguageCode())

        // cambiar a IT -> effective = it inmediatamente
        manager.aiLanguageOption = AiLanguageOption.IT
        assertEquals("it", manager.getEffectiveLanguageCode())

        // cambiar a EN -> effective = en
        manager.aiLanguageOption = AiLanguageOption.EN
        assertEquals("en", manager.getEffectiveLanguageCode())
    }
}

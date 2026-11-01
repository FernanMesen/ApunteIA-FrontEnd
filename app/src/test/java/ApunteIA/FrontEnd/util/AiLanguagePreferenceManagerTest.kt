package ApunteIA.FrontEnd.util

import org.junit.Assert.assertEquals
import org.junit.Test

class AiLanguagePreferenceManagerTest {

    @Test
    fun `AiLanguageOption fromString parses correctly`() {
        assertEquals(AiLanguageOption.SAME_AS_APP, AiLanguageOption.fromString(null))
        assertEquals(AiLanguageOption.SAME_AS_APP, AiLanguageOption.fromString("INVALID"))
        assertEquals(AiLanguageOption.ES, AiLanguageOption.fromString("ES"))
        assertEquals(AiLanguageOption.EN, AiLanguageOption.fromString("EN"))
        assertEquals(AiLanguageOption.PT, AiLanguageOption.fromString("PT"))
        assertEquals(AiLanguageOption.IT, AiLanguageOption.fromString("IT"))
    }
}

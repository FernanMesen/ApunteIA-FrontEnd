package ApunteIA.FrontEnd.domain

import ApunteIA.FrontEnd.domain.util.IsoDates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class IsoDatesFormatTest {

    @Test
    fun `english uses month day year`() {
        assertEquals("MM/dd/yyyy", IsoDates.datePattern(Locale.ENGLISH))
        assertEquals("MM/dd/yyyy", IsoDates.datePattern(Locale.US))
    }

    @Test
    fun `spanish portuguese and italian use day month year`() {
        listOf("es", "pt", "it").forEach {
            assertEquals("dd/MM/yyyy", IsoDates.datePattern(Locale(it)))
        }
    }

    @Test
    fun `iso date is built from the date picker values`() {
        assertEquals("2026-11-04", IsoDates.isoDateOf(2026, 10, 4))   // noviembre = 10 (base 0)
        assertEquals("2026-01-09", IsoDates.isoDateOf(2026, 0, 9))
    }

    @Test
    fun `today is an iso date`() {
        assertTrue(IsoDates.todayIsoDate().matches(Regex("""\d{4}-\d{2}-\d{2}""")))
    }
}

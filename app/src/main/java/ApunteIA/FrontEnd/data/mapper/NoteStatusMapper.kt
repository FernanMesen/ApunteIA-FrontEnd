package ApunteIA.FrontEnd.data.mapper

import ApunteIA.FrontEnd.domain.model.NoteStatus

/**
 * Traduce el `status` crudo (String) del [ApunteIA.FrontEnd.data.datasource.model.NoteDto]
 * al enum de dominio [NoteStatus].
 */
fun String.toNoteStatus(): NoteStatus = NoteStatus.fromLabel(this)

package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.NoteDto

/**
 * Fuente de datos de notas. Hoy solo existe [NoteDataSourceImpl] leyendo de
 * [NoteProvider] (datos de ejemplo en memoria), pero al depender de esta
 * interfaz se puede agregar mañana un `NoteRemoteDataSource` (API) sin tocar
 * el repositorio ni capas superiores.
 */
interface NoteDataSource {
    suspend fun getNotes(): List<NoteDto>
}

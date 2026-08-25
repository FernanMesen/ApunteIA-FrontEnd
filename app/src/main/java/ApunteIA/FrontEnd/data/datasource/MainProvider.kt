package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.domain.model.RecentActivity
import ApunteIA.FrontEnd.domain.model.UserStats

object MainProvider {
    fun getUserStats(): UserStats = UserStats(
        userName = "Maya",
        streakDays = 12,
        progressPercent = 53,
        notesCount = 6,
        pendingTopicsCount = 6,
        quizzesDoneCount = 9
    )

    fun getRecentActivity(): List<RecentActivity> = listOf(
        RecentActivity("Escaneadas 3 páginas", "Mecanismos de Reacción", "Hace 2h"),
        RecentActivity("Cuestionario completado", "Estereoquímica (4/5)", "Ayer"),
        RecentActivity("Auditoría actualizada", "2 brechas nuevas", "Hace 2 días"),
        RecentActivity("Escaneadas 2 páginas", "Transporte en la Membrana Celular", "Hace 3 días")
    )
}

package ApunteIA.FrontEnd.domain.model

data class AuditSummary(
    val percentCovered: Int,
    val solidCount: Int,
    val weakCount: Int,
    val missingCount: Int
)
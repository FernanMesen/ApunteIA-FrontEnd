package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.AuditSummary
import ApunteIA.FrontEnd.domain.model.Topic
import ApunteIA.FrontEnd.domain.model.TopicStatus

class CalculateAuditSummaryUseCase {

    operator fun invoke(topics: List<Topic>): AuditSummary {
        if (topics.isEmpty()) {
            return AuditSummary(percentCovered = 0, solidCount = 0, weakCount = 0, missingCount = 0)
        }
        val solidCount = topics.count { it.status == TopicStatus.SOLID }
        val weakCount = topics.count { it.status == TopicStatus.WEAK }
        val missingCount = topics.count { it.status == TopicStatus.MISSING }
        val percentCovered = topics.sumOf { it.progress } / topics.size

        return AuditSummary(percentCovered, solidCount, weakCount, missingCount)
    }
}
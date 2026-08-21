package ApunteIA.FrontEnd.domain.usecase

import ApunteIA.FrontEnd.domain.model.Topic

class FilterTopicsUseCase {

    companion object {
        const val CATEGORY_ALL = "Todos"
    }

    operator fun invoke(topics: List<Topic>, category: String): List<Topic> {
        return if (category == CATEGORY_ALL) topics
        else topics.filter { it.className == category }
    }
}
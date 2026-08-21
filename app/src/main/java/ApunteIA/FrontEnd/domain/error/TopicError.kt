package ApunteIA.FrontEnd.domain.error

sealed class TopicError : Exception() {
    data object DataUnavailable : TopicError()
    data object Unknown : TopicError()
}
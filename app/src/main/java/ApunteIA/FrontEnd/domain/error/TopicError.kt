package ApunteIA.FrontEnd.domain.error

import ApunteIA.FrontEnd.R
import ApunteIA.FrontEnd.util.L10n

sealed class TopicError : Exception() {
    data object NoConnection : TopicError()
    data object DataUnavailable : TopicError()
    data object Unknown : TopicError()

    fun toUserMessage(): String = when (this) {
        is NoConnection -> L10n.getString(R.string.topic_error_connection)
        is DataUnavailable -> L10n.getString(R.string.topic_error_data)
        is Unknown -> L10n.getString(R.string.topic_error_unknown)
    }
}

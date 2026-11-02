package ApunteIA.FrontEnd

import android.app.Application
import ApunteIA.FrontEnd.util.L10n
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApunteIAApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        L10n.init(this)
    }
}

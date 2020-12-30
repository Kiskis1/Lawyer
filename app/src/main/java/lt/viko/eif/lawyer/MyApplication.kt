@file:Suppress("unused")

package lt.viko.eif.lawyer

import android.app.Application
import com.yariksoffice.lingver.Lingver

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Lingver.init(this, "en")
    }
}

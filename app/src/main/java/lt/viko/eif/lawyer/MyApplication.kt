@file:Suppress("unused")

package lt.viko.eif.lawyer

import android.app.Application
import androidx.lifecycle.MutableLiveData

class MyApplication : Application() {
    // override fun onCreate() {
    //     super.onCreate()
    //     Lingver.init(this, "en")
    // }

    companion object {
        val fcmToken = MutableLiveData<String>()
    }
}

package lt.viko.eif.lawyer.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class WorkingHours(
    var monday: String = "",
    var tuesday: String = "",
    var wednesday: String = "",
    var thursday: String = "",
    var friday: String = "",
    var saturday: String = "",
    var sunday: String = "",
) : Parcelable

package lt.viko.eif.lawyer.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.Locale
import java.util.UUID

@Parcelize
@Keep
data class Question(
    var description: String = "N/A",
    var country: String = "N/A",
    var city: String = "N/A",
    var phone: String = "N/A",
    var fullname: String = "N/A",
    var destination: String = "",
    var email: String = "N/A",
    var sender: String = "",
    var id: String = UUID.randomUUID().toString().replace("-", "")
        .toUpperCase(Locale.ENGLISH) + Date().time,
) : Parcelable

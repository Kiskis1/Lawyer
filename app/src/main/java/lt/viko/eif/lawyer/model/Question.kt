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
    var destination: User? = null,
    var sender: User? = null,
    var id: String = UUID.randomUUID().toString().replace("-", "")
        .uppercase(Locale.ENGLISH) + Date().time,
) : Parcelable

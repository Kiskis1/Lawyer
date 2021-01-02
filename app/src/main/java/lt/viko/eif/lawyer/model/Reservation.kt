package lt.viko.eif.lawyer.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.Locale
import java.util.UUID

@Parcelize
@Keep
data class Reservation(
    var id: String = UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ENGLISH),
    var date: String = "",
    var time: String = "",
    var lawyer: User? = null,
    var user: String = "",
    var userName: String = "",
    var inPerson: Boolean = true,
    var dateLawyer: String = "",
) : Parcelable

package fr.afpa.my_yuka.métier

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//The object returned by the http request
@Parcelize
data class JsonObject(val code: String, val product: ProductBody): Parcelable{

}
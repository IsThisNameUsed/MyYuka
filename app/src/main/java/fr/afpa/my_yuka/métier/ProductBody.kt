package fr.afpa.my_yuka.métier

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//Object that is contained in the json object returned by the http request
@Parcelize
data class ProductBody(val generic_name:String, val image_front_url:String,
                        val ingredients_text: String, var allergens_tags: List<String>,
                        val nova_group:String, val nutriscore_grade:String,
                        val ingredients_analysis_tags:List<String>, val manufacturing_places:String,
                        val brands_tags:List<String>, val countries_imported:String,
                        val brands: String): Parcelable

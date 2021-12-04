package fr.afpa.my_yuka.Api

import fr.afpa.my_yuka.métier.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface IOpenFoodFactsAPI {

    companion object {
        val ENDPOINT="https://world.openfoodfacts.org/api/v0/product/"
    }

    @GET("{codeBarre}.json")
    fun getProduct(@Path("codeBarre")codeBarre: String): Call<JsonObject>
}
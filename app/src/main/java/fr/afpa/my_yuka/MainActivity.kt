package fr.afpa.my_yuka


import android.annotation.SuppressLint

import android.content.ActivityNotFoundException
import android.content.Intent

import android.graphics.Bitmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import fr.afpa.my_yuka.Api.IOpenFoodFactsClientAPI
import fr.afpa.my_yuka.métier.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.text.method.ScrollingMovementMethod

class MainActivity : AppCompatActivity() {

    private var clientAPI = IOpenFoodFactsClientAPI()
    private var imageAnalizer = ImageAnalyzer()
    private lateinit var ingredientsTextView: TextView
    private lateinit var productImageView: ImageView
    private lateinit var novaGroupView: ImageView
    private lateinit var nutriscoreView : ImageView
    private lateinit var palmOilView : ImageView
    private lateinit var countriesView : TextView
    private lateinit var manufactureView : TextView
    private lateinit var manufacturePlacesView : TextView
    private lateinit var allergensView: TextView
    private lateinit var textInputView: TextView
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set buttons
        val scannerButton = findViewById<Button>(R.id.scanner_button)
        scannerButton.setOnClickListener{onClickScanner()}

        val rechercheButton = findViewById<Button>(R.id.research_button)
        rechercheButton.setOnClickListener{onClickResearch()}

        //Store views in variables                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          a                                                                                                                                                                                                                                                                                                                                                                                                                                                       ²²              12
        ingredientsTextView= findViewById(R.id.ingredient_text)
        productImageView=findViewById(R.id.product_image)
        novaGroupView=findViewById(R.id.nova_image)
        nutriscoreView=findViewById(R.id.nutriscore_image)
        palmOilView=findViewById(R.id.palme_image)
        manufacturePlacesView=findViewById(R.id.manufacturing_places_text)
        countriesView=findViewById(R.id.countries_text)
        manufactureView=findViewById(R.id.manufacturer_text)
        allergensView=findViewById(R.id.allergenes_liste_text)
        textInputView = findViewById<TextInputEditText>(R.id.textInput)

        //Activation of the scrollbar of the textView "ingredient_text"
        ingredientsTextView.movementMethod = ScrollingMovementMethod()
    }

    private fun onClickScanner() {
        imageAnalizer.resetRawValue()
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            //registerForActivityResult(ActivityResultContracts.TakePicture()){}
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    private fun onClickResearch()
    {
        getProduct(textInputView.text.toString())
    }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
        val imageBitmap = data?.extras?.get("data") as Bitmap
            imageAnalizer.analyze(imageBitmap, this)
            productImageView.setImageBitmap(imageBitmap)
        }
    }

    fun displayRawValue(rawValue: String) {
        ingredientsTextView.text = rawValue
    }

    fun callGetProduct(rawValue: String)
    {
        getProduct(rawValue)
    }

    private fun getProduct(productCode: String) {
        clientAPI.service.getProduct(productCode).enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val jsonObject = response.body()

                jsonObject?.let {
                    displayProduct(jsonObject)
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("REG", "Error : $t")
            }
        })
    }

    private fun displayNoResult() {
        productImageView.setImageDrawable(null)
        ingredientsTextView.text = ""
        allergensView.text = ""
        novaGroupView.setImageDrawable(null)
        nutriscoreView.setImageDrawable(null)
        palmOilView.setImageDrawable(null)
        countriesView.text = getString(R.string.originCountryTitle)
        manufacturePlacesView.text = getString(R.string.placeMadeInTitle)
        manufactureView.text = getString(R.string.makerNameTitle)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun displayProduct(jsonObject: JsonObject)
    {
        if(jsonObject.product.generic_name.isEmpty()) {
            displayNoResult()
            return
        }

        //Display product image
        Picasso.get().load(jsonObject.product.image_front_url).into(productImageView)

        //Display product ingredients
        ingredientsTextView.text = jsonObject.product.ingredients_text

        //Display allergens list
        var allergensString = ""

        for(string in jsonObject.product.allergens_tags) {
            var resultString = if(string[2]==':')
                string.slice(3 until string.length)
            else string

            allergensString += "$resultString / "
        }
        allergensString = allergensString.slice(0 until allergensString.length-3)
        allergensView.text = allergensString

        //Display nova group image
        novaGroupView.setImageResource(R.drawable.nova1)

        //Display nutriscore grade
        val drawable = when(jsonObject.product.nutriscore_grade) {
            "a" -> R.drawable.nutriscore_a
            "b" -> R.drawable.nutriscore_b
            "c" -> R.drawable.nutriscore_c
            "d" -> R.drawable.nutriscore_d
            "e" -> R.drawable.nutriscore_e
            else ->R.drawable.interrogation_point
        }
        nutriscoreView.setImageResource(drawable)

        if(jsonObject.product.ingredients_analysis_tags.contains("en:palm-oil"))
            palmOilView.setImageResource(R.drawable.palm_oil)
        else palmOilView.setImageResource(R.drawable.no_palm_oil)

        //Display countries of origin
        var countries = "Pays d'origine: "
        countries += jsonObject.product.countries_imported
        countriesView.text = countries

        //Display manufacture places
        var places = "Lieu(x) de fabrication: "
        places += jsonObject.product.manufacturing_places
        manufacturePlacesView.text = places

        //Display manufacturer
        var manufacturers="Fabricant(s): "
        for(string in jsonObject.product.brands_tags) {
            manufacturers += string
        }
        manufactureView.text = manufacturers
    }
}
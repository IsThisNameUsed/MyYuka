package fr.afpa.my_yuka

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class ImageAnalyzer {

    var rawValue  = ""

    fun resetRawValue(){
        rawValue=""
    }

    fun analyze(bitmap: Bitmap, mainActivity: MainActivity) {
        val image = InputImage.fromBitmap(bitmap, 0)

            //Set the scanner
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_EAN_8
                )
                .build()

            // Pass image to an ML Kit Vision API
            val scanner = BarcodeScanning.getClient(options)
            val result = scanner.process(image)
                .addOnSuccessListener {barcodes ->
                    Log.i("SCANNER SUCCESS", "lol")
                    for (barcode in barcodes) {
                        Log.i("SCANNER", "Raw Value scanned: "+barcode.rawValue)
                        Log.i("SCANNER", "Code Type: "+barcode.valueType)
                        rawValue = barcode.rawValue
                    }
                    mainActivity.callGetProduct(rawValue)
                    mainActivity.displayRawValue(rawValue)

                }
                .addOnFailureListener {
                    // Task failed with an exception
                    Log.e("SCANNER ERROR: ", it.message.toString())
                    rawValue = ""
                }

        if(result.exception != null)
            Log.e("task exception: ", result.exception.toString())

        Log.i("SCANNER: ", "raw value returned: " + rawValue.toString())
    }
}

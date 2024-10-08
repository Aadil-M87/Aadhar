package com.example.myapplication.aadhar_card

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException
import java.util.regex.Pattern

class AadhaarOCRHelper(private val context: Context, private val callback: (String, String, String, String) -> Unit) {

    fun processImage(imageUri: Uri) {
        try {
            // Pass the context instead of contentResolver here
            val image = InputImage.fromFilePath(context, imageUri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val extractedText = visionText.text
                    processAadhaarText(extractedText)
                }
                .addOnFailureListener { e ->
                    Log.e("AadhaarOCR", "Text recognition failed", e)
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun processAadhaarText(extractedText: String) {
        val namePattern = Pattern.compile("(Name|नाम)[:\\s]+([A-Za-z ]+)")
        val sexPattern = Pattern.compile("(Male|Female|पुरुष|महिला)")
        val dobPattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}")
        val aadhaarPattern = Pattern.compile("\\d{4}\\s\\d{4}\\s\\d{4}")

        val nameMatcher = namePattern.matcher(extractedText)
        val sexMatcher = sexPattern.matcher(extractedText)
        val dobMatcher = dobPattern.matcher(extractedText)
        val aadhaarMatcher = aadhaarPattern.matcher(extractedText)

        val name = if (nameMatcher.find()) nameMatcher.group(2) else "Not Found"
        val sex = if (sexMatcher.find()) sexMatcher.group() else "Not Found"
        val dob = if (dobMatcher.find()) dobMatcher.group() else "Not Found"
        val aadhaar = if (aadhaarMatcher.find()) aadhaarMatcher.group() else "Not Found"

        // Trigger the callback to pass the data back to MainActivity
        callback(name, sex, dob, aadhaar)
    }
}

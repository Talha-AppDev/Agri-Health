package com.example.agrihealth

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class DelectedActivity : AppCompatActivity() {
    private lateinit var resultTextView: TextView
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view to your layout
        setContentView(R.layout.activity_delected)

        resultTextView = findViewById(R.id.resultTextView)
        imageView = findViewById(R.id.imageView)

        // Retrieve image from intent extras (either a Bitmap or a URI)
        val bitmap: Bitmap? = if (intent.hasExtra("IMAGE_BITMAP")) {
            // If the image came from the camera, it’s already a Bitmap.
            intent.getParcelableExtra("IMAGE_BITMAP")
        } else if (intent.hasExtra("IMAGE_URI")) {
            // If the image came from the gallery, convert the URI to a Bitmap.
            val uriString = intent.getStringExtra("IMAGE_URI")
            uriString?.let {
                val uri = Uri.parse(it)
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        } else {
            null
        }

        if (bitmap != null) {
            // Show the image in the ImageView
            imageView.setImageBitmap(bitmap)
            // Classify the image and update the UI with the results.
            classifyImage(bitmap)
        } else {
            resultTextView.text = "No image data received."
        }
    }

    private fun classifyImage(bitmap: Bitmap) {
        val classifier = DiseaseClassifier(this)
        // Run the classifier in a coroutine since classifyPlantDisease is a suspend function.
        lifecycleScope.launch {
            val (label, confidence) = classifier.classifyPlantDisease(bitmap)
            resultTextView.text = "Disease: $label\nConfidence: ${(confidence * 100).toInt()}%"
            classifier.close()  // Release model resources when done.
        }
    }
}

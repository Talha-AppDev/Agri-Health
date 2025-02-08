package com.example.agrihealth

import WheatFragment
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.agrihealth.databinding.ActivityGetimgBinding

class GetimgActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGetimgBinding

    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout
        binding = ActivityGetimgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the fragment dynamically
        val fragmentType = intent.getStringExtra("FRAGMENT_TYPE")
        val fragment: Fragment = when (fragmentType) {
            "WheatFragment" -> WheatFragment()
            else -> throw IllegalArgumentException("Invalid fragment type")
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.cvbanner, fragment)
            .commit()

        // Open Camera
        binding.camera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }

        // Open Gallery
        binding.gallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val photo: Bitmap = data?.extras?.get("data") as Bitmap
                    showImagePreviewDialog(photo = photo)
                }

                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri: Uri? = data?.data
                    selectedImageUri?.let {
                        showImagePreviewDialog(uri = it)
                    }
                }
            }
        }
    }

    // Function to preview image in a dialog and handle user confirmation
    private fun showImagePreviewDialog(photo: Bitmap? = null, uri: Uri? = null) {
        val dialogView = layoutInflater.inflate(R.drawable.dialog_image_preview, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.previewImageView)

        // Set the image
        if (photo != null) {
            imageView.setImageBitmap(photo)
        } else if (uri != null) {
            imageView.setImageURI(uri)
        }

        // Show the dialog
        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                // Proceed with the selected image
                dialog.dismiss()
                moveToNextStep(photo = photo, uri = uri)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    // Function to handle the next step with the selected image
    private fun moveToNextStep(photo: Bitmap? = null, uri: Uri? = null) {
        // Example: Pass the selected image to another activity
        val intent = Intent(this, DelectedActivity::class.java)
        if (photo != null) {
            // For camera images, save the bitmap as needed or handle it
        } else if (uri != null) {
            intent.putExtra("IMAGE_URI", uri.toString())
        }
        startActivity(intent)
    }

    // Accept color as an integer and set it as the background color
    fun setColor(color_x: Int, color_y: Int) {
        binding.camera.setCardBackgroundColor(color_x)
        binding.gallery.setCardBackgroundColor(color_x)
        binding.tvCamera.setTextColor(color_y)
        binding.tvGallery.setTextColor(color_y)
    }
}

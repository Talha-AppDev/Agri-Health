package com.example.agrihealth

import android.content.Context
import android.graphics.Bitmap
import com.example.agrihealth.ml.ModelNew
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DiseaseClassifier(context: Context) {

    // Instantiate the TFLite model (generated from your .tflite file)
    private val model = ModelNew.newInstance(context)

    // Define labels; if you have metadata in your model, you can load these dynamically.
    private val labels = listOf(
        "Aphid", "Black Rust", "Blast", "Brown Rust",
        "Common Root Rot", "Fusarium Head Blight", "Healthy",
        "Leaf Blight", "Mildew", "Mite", "Septoria", "Smut",
        "Stem fly", "Tan spot", "Yellow Rust"
    )

    /**
     * Classify the plant disease in the given [bitmap].
     *
     * @param bitmap The input image.
     * @return A pair containing the predicted label and the associated confidence.
     */
    suspend fun classifyPlantDisease(bitmap: Bitmap): Pair<String, Float> {
        return withContext(Dispatchers.IO) {
            try {
                // Preprocess the bitmap into a ByteBuffer
                val byteBuffer = preprocessImage(bitmap)
                // Create a TensorBuffer with the expected input shape and type.
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 255, 255, 3), DataType.FLOAT32)
                inputFeature0.loadBuffer(byteBuffer)

                // Run model inference.
                val outputs = model.process(inputFeature0)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer
                val probabilities = outputFeature0.floatArray

                // Identify the index with the maximum probability.
                val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
                val confidence = if (maxIndex != -1) probabilities[maxIndex].coerceIn(0f, 1f) else 0f
                val label = if (maxIndex in labels.indices) labels[maxIndex] else "Unknown"

                label to confidence
            } catch (e: Exception) {
                "Error: ${e.message}" to -1f
            }
        }
    }

    /**
     * Preprocess the [bitmap] to convert it into a ByteBuffer that the model can accept.
     *
     * The expected model input is a 255x255 image with 3 channels (RGB) in FLOAT32 format.
     * Here we resize the image, extract its pixels, normalize them to [0, 1], and write
     * them in the order R, G, B.
     *
     * @param bitmap The source image.
     * @return A ByteBuffer containing the preprocessed image.
     */
    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val inputSize = 255
        val inputChannels = 3
        // Allocate a direct ByteBuffer with enough space for 1 x 255 x 255 x 3 float values.
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * inputChannels)
        byteBuffer.order(ByteOrder.nativeOrder())

        // Resize the bitmap to the expected input dimensions.
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false)
        val intValues = IntArray(inputSize * inputSize)
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.width, 0, 0, scaledBitmap.width, scaledBitmap.height)

        // Iterate over all pixels and extract RGB channels.
        // Here we normalize each channel value to [0, 1] by dividing by 255.
        for (pixel in intValues) {
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }

        // Rewind the buffer to ensure it's read from the beginning during inference.
        byteBuffer.rewind()
        return byteBuffer
    }

    /**
     * Closes the model and releases its resources.
     */
    fun close() {
        model.close()
    }
}

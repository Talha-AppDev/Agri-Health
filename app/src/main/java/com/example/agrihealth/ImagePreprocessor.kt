package com.example.agrihealth
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

object ImagePreprocessor {

    // Configure based on your model's input requirements
    private const val INPUT_SIZE = 255
    private val dataType = DataType.FLOAT32

    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
        .add(NormalizeOp(0f, 255f)) // Adjust if model expects different normalization
        .build()

    fun preprocess(bitmap: Bitmap): TensorImage {
        return imageProcessor.process(TensorImage(dataType).apply { load(bitmap) })
    }
}
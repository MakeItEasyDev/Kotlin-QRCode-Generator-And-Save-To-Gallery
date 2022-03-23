package com.kotlin.qrcodegenerator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var qrImage: Bitmap? = null
    val EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_save.setOnClickListener(this)
        btn_generateQR.setOnClickListener(this)

        if (!checkPermissionForExternalStorage()) {
            requestPermissionForExternalStorage()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_generateQR -> {
                if (input_text.text.toString().isNotEmpty()) {
                    generateQRCode()
                } else {
                    input_text.error = "This field is required"
                }
            }
            R.id.btn_save -> {
                if (!checkPermissionForExternalStorage()) {
                    Toast.makeText(this, "External storage permission needed.", Toast.LENGTH_SHORT).show()
                } else {
                    if (qrImage != null)
                        saveImage(qrImage!!)
                }
            }
        }
    }

    private fun checkPermissionForExternalStorage(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionForExternalStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "External storage permission needed", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE), EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
        }
    }

    fun getTimeStamp(): String? {
        val tsLong = System.currentTimeMillis() / 1000
        return tsLong.toString()
    }

    fun generateQRCode() {
        qrImage = net.glxn.qrgen.android.QRCode.from(input_text.text.toString()).bitmap()
        if (qrImage != null) {
            imageView_qrCode.setImageBitmap(qrImage)
            btn_save.visibility = View.VISIBLE
        }
    }

    fun saveImage(image: Bitmap): String {
        var savedImagePath: String? = null
        val imageFileName = "QR" + getTimeStamp() + ".jpg"
        val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/QRGenerator")
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            val imageFile = File(storageDir, imageFileName)
            savedImagePath = imageFile.absolutePath
            try {
                val fOut = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val f = File(savedImagePath)
            val contentUrl = Uri.fromFile(f)
            mediaScanIntent.data = contentUrl
            sendBroadcast(mediaScanIntent)
            Toast.makeText(this, "QR Image saved to Into folder: QRGenerator in gallery", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show()
        }

        return savedImagePath!!
    }
}
























package com.vnpay.extension.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*

object VNPBitmapExt {
    private fun getScreenWidth(context: Context): Int {
        val displays = (context
            .applicationContext.getSystemService(
                Context.WINDOW_SERVICE
            ) as WindowManager)
            .defaultDisplay
        return displays.width
    }

    private fun getScreenHeight(context: Context): Int {
        val displays = (context
            .applicationContext.getSystemService(
                Context.WINDOW_SERVICE
            ) as WindowManager)
            .defaultDisplay
        return displays.height
    }

    fun convertBitmapPath(context: Context?, f: String?): Bitmap? {
        try {
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f), null, o)
            // Find the correct scale value. It should be the power of 2.
            val minWScreen = getScreenWidth(context!!)
            val minHScreen = getScreenHeight(context)
            var REQUIRED_SIZE = 120
            val height_tmp = o.outWidth
            val width_tmp = o.outHeight
            val scaleW = width_tmp.toDouble() / minWScreen.toDouble()
            val scaleH = height_tmp.toDouble() / minHScreen.toDouble()
            var scale = 1.0
            // scale = (scaleW + scaleH) / 2;
            scale = Math.max(scaleW, scaleH)
            if (scale == 0.0) scale = 1.0
            val max = Math.max(minWScreen, minHScreen)
            REQUIRED_SIZE = if (max > 720) 960 else {
                720
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale.toInt()
            o2.inPreferredConfig = Bitmap.Config.ARGB_8888
            var oriBitmap = BitmapFactory.decodeStream(
                FileInputStream(f), null, o2
            )
            oriBitmap = getBitmapBySize(
                oriBitmap,
                REQUIRED_SIZE
            )
            return oriBitmap
        } catch (e: FileNotFoundException) {

        }
        return null
    }

    fun getBitmapBySize(
        oriBitmap: Bitmap?,
        maxSize: Int,
    ): Bitmap? {
        var oriBitmap = oriBitmap
        return if (oriBitmap == null) oriBitmap else try {
            var _sWidth = 0
            var _sHeight = 0
            var width = oriBitmap.width
            var height = oriBitmap.height
            // int max = Math.max(width, height);
            _sWidth = width
            _sHeight = height
            if (width < height) {
                height = maxSize
                width = height * _sWidth / _sHeight
            } else {
                width = maxSize
                height = width * _sHeight / _sWidth
            }
            oriBitmap = Bitmap.createScaledBitmap(
                oriBitmap, width, height,
                true
            )
            oriBitmap
        } catch (ex: Exception) {

            null
        }
    }

    fun getBitmapFromPath(context: Context, uri: Uri): Bitmap? {
        val path = uri.toString()
        return if (path.startsWith("content://com.google.android.apps.photos.content")) {
            try {
                val `is` = context.contentResolver.openInputStream(uri)
                if (`is` != null) {
                    return this.getResizedBitmap(BitmapFactory.decodeStream(`is`))
                }
            } catch (var5: java.lang.Exception) {
                // LogVnp.e(var5);
            }
            null
        } else {
            this.getResizedBitmap(this.getBitmapFromUri(uri, context))
        }
    }

    fun Bitmap.toFile(context: Context, pathFile: String): File {
        val timestamp = System.currentTimeMillis()
        val file = File(context.externalCacheDir, pathFile)
        try {
            val outputStream = FileOutputStream(file)
            this.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

    private fun getResizedBitmap(bm: Bitmap?): Bitmap {
        var bm: Bitmap = bm!!
        val width = bm.width
        val height = bm.height
        val wd = 1360
        return if (width < wd) {
            bm
        } else {
            val scaleWidth = wd.toFloat() / width.toFloat()
            val scaleHeight = scaleWidth * height.toFloat() / width.toFloat()
            val matrix = Matrix()
            matrix.postScale(scaleWidth, scaleHeight)
            bm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
            bm
        }
    }

    private fun getResizedBitmap(
        bm: Bitmap,
        newWidth: Int,
        newHeight: Int,
        isNecessaryToKeepOrig: Boolean
    ): Bitmap {
        val width = bm.width;
        val height = bm.height;
        val scaleWidth: Float = (newWidth.toFloat()) / width
        val scaleHeight: Float = (newHeight.toFloat()) / height
        //CREATE A MATRIX FOR THE MANIPULATION
        val matrix: Matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        if (!isNecessaryToKeepOrig) {
            bm.recycle();
        }
        return resizedBitmap;
    }

    private fun getBitmapFromUri(uri: Uri, context: Context): Bitmap? {
        return try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            image
        } catch (var6: java.lang.Exception) {
            null
        }
    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        try {
            drawable?.let {
                return if (drawable is BitmapDrawable) {
                    drawable.bitmap
                } else {
                    val bitmap = Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(bitmap)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                    bitmap
                }
            }

        } catch (e: java.lang.Exception) {
            Log.wtf("EXX", e)
        }
        return null
    }


    fun savePhotoFromBitmap(context: Context, bitmap: Bitmap?): String? {
        try {
            val FILE_SAVE =
                Environment.getExternalStorageDirectory().absolutePath +
                        "/screenshot/"
            val mDir = File(FILE_SAVE)
            if (!mDir.exists()) mDir.mkdirs()
            var pathSaveImage: String? =
                FILE_SAVE + UUID.randomUUID().toString() + Calendar.getInstance().time + ".jpg"
            val mFile = File(pathSaveImage)
            val pathShare = if (mFile.exists()) {
                VNPFileExt.writeFile(pathSaveImage, bitmap)
                MediaStore.Images.Media.insertImage(
                    context.contentResolver,
                    pathSaveImage, "screenshot " + Calendar.getInstance().time, "drawing"
                )
            } else {
                MediaStore.Images.Media.insertImage(
                    context.contentResolver,
                    bitmap, "screenshot " + Calendar.getInstance().time, null
                )
            }
            return pathShare
        } catch (e: java.lang.Exception) {
            return null
        }
    }
    fun savePhotoFromView(context: Context,view: View){
        val bitmap = getBitmapFromView(view,context)
        savePhotoFromBitmap(context,bitmap)
    }

    private fun getBitmapFromView(view: View, context: Context): Bitmap {
        return Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888).apply {
            Canvas(this).apply {
                val bgDrawable = view.background
                if (bgDrawable != null) bgDrawable.draw(this) else drawColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.transparent
                    )
                )
                view.draw(this)
            }
        }
    }
}
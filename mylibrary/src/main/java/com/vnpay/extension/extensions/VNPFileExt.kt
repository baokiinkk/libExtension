package com.vnpay.extension.extensions

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Base64
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object VNPFileExt {
    suspend fun downloadFile(
        context: Context,
        url: String,
        fileName: String,
        BUFFER_SIZE: Int = 4096,
        fileDownloadState: FileDownloadState
    ) = withContext(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            fileDownloadState.onStarted()
        }
        try {
            val file = File(context.externalCacheDir, fileName)
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.readTimeout = 1500
            connection.connectTimeout = 1500
            connection.connect()
            val inputStream = BufferedInputStream(connection.inputStream, BUFFER_SIZE)
            val outputStream = FileOutputStream(file)
            var downloadedLength = 0
            val totalLength = connection.contentLength
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead = inputStream.read(buffer)
            while (bytesRead != -1) {
                outputStream.write(buffer, 0, bytesRead)
                downloadedLength += bytesRead
                val progress = if (totalLength > 0) {
                    (downloadedLength * 100 / totalLength)
                } else {
                    ((downloadedLength * 100) / downloadedLength + (4096))
                }
                withContext(Dispatchers.Main) {
                    fileDownloadState.onProgressing(progress)
                }
                bytesRead = inputStream.read(buffer)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
            connection.disconnect()
            withContext(Dispatchers.Main) {
                fileDownloadState.onCompleted(file)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                fileDownloadState.onError(e)
            }
        }
    }

    fun downloadFileAndOpenPdf(activity: Activity, base64: String, filename: String): Boolean {
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                writeToFile(activity, filename, base64)
            } else {
                val values = ContentValues()
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                values.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS
                )

                getRealPathFromURI(activity, MediaStore.Files.getContentUri("external"))
                val uri = activity.contentResolver.insert(
                    MediaStore.Files.getContentUri("external"),
                    values
                )
                val pdfAsBytes = Base64.decode(base64, 0)
                val outputStream: OutputStream? =
                    uri?.let { activity.contentResolver.openOutputStream(it) }
                outputStream?.write(pdfAsBytes)
                outputStream?.close()
                uri?.let { openPDF(activity, it) }
            }
            return true
        } catch (e: java.lang.Exception) {
            return false
        }
    }

    private fun getRealPathFromURI(context: Context?, contentUri: Uri?): String? {
        return getPathForV19AndUp(context, contentUri)
    }

    private fun getPathForV19AndUp(context: Context?, contentUri: Uri?): String? {
        try {
            val wholeID = DocumentsContract.getDocumentId(contentUri)
            val id =
                wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            val column = arrayOf("_data")
            val sel = "_id=?"
            val cursor = context?.contentResolver?.query(
                Images.Media.EXTERNAL_CONTENT_URI,
                column,
                sel,
                arrayOf(id),
                null as String?
            )
            var filePath: String? = ""
            val columnIndex = cursor!!.getColumnIndex(column[0])
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }
            cursor.close()
            return filePath
        } catch (var9: java.lang.Exception) {
        }
        return null
    }

    private fun writeToFile(activity: Activity, name: String, data: String) {
        try {
            val FILE_SAVE =
                Environment.getExternalStorageDirectory().absolutePath
            val mDir = File(FILE_SAVE)
            if (!mDir.exists()) mDir.mkdirs()
            val pathSaveImage = "$FILE_SAVE$name.pdf"
            writeFile(pathSaveImage, data)
            openPDF(activity, name)

        } catch (e: java.lang.Exception) {
        }
    }

    fun writeFile(fileName: String?, data: String) {
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(fileName)
            val pdfAsBytes = Base64.decode(data, 0)
            out.write(pdfAsBytes)
        } catch (e: java.lang.Exception) {
            Log.wtf("", e)
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                Log.wtf("", e)
            }
        }
    }

    fun writeFile(fileName: String?, bm: Bitmap?) {
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(fileName)
            bm?.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } catch (e: java.lang.Exception) {
            Log.wtf("EXC", e)
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
            }
        }
    }

    private fun openPDF(activity: Activity, fileName: String) {
        // Get the File location and file name.
        try {
            val file = File(Environment.getExternalStorageDirectory(), "Download/${fileName}.pdf")
            Log.d("pdfFIle", "" + file)
            // Get the URI Path of file.
            val uriPdfPath = FileProvider.getUriForFile(
                activity,
                activity.applicationContext.packageName.toString() + ".provider",
                file
            )
            Log.d("pdfPath", "" + uriPdfPath)
            // Start Intent to View PDF from the Installed Applications.
            val pdfOpenIntent = Intent(Intent.ACTION_VIEW)
            pdfOpenIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            pdfOpenIntent.clipData = ClipData.newRawUri("", uriPdfPath)
            pdfOpenIntent.setDataAndType(uriPdfPath, "application/pdf")
            pdfOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            activity.startActivity(pdfOpenIntent)
        } catch (activityNotFoundException: ActivityNotFoundException) {
        }
    }

    fun openPDF(activity: Activity, uri: Uri) {
        var uriPdfPath: Uri = uri
        val pdfOpenIntent = Intent(Intent.ACTION_VIEW)
        pdfOpenIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        pdfOpenIntent.clipData = ClipData.newRawUri("", uriPdfPath)
        pdfOpenIntent.setDataAndType(uriPdfPath, "application/pdf")
        pdfOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        try {
            activity.startActivity(pdfOpenIntent)
        } catch (activityNotFoundException: ActivityNotFoundException) {
        }
    }
}

interface FileDownloadState {
    fun onStarted()
    fun onProgressing(progress: Int) {}

    fun onCompleted(file: File)

    fun onError(exception: Exception)
}
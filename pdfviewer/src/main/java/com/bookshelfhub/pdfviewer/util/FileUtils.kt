package com.bookshelfhub.pdfviewer.util

import android.content.Context

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class FileUtils private constructor(){


    companion object {
        @Throws(IOException::class)
        fun fileFromAsset(context: Context, assetName: String): File {
            val outFile = File(context.cacheDir, "$assetName-pdfview.pdf")
            if (assetName.contains("/")) {
                outFile.getParentFile().mkdirs()
            }
            copy(context.assets.open(assetName), outFile)
            return outFile
        }

        @Throws(IOException::class)
        fun copy(inputStream: InputStream?, output: File?) {
            var outputStream: OutputStream? = null
            try {
                outputStream = FileOutputStream(output)
                var read = 0
                val bytes = ByteArray(1024)
                while (inputStream!!.read(bytes).also { read = it } != -1) {
                    outputStream.write(bytes, 0, read)
                }
            } finally {
                try {
                    inputStream?.close()
                } finally {
                    outputStream!!.close()
                }
            }
        }
    }
}
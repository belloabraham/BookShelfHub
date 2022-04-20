package com.bookshelfhub.bookshelfhub.helpers.utils
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

object IconUtil {

    fun getBitmap(context: Context, res: Int): Bitmap {
        return BitmapFactory.decodeResource(context.resources, res)
    }

    fun getBitmap(drawable: BitmapDrawable): Bitmap {
        return drawable.bitmap
    }

    fun getDrawable(context: Context, bitmap: Bitmap?): Drawable {
        return BitmapDrawable(context.resources, bitmap)
    }

    fun getDrawable(context: Context, @DrawableRes res: Int): Drawable? {
        return ContextCompat.getDrawable(context, res)
    }

    fun getBitmap(dataUrlImage: String): Bitmap? {
        val cleanDataUrlImage = dataUrlImage.replace("data:image/png;base64,", "")
            .replace("data:image/jpeg;base64,", "")
        val decodedString: ByteArray = Base64.decode(cleanDataUrlImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

}
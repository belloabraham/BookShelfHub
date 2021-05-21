package com.bookshelfhub.bookshelfhub.wrapper.tooltip

import android.content.Context
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.bookshelfhub.bookshelfhub.R
import com.skydoves.balloon.*

open class SkyDove (private val context:Context) {


    open fun showPhoneNumErrorBottom(anchorView:View, message:String, event: ()->Unit ){

        val balloon = showPhoneNumberError(message, event)
        balloon.showAlignBottom(anchorView)
    }

    private fun showPhoneNumberError(message:String, event: ()->Unit):Balloon{
        val balloon = createBalloon(context) {
            setArrowSize(15)
            setWidth(BalloonSizeSpec.WRAP)
            setHeight(BalloonSizeSpec.WRAP)
            setArrowPosition(0.89f)
            setArrowOrientation(ArrowOrientation.TOP)
            setCornerRadius(0f)
            setMarginRight(8)
            setAlpha(0.9f)
            setPadding(8)
            setText(message)
            setTextColorResource(R.color.white)
            setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.error_background))
            setTextSize(14f)
            setBackgroundColorResource(R.color.black)
            setBalloonAnimation(BalloonAnimation.ELASTIC)
            setOnBalloonDismissListener {
                event()
            }
            setLifecycleOwner(lifecycleOwner)
        }
        return balloon
    }

}
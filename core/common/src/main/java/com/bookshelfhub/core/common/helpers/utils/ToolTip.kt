package com.bookshelfhub.core.common.helpers.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.skydoves.balloon.*
import com.bookshelfhub.core.resources.R


class ToolTip (private val context:Context)  {

      fun showPhoneNumberError(message: String, event: () -> Unit): Balloon {
         return createBalloon(context) {
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
             setBackgroundDrawable(
                 ContextCompat.getDrawable(
                     context,
                     R.drawable.tooltip_error_background
                 )
             )
             setTextSize(14f)
             setBackgroundColorResource(R.color.black)
             setBalloonAnimation(BalloonAnimation.ELASTIC)
             setOnBalloonDismissListener {
                 event()
             }
             setLifecycleOwner(lifecycleOwner)
         }
     }

}
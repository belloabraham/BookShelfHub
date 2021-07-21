package com.bookshelfhub.pdfviewer.scroll

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bookshelfhub.pdfviewer.PDFView
import com.bookshelfhub.pdfviewer.R
import com.bookshelfhub.pdfviewer.util.Util


open class DefaultScrollHandle : RelativeLayout, ScrollHandle {

    private val HANDLE_LONG = 65
    private val HANDLE_SHORT = 40
    private val DEFAULT_TEXT_SIZE = 16

    private var relativeHandlerMiddle = 0f

    protected var textView: TextView? = null
    protected var mContext: Context? = null
    private var inverted = false
    private var pdfView: PDFView? = null
    private var currentPos = 0f

    private val mHandler = Handler()
    private val hidePageScrollerRunnable = Runnable { hide() }


    constructor(context: Context?) : super(context){
        this.mContext = context
    }

    constructor(context: Context?, inverted: Boolean) : super(context) {
        this.mContext = context
        this.inverted = inverted
        textView = TextView(context)
        visibility = INVISIBLE
        setTextColor(Color.BLACK)
        setTextSize(DEFAULT_TEXT_SIZE)

    }

    override fun setupLayout(pdfView: PDFView) {
        val align: Int
        val width: Int
        val height: Int
        val background: Drawable?
        // determine handler position, default is right (when scrolling vertically) or bottom (when scrolling horizontally)
        if (pdfView.isSwipeVertical()) {
            width = HANDLE_LONG
            height = HANDLE_SHORT
            if (inverted) { // left
                align = ALIGN_PARENT_LEFT
                background =
                    ContextCompat.getDrawable(context!!, R.drawable.default_scroll_handle_left)
            } else { // right
                align = ALIGN_PARENT_RIGHT
                background =
                    ContextCompat.getDrawable(context!!, R.drawable.default_scroll_handle_right)
            }
        } else {
            width = HANDLE_SHORT
            height = HANDLE_LONG
            if (inverted) { // top
                align = ALIGN_PARENT_TOP
                background =
                    ContextCompat.getDrawable(context!!, R.drawable.default_scroll_handle_top)
            } else { // bottom
                align = ALIGN_PARENT_BOTTOM
                background =
                    ContextCompat.getDrawable(context!!, R.drawable.default_scroll_handle_bottom)
            }
        }
        setBackground(background)
        val lp: LayoutParams = LayoutParams(Util.getDP(context!!, width), Util.getDP(context!!, height))
        lp.setMargins(0, 0, 0, 0)
        val tvlp =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        tvlp.addRule(CENTER_IN_PARENT, TRUE)
        addView(textView, tvlp)
        lp.addRule(align)
        pdfView.addView(this, lp)
        this.pdfView = pdfView
    }

    override fun destroyLayout() {
        pdfView?.removeView(this)
    }

    override fun setScroll(position: Float) {
        if (!shown()) {
            show()
        } else {
            handler.removeCallbacks(hidePageScrollerRunnable)
        }
        if (pdfView != null) {
            setPosition((if (pdfView!!.isSwipeVertical()) pdfView!!.height else pdfView!!.width) * position)
        }
    }


    private fun setPosition(posNo: Float) {
        var pos = posNo
        if (java.lang.Float.isInfinite(pos) || java.lang.Float.isNaN(pos)) {
            return
        }
        val pdfViewSize: Float = if (pdfView!!.isSwipeVertical()) {
            pdfView!!.height.toFloat()
        } else {
            pdfView!!.width.toFloat()
        }
        pos -= relativeHandlerMiddle
        if (pos < 0) {
            pos = 0f
        } else if (pos > pdfViewSize - Util.getDP(context!!, HANDLE_SHORT)) {
            pos = pdfViewSize - Util.getDP(context!!, HANDLE_SHORT)
        }
        if (pdfView!!.isSwipeVertical()) {
            y = pos
        } else {
            x = pos
        }
        calculateMiddle()
        invalidate()
    }

    private fun calculateMiddle() {
        val pos: Float
        val viewSize: Float
        val pdfViewSize: Float
        if (pdfView!!.isSwipeVertical()) {
            pos = y
            viewSize = height.toFloat()
            pdfViewSize = pdfView!!.height.toFloat()
        } else {
            pos = x
            viewSize = width.toFloat()
            pdfViewSize = pdfView!!.width.toFloat()
        }
        relativeHandlerMiddle = (pos + relativeHandlerMiddle) / pdfViewSize * viewSize
    }

    override fun hideDelayed() {
        handler.postDelayed(hidePageScrollerRunnable, 1000)
    }

    override fun setPageNum(pageNum: Int) {
        val text = pageNum.toString()
        if (textView!!.text != text) {
            textView!!.text = text
        }
    }

    override fun shown(): Boolean {
        return visibility == VISIBLE
    }

    override fun show() {
        visibility = VISIBLE
    }

    override fun hide() {
        visibility = INVISIBLE
    }

    fun setTextColor(color: Int) {
        textView!!.setTextColor(color)
    }

    /**
     * @param size text size in dp
     */
    fun setTextSize(size: Int) {
        textView!!.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size.toFloat())
    }

    private fun isPDFViewReady(): Boolean {
        return pdfView != null && pdfView!!.getPageCount() > 0 && !pdfView!!.documentFitsView()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isPDFViewReady()) {
            return super.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                pdfView!!.stopFling()
                handler.removeCallbacks(hidePageScrollerRunnable)
                currentPos = if (pdfView!!.isSwipeVertical()) {
                    event.rawY - y
                } else {
                    event.rawX - x
                }
                if (pdfView!!.isSwipeVertical()) {
                    setPosition(event.rawY - currentPos + relativeHandlerMiddle)
                    pdfView!!.setPositionOffset(relativeHandlerMiddle / height.toFloat(), false)
                } else {
                    setPosition(event.rawX - currentPos + relativeHandlerMiddle)
                    pdfView!!.setPositionOffset(relativeHandlerMiddle / width.toFloat(), false)
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (pdfView!!.isSwipeVertical()) {
                    setPosition(event.rawY - currentPos + relativeHandlerMiddle)
                    pdfView!!.setPositionOffset(relativeHandlerMiddle / height.toFloat(), false)
                } else {
                    setPosition(event.rawX - currentPos + relativeHandlerMiddle)
                    pdfView!!.setPositionOffset(relativeHandlerMiddle / width.toFloat(), false)
                }
                return true
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                hideDelayed()
                pdfView!!.performPageSnap()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

}
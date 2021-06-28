package com.bookshelfhub.bookshelfhub.view.about

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import java.util.*

class AutoFitGridLayout : ViewGroup {

    private var verticalSpace = 0

    private var horizontalSpace = 0

    private var columnCount = 2

    private var notGoneViewList: ArrayList<View>? = null

     constructor(context: Context?) : super(context)

     constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

     constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private fun refreshNotGoneChildList() {
        if (notGoneViewList == null) {
            notGoneViewList = ArrayList()
        }
        notGoneViewList!!.clear()
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                notGoneViewList!!.add(child)
            }
        }
    }

    fun getVerticalSpace(): Int {
        return verticalSpace
    }

    fun setVerticalSpace(verticalSpace: Int) {
        this.verticalSpace = verticalSpace
        requestLayout()
    }

    fun getHorizontalSpace(): Int {
        return horizontalSpace
    }

    fun setHorizontalSpace(horizontalSpace: Int) {
        this.horizontalSpace = horizontalSpace
        requestLayout()
    }

    fun getColumnCount(): Int {
        return columnCount
    }

    fun setColumnCount(columnCount: Int) {
        this.columnCount = columnCount
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        refreshNotGoneChildList()
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val childWidth =
            ((parentWidth - (columnCount - 1) * horizontalSpace * 1.0f) / columnCount + 0.5f).toInt()
        val childCount = notGoneViewList!!.size
        val line =
            if (childCount % columnCount == 0) childCount / columnCount else childCount / columnCount + 1
        var totalHeight = 0
        var childIndex = 0
        for (i in 0 until line) {
            var inlineHeight = 0
            for (j in 0 until columnCount) {
                childIndex = i * columnCount + j
                if (childIndex < childCount) {
                    val child = notGoneViewList!![childIndex]
                    var childWidthWithPadding = childWidth
                    if (j == 0) {
                        childWidthWithPadding += paddingRight
                    } else if (j == columnCount - 1) {
                        childWidthWithPadding += paddingLeft
                    }
                    measureChild(
                        child,
                        MeasureSpec.makeMeasureSpec(childWidthWithPadding, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    )
                    val totalInlineChildHeight = child.measuredHeight
                    if (totalInlineChildHeight > inlineHeight) {
                        inlineHeight = totalInlineChildHeight
                    }
                }
            }
            totalHeight += inlineHeight
            totalHeight += verticalSpace
        }
        totalHeight -= verticalSpace
        totalHeight += paddingTop + paddingBottom
        setMeasuredDimension(
            getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
            totalHeight
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childCount = notGoneViewList!!.size
        val line =
            if (childCount % columnCount == 0) childCount / columnCount else childCount / columnCount + 1
        var childIndex = 0
        var lastLeft = paddingLeft
        var lastTop = paddingTop
        for (i in 0 until line) {
            var inlineHeight = 0
            for (j in 0 until columnCount) {
                childIndex = i * columnCount + j
                if (childIndex < childCount) {
                    val child = notGoneViewList!![childIndex]
                    val childWidth = child.measuredWidth
                    val childHeight = child.measuredHeight
                    child.layout(lastLeft, lastTop, lastLeft + childWidth, lastTop + childHeight)
                    lastLeft += childWidth + horizontalSpace
                    val totalInlineChildHeight = child.measuredHeight
                    if (totalInlineChildHeight > inlineHeight) {
                        inlineHeight = totalInlineChildHeight
                    }
                }
            }
            lastLeft = paddingLeft
            lastTop += inlineHeight + verticalSpace
        }
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
        return p is LayoutParams
    }

    override fun generateDefaultLayoutParams(): LayoutParams{
        return LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams?): LayoutParams {
        return LayoutParams(p)
    }

    class LayoutParams : MarginLayoutParams {
        constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs) {}
        constructor(width: Int, height: Int) : super(width, height) {}
        constructor(source: ViewGroup.LayoutParams?) : super(source) {}
    }

}
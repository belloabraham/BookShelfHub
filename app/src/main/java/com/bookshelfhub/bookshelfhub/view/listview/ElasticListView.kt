package com.bookshelfhub.bookshelfhub.view.listview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.AbsListView
import android.widget.ListView


class ElasticListView: ListView {

    private var mDistanceY = 0.0f
    private var mDownY = 0.0f
    private var mFirstVisibleItem = 0
    private var mMotionPosition = 0
    private var mOutsideScrollListener: OnScrollListener? = null
    private var mPviotY = 0.0f
    private var mScaleHelper: ListScaleHelper? = null
    private var mScaleY = 1.0f
    private var mVisibleItemCount = 0
    private var scrollListener: InnerOnScrollListener? = null

    private inner class InnerOnScrollListener : OnScrollListener {
        private var mScrollState = 0
        override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
            if (scrollState == 0 && mScrollState == 2) {
                this@ElasticListView.startScaleAnimation()
            }
            mScrollState = scrollState
            this@ElasticListView.mOutsideScrollListener?.onScrollStateChanged(
                view,
                scrollState
            )

        }

        override fun onScroll(
            view: AbsListView,
            firstVisibleItem: Int,
            visibleItemCount: Int,
            totalItemCount: Int
        ) {
            this@ElasticListView.mFirstVisibleItem = firstVisibleItem
            this@ElasticListView.mVisibleItemCount = visibleItemCount
                this@ElasticListView.mOutsideScrollListener?.onScroll(
                    view,
                    firstVisibleItem,
                    visibleItemCount,
                    totalItemCount
                )
        }
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int,  defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }


    private fun init() {
        mScaleHelper = ListScaleHelper(context)
        scrollListener = InnerOnScrollListener()
        super.setOnScrollListener(scrollListener)
    }

    override fun setOnScrollListener(l: OnScrollListener?) {
        mOutsideScrollListener = l
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            0 -> {
                if (!mScaleHelper!!.isFinished()) {
                    mScaleHelper?.finish()
                }
                mDownY = ev.y
                mMotionPosition = pointToPosition(ev.x.toInt(), mDownY.toInt())
                mScaleY = 1.0f
                mDistanceY = 0.0f
            }
            1, 3 -> if (scaleY != 1.0f) {
                mScaleHelper?.onRelease(this, scaleY, mPviotY)
                mScaleY = 1.0f
                mDownY = 0.0f
                mDistanceY = 0.0f
                invalidate()
            }
            2 -> {
                if (mDownY == 0.0f) {
                    mDownY = ev.y
                    mScaleY = 1.0f
                }
                mDistanceY = ev.y - mDownY
                if (childCount > 0 && Math.abs(mDistanceY) > 10.0f && scaleWithDistance(
                        mDistanceY
                    )
                ) {
                    isPressed = false
                    val child = getChildAt(mMotionPosition - firstVisiblePosition)
                    if (child != null) {
                        child.isPressed = false
                    }
                    onCancelPendingInputEvents()
                }
            }
        }
        return if (Math.abs(scaleY - 1.0f) > 0.01f) {
            true
        } else super.onTouchEvent(ev)
    }

    private fun scaleWithDistance(distance: Float): Boolean {
        return if (isReachedFirstItemEdge() && isReachedLastItemEdge()) {
            if (distance > 0.0f) {
                mPviotY = 0.0f
                pivotY = mPviotY
                mScaleY = 0.1f * distance / measuredHeight.toFloat() + 1.0f
                if (mScaleY < 1.0f) {
                    mScaleY = 1.0f
                    return false
                }
                if (mScaleY > 1.1f) {
                    mScaleY = 1.1f
                }
                scaleY = mScaleY
                return true
            }
            mPviotY = measuredHeight.toFloat()
            pivotY = mPviotY
            mScaleY = 1.0f - 0.1f * distance / measuredHeight.toFloat()
            if (mScaleY < 1.0f) {
                mScaleY = 1.0f
                return false
            }
            if (mScaleY > 1.1f) {
                mScaleY = 1.1f
            }
            scaleY = mScaleY
            true
        } else if (isReachedFirstItemEdge()) {
            mPviotY = 0.0f
            pivotY = mPviotY
            mScaleY = 0.1f * distance / measuredHeight.toFloat() + 1.0f
            if (mScaleY < 1.0f) {
                mScaleY = 1.0f
                return false
            }
            if (mScaleY > 1.1f) {
                mScaleY = 1.1f
            }
            scaleY = mScaleY
            true
        } else if (!isReachedLastItemEdge()) {
            false
        } else {
            mPviotY = measuredHeight.toFloat()
            pivotY = mPviotY
            mScaleY = 1.0f - 0.1f * distance / measuredHeight.toFloat()
            if (mScaleY < 1.0f) {
                mScaleY = 1.0f
                return false
            }
            if (mScaleY > 1.1f) {
                mScaleY = 1.1f
            }
            scaleY = mScaleY
            true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!mScaleHelper!!.isFinished() && mScaleHelper!!.update()) {
            invalidate()
        }
    }

    private fun startScaleAnimation() {
        if (isReachedFirstItemEdge()) {
            mPviotY = 0.0f
            mScaleY = 1.02f
        }
        if (isReachedLastItemEdge()) {
            mPviotY = measuredHeight.toFloat()
            mScaleY = 1.02f
        }
        if (mScaleY != 1.0f) {
            mScaleHelper?.onRebound(this, mScaleY, mPviotY)
            mScaleY = 1.0f
            invalidate()
        }
    }

    fun isReachedFirstItemEdge(): Boolean {
        var result = false
        if (firstVisiblePosition == 0 && mFirstVisibleItem == 0) {
            val childFirst = getChildAt(0)
            if (childFirst != null) {
                result = childFirst.top + paddingTop >= 0
            }
        }
        return result
    }

    fun isReachedLastItemEdge(): Boolean {
        var result = false
        val firstVisPos = firstVisiblePosition
        val lastVisPos = lastVisiblePosition
        val itemCount = count
        if (lastVisPos == itemCount - 1 || mFirstVisibleItem + mVisibleItemCount == itemCount) {
            val childLast = getChildAt(lastVisPos - firstVisPos)
            if (childLast != null) {
                result = height >= childLast.bottom + paddingBottom
            }
        }
        return result
    }

}
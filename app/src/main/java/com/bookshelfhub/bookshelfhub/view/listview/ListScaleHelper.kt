package com.bookshelfhub.bookshelfhub.view.listview


import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator


class ListScaleHelper(val context: Context?) {


    private val DEBUG = false
    private var mDuration = 0f
    private val mInterpolator: Interpolator = DecelerateInterpolator()
    private var mPviotY = 0f
    private var mScaleFinish = 0f
    private var mScaleStart = 0f
    private var mScaleView: View? = null
    private var mStartTime: Long = 0
    private var mState = 0


    fun onRelease(scaleView: View?, scaleY: Float, pviotY: Float) {
        mScaleView = scaleView
        setScales(scaleY, 1.0f)
        setPviotY(pviotY)
        val now = AnimationUtils.currentAnimationTimeMillis()
        mState = 1
        mStartTime = now
        mDuration = 150.0f
        if (DEBUG) {
            Log.i(
                "os_list",
                "ListScaleHelper onRelease() now = $now, scaleY = $scaleY, pviotY = $pviotY"
            )
        }
    }

    fun onRebound(scaleView: View?, scaleY: Float, pviotY: Float) {
        mScaleView = scaleView
        setScales(1.0f, scaleY)
        setPviotY(pviotY)
        val now = AnimationUtils.currentAnimationTimeMillis()
        mState = 2
        mStartTime = now
        mDuration = 150.0f
        if (DEBUG) {
            Log.i(
                "os_list",
                "ListScaleHelper onRebound() now = $now, scaleY = $scaleY, pviotY = $pviotY"
            )
        }
    }

    fun finish() {
        mState = 0
    }

    private fun setScales(startScale: Float, finishScale: Float) {
        mScaleStart = startScale
        mScaleFinish = finishScale
    }

    private fun setPviotY(pviotY: Float) {
        mPviotY = pviotY
    }

    fun isFinished(): Boolean {
        return mState == 0
    }

    fun update(): Boolean {
        val time = AnimationUtils.currentAnimationTimeMillis()
        val scale = mScaleStart + (mScaleFinish - mScaleStart) * mInterpolator.getInterpolation(
            Math.min(
                (time - mStartTime).toFloat() / mDuration, 1.0f
            )
        )
        when (mState) {
            1 -> {
                if (DEBUG) {
                    Log.i(
                        "os_list",
                        "ListScaleHelper update() 1 duration = " + (time - mStartTime) + ", scale = " + scale + ", mPviotY = " + mPviotY
                    )
                }
                mScaleView!!.pivotY = mPviotY
                mScaleView!!.scaleY = scale
                if ((time - mStartTime).toFloat() > mDuration) {
                    mState = 0
                }
            }
            2 -> {
                if (DEBUG) {
                    Log.i(
                        "os_list",
                        "ListScaleHelper update() 2 duration = " + (time - mStartTime) + ", scale = " + scale + ", mPviotY = " + mPviotY
                    )
                }
                mScaleView!!.pivotY = mPviotY
                mScaleView!!.scaleY = scale
                if ((time - mStartTime).toFloat() > mDuration) {
                    mState = 1
                    mScaleStart = scale
                    mScaleFinish = 1.0f
                    mStartTime = AnimationUtils.currentAnimationTimeMillis()
                    mDuration = 150.0f
                }
            }
        }
        return if (mState == 0) {
            false
        } else true
    }

}
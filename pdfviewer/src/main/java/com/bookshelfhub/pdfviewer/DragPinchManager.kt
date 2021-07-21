package com.bookshelfhub.pdfviewer

import android.graphics.PointF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.bookshelfhub.pdfviewer.model.LinkTapEvent
import com.bookshelfhub.pdfviewer.scroll.ScrollHandle
import com.bookshelfhub.pdfviewer.util.Constants.Pinch.MAXIMUM_ZOOM
import com.bookshelfhub.pdfviewer.util.Constants.Pinch.MINIMUM_ZOOM
import com.bookshelfhub.pdfviewer.util.SnapEdge


/**
 * This Manager takes care of moving the PDFView,
 * set its zoom track user actions.
 */
class DragPinchManager (private val pdfView: PDFView, private  val animationManager: AnimationManager): GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {
    
    private var gestureDetector: GestureDetector? = null
    private var scaleGestureDetector: ScaleGestureDetector? = null

    private var scrolling = false
    private var scaling = false
    private var enabled = false

     init{
        gestureDetector = GestureDetector(pdfView.context, this)
        scaleGestureDetector = ScaleGestureDetector(pdfView.context, this)
        pdfView.setOnTouchListener(this)
    }

    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
    }

    fun disableLongpress() {
        gestureDetector!!.setIsLongpressEnabled(false)
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        val onTapHandled: Boolean = pdfView.callbacks.callOnTap(e)
        val linkTapped = checkLinkTapped(e.x, e.y)
        if (!onTapHandled && !linkTapped) {
            val ps: ScrollHandle? = pdfView.getScrollHandle()
            if (ps != null && !pdfView.documentFitsView()) {
                if (!ps.shown()) {
                    ps.show()
                } else {
                    ps.hide()
                }
            }
        }
        pdfView.performClick()
        return true
    }

    private fun checkLinkTapped(x: Float, y: Float): Boolean {
        val pdfFile: PdfFile = pdfView.pdfFile ?: return false
        val mappedX: Float = -pdfView.getCurrentXOffset() + x
        val mappedY: Float = -pdfView.getCurrentYOffset() + y
        val page = pdfFile.getPageAtOffset(
            if (pdfView.isSwipeVertical()) mappedY else mappedX,
            pdfView.getZoom()
        )
        val pageSize = pdfFile.getScaledPageSize(page, pdfView.getZoom())
        val pageX: Int
        val pageY: Int
        if (pdfView.isSwipeVertical()) {
            pageX = pdfFile.getSecondaryPageOffset(page, pdfView.getZoom()).toInt()
            pageY = pdfFile.getPageOffset(page, pdfView.getZoom()).toInt()
        } else {
            pageY = pdfFile.getSecondaryPageOffset(page, pdfView.getZoom()).toInt()
            pageX = pdfFile.getPageOffset(page, pdfView.getZoom()).toInt()
        }
        for (link in pdfFile.getPageLinks(page)!!) {
            val mapped = pdfFile.mapRectToDevice(
                page, pageX, pageY,
                pageSize!!.width.toInt(),
                pageSize.height.toInt(), link!!.bounds
            )
            mapped!!.sort()
            if (mapped.contains(mappedX, mappedY)) {
                pdfView.callbacks.callLinkHandler(
                    LinkTapEvent(
                        x,
                        y,
                        mappedX,
                        mappedY,
                        mapped,
                        link
                    )
                )
                return true
            }
        }
        return false
    }

    private fun startPageFling(
        downEvent: MotionEvent,
        ev: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ) {
        if (!checkDoPageFling(velocityX, velocityY)) {
            return
        }
        val direction: Int
        direction = if (pdfView.isSwipeVertical()) {
            if (velocityY > 0) -1 else 1
        } else {
            if (velocityX > 0) -1 else 1
        }
        // get the focused page during the down event to ensure only a single page is changed
        val delta = if (pdfView.isSwipeVertical()) ev.y - downEvent.y else ev.x - downEvent.x
        val offsetX: Float = pdfView.getCurrentXOffset() - delta * pdfView.getZoom()
        val offsetY: Float = pdfView.getCurrentYOffset() - delta * pdfView.getZoom()
        val startingPage: Int = pdfView.findFocusPage(offsetX, offsetY)
        val targetPage = Math.max(0, Math.min(pdfView.getPageCount() - 1, startingPage + direction))
        val edge: SnapEdge = pdfView.findSnapEdge(targetPage)
        val offset: Float = pdfView.snapOffsetForPage(targetPage, edge)
        animationManager.startPageFlingAnimation(-offset)
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        if (!pdfView.isDoubletapEnabled()) {
            return false
        }
        if (pdfView.getZoom() < pdfView.getMidZoom()) {
            pdfView.zoomWithAnimation(e.x, e.y, pdfView.getMidZoom())
        } else if (pdfView.getZoom() < pdfView.getMaxZoom()) {
            pdfView.zoomWithAnimation(e.x, e.y, pdfView.getMaxZoom())
        } else {
            pdfView.resetZoomWithAnimation()
        }
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        animationManager.stopFling()
        return true
    }

    override fun onShowPress(e: MotionEvent?) {}

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        scrolling = true
        if (pdfView.isZooming() || pdfView.isSwipeEnabled()) {
            pdfView.moveRelativeTo(-distanceX, -distanceY)
        }
        if (!scaling || pdfView.doRenderDuringScale()) {
            pdfView.loadPageByOffset()
        }
        return true
    }

    private fun onScrollEnd(event: MotionEvent) {
        pdfView.loadPages()
        hideHandle()
        if (!animationManager.isFlinging()) {
            pdfView.performPageSnap()
        }
    }

    override fun onLongPress(e: MotionEvent?) {
        pdfView.callbacks.callOnLongPress(e)
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (!pdfView.isSwipeEnabled()) {
            return false
        }
        if (pdfView.isPageFlingEnabled()) {
            if (pdfView.pageFillsScreen()) {
                onBoundedFling(velocityX, velocityY)
            } else {
                startPageFling(e1, e2, velocityX, velocityY)
            }
            return true
        }
        val xOffset = pdfView.getCurrentXOffset().toInt()
        val yOffset = pdfView.getCurrentYOffset().toInt()
        val minX: Float
        val minY: Float
        val pdfFile: PdfFile = pdfView.pdfFile!!
        if (pdfView.isSwipeVertical()) {
            minX = -(pdfView.toCurrentScale(pdfFile.getMaxPageWidth()) - pdfView.width)
            minY = -(pdfFile.getDocLen(pdfView.getZoom()) - pdfView.height)
        } else {
            minX = -(pdfFile.getDocLen(pdfView.getZoom()) - pdfView.width)
            minY = -(pdfView.toCurrentScale(pdfFile.getMaxPageHeight()) - pdfView.height)
        }
        animationManager.startFlingAnimation(
            xOffset, yOffset, velocityX.toInt(), velocityY.toInt(),
            minX.toInt(), 0, minY.toInt(), 0
        )
        return true
    }

    private fun onBoundedFling(velocityX: Float, velocityY: Float) {
        val xOffset = pdfView.getCurrentXOffset().toInt()
        val yOffset = pdfView.getCurrentYOffset().toInt()
        val pdfFile: PdfFile = pdfView.pdfFile!!
        val pageStart = -pdfFile.getPageOffset(pdfView.getCurrentPage(), pdfView.getZoom())
        val pageEnd = pageStart - pdfFile.getPageLength(pdfView.getCurrentPage(), pdfView.getZoom())
        val minX: Float
        val minY: Float
        val maxX: Float
        val maxY: Float
        if (pdfView.isSwipeVertical()) {
            minX = -(pdfView.toCurrentScale(pdfFile.getMaxPageWidth()) - pdfView.width)
            minY = pageEnd + pdfView.height
            maxX = 0f
            maxY = pageStart
        } else {
            minX = pageEnd + pdfView.width
            minY = -(pdfView.toCurrentScale(pdfFile.getMaxPageHeight()) - pdfView.height)
            maxX = pageStart
            maxY = 0f
        }
        animationManager.startFlingAnimation(
            xOffset, yOffset, velocityX.toInt(), velocityY.toInt(),
            minX.toInt(), maxX.toInt(), minY.toInt(), maxY.toInt()
        )
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        var dr = detector.scaleFactor
        val wantedZoom: Float = pdfView.getZoom() * dr
        val minZoom: Float = Math.min(MINIMUM_ZOOM, pdfView.getMinZoom())
        val maxZoom: Float = Math.min(MAXIMUM_ZOOM, pdfView.getMaxZoom())
        if (wantedZoom < minZoom) {
            dr = minZoom / pdfView.getZoom()
        } else if (wantedZoom > maxZoom) {
            dr = maxZoom / pdfView.getZoom()
        }
        pdfView.zoomCenteredRelativeTo(dr, PointF(detector.focusX, detector.focusY))
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        scaling = true
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {
        pdfView.loadPages()
        hideHandle()
        scaling = false
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (!enabled) {
            return false
        }
        var retVal = scaleGestureDetector!!.onTouchEvent(event)
        retVal = gestureDetector!!.onTouchEvent(event) || retVal
        if (event.action == MotionEvent.ACTION_UP) {
            if (scrolling) {
                scrolling = false
                onScrollEnd(event)
            }
        }
        return retVal
    }

    private fun hideHandle() {
        val scrollHandle: ScrollHandle? = pdfView.getScrollHandle()
        if (scrollHandle != null && scrollHandle.shown()) {
            scrollHandle.hideDelayed()
        }
    }

    private fun checkDoPageFling(velocityX: Float, velocityY: Float): Boolean {
        val absX = Math.abs(velocityX)
        val absY = Math.abs(velocityY)
        return if (pdfView.isSwipeVertical()) absY > absX else absX > absY
    }

}
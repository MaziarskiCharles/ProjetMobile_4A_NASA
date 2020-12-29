/*
* Copyright (c) 2012 Jason Polites
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.polites.android
import android.content.res.Configuration
import android.graphics.PointF
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
class GestureImageViewTouchListener(image:GestureImageView, displayWidth:Int, displayHeight:Int):OnTouchListener {
  private val image:GestureImageView
  private val onClickListener:OnClickListener
  private val current = PointF()
  private val last = PointF()
  private val next = PointF()
  private val midpoint = PointF()
  private val scaleVector = VectorF()
  private val pinchVector = VectorF()
  private val touched = false
  private val inZoom = false
  private val initialDistance:Float = 0.toFloat()
  private val lastScale = 1.0f
  private val currentScale = 1.0f
  private val boundaryLeft = 0f
  private val boundaryTop = 0f
  private val boundaryRight = 0f
  private val boundaryBottom = 0f
  var maxScale = 5.0f
  var minScale = 0.25f
  private val fitScaleHorizontal = 1.0f
  private val fitScaleVertical = 1.0f
  private val canvasWidth = 0
  private val canvasHeight = 0
  private val centerX = 0f
  private val centerY = 0f
  private val startingScale = 0f
  private val canDragX = false
  private val canDragY = false
  private val multiTouch = false
  private val displayWidth:Int = 0
  private val displayHeight:Int = 0
  private val imageWidth:Int = 0
  private val imageHeight:Int = 0
  private val flingListener:FlingListener
  private val flingAnimation:FlingAnimation
  private val zoomAnimation:ZoomAnimation
  private val moveAnimation:MoveAnimation
  private val tapDetector:GestureDetector
  private val flingDetector:GestureDetector
  private val imageListener:GestureImageViewListener
  init{
    this.image = image
    this.displayWidth = displayWidth
    this.displayHeight = displayHeight
    this.centerX = displayWidth.toFloat() / 2.0f
    this.centerY = displayHeight.toFloat() / 2.0f
    this.imageWidth = image.getImageWidth()
    this.imageHeight = image.getImageHeight()
    startingScale = image.getScale()
    currentScale = startingScale
    lastScale = startingScale
    boundaryRight = displayWidth.toFloat()
    boundaryBottom = displayHeight.toFloat()
    boundaryLeft = 0f
    boundaryTop = 0f
    next.x = image.getImageX()
    next.y = image.getImageY()
    flingListener = FlingListener()
    flingAnimation = FlingAnimation()
    zoomAnimation = ZoomAnimation()
    moveAnimation = MoveAnimation()
    flingAnimation.setListener(object:FlingAnimationListener() {
      fun onMove(x:Float, y:Float) {
        handleDrag(current.x + x, current.y + y)
      }
      fun onComplete() {}
    })
    zoomAnimation.setZoom(2.0f)
    zoomAnimation.setZoomAnimationListener(object:ZoomAnimationListener() {
      fun onZoom(scale:Float, x:Float, y:Float) {
        if (scale <= maxScale && scale >= minScale)
        {
          handleScale(scale, x, y)
        }
      }
      fun onComplete() {
        inZoom = false
        handleUp()
      }
    })
    moveAnimation.setMoveAnimationListener(object:MoveAnimationListener() {
      fun onMove(x:Float, y:Float) {
        image.setPosition(x, y)
        image.redraw()
      }
    })
    tapDetector = GestureDetector(image.getContext(), object:SimpleOnGestureListener() {
      fun onDoubleTap(e:MotionEvent):Boolean {
        startZoom(e)
        return true
      }
      fun onSingleTapConfirmed(e:MotionEvent):Boolean {
        if (!inZoom)
        {
          if (onClickListener != null)
          {
            onClickListener.onClick(image)
            return true
          }
        }
        return false
      }
    })
    flingDetector = GestureDetector(image.getContext(), flingListener)
    imageListener = image.getGestureImageViewListener()
    calculateBoundaries()
  }
  private fun startFling() {
    flingAnimation.setVelocityX(flingListener.getVelocityX())
    flingAnimation.setVelocityY(flingListener.getVelocityY())
    image.animationStart(flingAnimation)
  }
  private fun startZoom(e:MotionEvent) {
    inZoom = true
    zoomAnimation.reset()
    val zoomTo:Float
    if (image.isLandscape())
    {
      if (image.getDeviceOrientation() === Configuration.ORIENTATION_PORTRAIT)
      {
        val scaledHeight = image.getScaledHeight()
        if (scaledHeight < canvasHeight)
        {
          zoomTo = fitScaleVertical / currentScale
          zoomAnimation.setTouchX(e.getX())
          zoomAnimation.setTouchY(image.getCenterY())
        }
        else
        {
          zoomTo = fitScaleHorizontal / currentScale
          zoomAnimation.setTouchX(image.getCenterX())
          zoomAnimation.setTouchY(image.getCenterY())
        }
      }
      else
      {
        val scaledWidth = image.getScaledWidth()
        if (scaledWidth == canvasWidth)
        {
          zoomTo = currentScale * 4.0f
          zoomAnimation.setTouchX(e.getX())
          zoomAnimation.setTouchY(e.getY())
        }
        else if (scaledWidth < canvasWidth)
        {
          zoomTo = fitScaleHorizontal / currentScale
          zoomAnimation.setTouchX(image.getCenterX())
          zoomAnimation.setTouchY(e.getY())
        }
        else
        {
          zoomTo = fitScaleHorizontal / currentScale
          zoomAnimation.setTouchX(image.getCenterX())
          zoomAnimation.setTouchY(image.getCenterY())
        }
      }
    }
    else
    {
      if (image.getDeviceOrientation() === Configuration.ORIENTATION_PORTRAIT)
      {
        val scaledHeight = image.getScaledHeight()
        if (scaledHeight == canvasHeight)
        {
          zoomTo = currentScale * 4.0f
          zoomAnimation.setTouchX(e.getX())
          zoomAnimation.setTouchY(e.getY())
        }
        else if (scaledHeight < canvasHeight)
        {
          zoomTo = fitScaleVertical / currentScale
          zoomAnimation.setTouchX(e.getX())
          zoomAnimation.setTouchY(image.getCenterY())
        }
        else
        {
          zoomTo = fitScaleVertical / currentScale
          zoomAnimation.setTouchX(image.getCenterX())
          zoomAnimation.setTouchY(image.getCenterY())
        }
      }
      else
      {
        val scaledWidth = image.getScaledWidth()
        if (scaledWidth < canvasWidth)
        {
          zoomTo = fitScaleHorizontal / currentScale
          zoomAnimation.setTouchX(image.getCenterX())
          zoomAnimation.setTouchY(e.getY())
        }
        else
        {
          zoomTo = fitScaleVertical / currentScale
          zoomAnimation.setTouchX(image.getCenterX())
          zoomAnimation.setTouchY(image.getCenterY())
        }
      }
    }
    zoomAnimation.setZoom(zoomTo)
    image.animationStart(zoomAnimation)
  }
  private fun stopAnimations() {
    image.animationStop()
  }
  fun onTouch(v:View, event:MotionEvent):Boolean {
    if (!inZoom)
    {
      if (!tapDetector.onTouchEvent(event))
      {
        if (event.getPointerCount() === 1 && flingDetector.onTouchEvent(event))
        {
          startFling()
        }
        if (event.getAction() === MotionEvent.ACTION_UP)
        {
          handleUp()
        }
        else if (event.getAction() === MotionEvent.ACTION_DOWN)
        {
          stopAnimations()
          last.x = event.getX()
          last.y = event.getY()
          if (imageListener != null)
          {
            imageListener.onTouch(last.x, last.y)
          }
          touched = true
        }
        else if (event.getAction() === MotionEvent.ACTION_MOVE)
        {
          if (event.getPointerCount() > 1)
          {
            multiTouch = true
            if (initialDistance > 0)
            {
              pinchVector.set(event)
              pinchVector.calculateLength()
              val distance = pinchVector.length
              if (initialDistance != distance)
              {
                val newScale = (distance / initialDistance) * lastScale
                if (newScale <= maxScale)
                {
                  scaleVector.length *= newScale
                  scaleVector.calculateEndPoint()
                  scaleVector.length /= newScale
                  val newX = scaleVector.end.x
                  val newY = scaleVector.end.y
                  handleScale(newScale, newX, newY)
                }
              }
            }
            else
            {
              initialDistance = MathUtils.distance(event)
              MathUtils.midpoint(event, midpoint)
              scaleVector.setStart(midpoint)
              scaleVector.setEnd(next)
              scaleVector.calculateLength()
              scaleVector.calculateAngle()
              scaleVector.length /= lastScale
            }
          }
          else
          {
            if (!touched)
            {
              touched = true
              last.x = event.getX()
              last.y = event.getY()
              next.x = image.getImageX()
              next.y = image.getImageY()
            }
            else if (!multiTouch)
            {
              if (handleDrag(event.getX(), event.getY()))
              {
                image.redraw()
              }
            }
          }
        }
      }
    }
    return true
  }
  protected fun handleUp() {
    multiTouch = false
    initialDistance = 0f
    lastScale = currentScale
    if (!canDragX)
    {
      next.x = centerX
    }
    if (!canDragY)
    {
      next.y = centerY
    }
    boundCoordinates()
    if (!canDragX && !canDragY)
    {
      if (image.isLandscape())
      {
        currentScale = fitScaleHorizontal
        lastScale = fitScaleHorizontal
      }
      else
      {
        currentScale = fitScaleVertical
        lastScale = fitScaleVertical
      }
    }
    image.setScale(currentScale)
    image.setPosition(next.x, next.y)
    if (imageListener != null)
    {
      imageListener.onScale(currentScale)
      imageListener.onPosition(next.x, next.y)
    }
    image.redraw()
  }
  protected fun handleScale(scale:Float, x:Float, y:Float) {
    currentScale = scale
    if (currentScale > maxScale)
    {
      currentScale = maxScale
    }
    else if (currentScale < minScale)
    {
      currentScale = minScale
    }
    else
    {
      next.x = x
      next.y = y
    }
    calculateBoundaries()
    image.setScale(currentScale)
    image.setPosition(next.x, next.y)
    if (imageListener != null)
    {
      imageListener.onScale(currentScale)
      imageListener.onPosition(next.x, next.y)
    }
    image.redraw()
  }
  protected fun handleDrag(x:Float, y:Float):Boolean {
    current.x = x
    current.y = y
    val diffX = (current.x - last.x)
    val diffY = (current.y - last.y)
    if (diffX != 0f || diffY != 0f)
    {
      if (canDragX) next.x += diffX
      if (canDragY) next.y += diffY
      boundCoordinates()
      last.x = current.x
      last.y = current.y
      if (canDragX || canDragY)
      {
        image.setPosition(next.x, next.y)
        if (imageListener != null)
        {
          imageListener.onPosition(next.x, next.y)
        }
        return true
      }
    }
    return false
  }
  fun reset() {
    currentScale = startingScale
    next.x = centerX
    next.y = centerY
    calculateBoundaries()
    image.setScale(currentScale)
    image.setPosition(next.x, next.y)
    image.redraw()
  }
  fun setOnClickListener(onClickListener:OnClickListener) {
    this.onClickListener = onClickListener
  }
  protected fun setCanvasWidth(canvasWidth:Int) {
    this.canvasWidth = canvasWidth
  }
  protected fun setCanvasHeight(canvasHeight:Int) {
    this.canvasHeight = canvasHeight
  }
  protected fun setFitScaleHorizontal(fitScale:Float) {
    this.fitScaleHorizontal = fitScale
  }
  protected fun setFitScaleVertical(fitScaleVertical:Float) {
    this.fitScaleVertical = fitScaleVertical
  }
  protected fun boundCoordinates() {
    if (next.x < boundaryLeft)
    {
      next.x = boundaryLeft
    }
    else if (next.x > boundaryRight)
    {
      next.x = boundaryRight
    }
    if (next.y < boundaryTop)
    {
      next.y = boundaryTop
    }
    else if (next.y > boundaryBottom)
    {
      next.y = boundaryBottom
    }
  }
  protected fun calculateBoundaries() {
    val effectiveWidth = Math.round(imageWidth.toFloat() * currentScale)
    val effectiveHeight = Math.round(imageHeight.toFloat() * currentScale)
    canDragX = effectiveWidth > displayWidth
    canDragY = effectiveHeight > displayHeight
    if (canDragX)
    {
      val diff = (effectiveWidth - displayWidth).toFloat() / 2.0f
      boundaryLeft = centerX - diff
      boundaryRight = centerX + diff
    }
    if (canDragY)
    {
      val diff = (effectiveHeight - displayHeight).toFloat() / 2.0f
      boundaryTop = centerY - diff
      boundaryBottom = centerY + diff
    }
  }
}
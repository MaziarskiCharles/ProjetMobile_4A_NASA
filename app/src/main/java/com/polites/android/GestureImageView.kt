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
import java.io.InputStream
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import android.content.Context
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
class GestureImageView:ImageView {
  private val drawLock = Semaphore(0)
  private val animator:Animator
  val drawable:Drawable
  val imageX = 0f
  val imageY = 0f
  private val layout = false
  private val scaleAdjust = 1.0f
  private val startingScale = -1.0f
  private val scale = 1.0f
  private val maxScale = 5.0f
  private val minScale = 0.75f
  private val fitScaleHorizontal = 1.0f
  private val fitScaleVertical = 1.0f
  private val rotation = 0.0f
  val centerX:Float = 0.toFloat()
  val centerY:Float = 0.toFloat()
  private val startX:Float
  private val startY:Float
  private val hWidth:Int = 0
  private val hHeight:Int = 0
  private val resId = -1
  var isRecycle = false
  var isStrict = false
  private val displayHeight:Int = 0
  private val displayWidth:Int = 0
  private val alpha = 255
  private val colorFilter:ColorFilter
  val deviceOrientation = -1
  private val imageOrientation:Int = 0
  var gestureImageViewListener:GestureImageViewListener
  private val gestureImageViewTouchListener:GestureImageViewTouchListener
  private val customOnTouchListener:OnTouchListener
  private val onClickListener:OnClickListener
  protected val isRecycled:Boolean
  get() {
    if (drawable != null && drawable is BitmapDrawable)
    {
      val bitmap = (drawable as BitmapDrawable).getBitmap()
      if (bitmap != null)
      {
        return bitmap.isRecycled()
      }
    }
    return false
  }
  val scaledWidth:Int
  get() {
    return Math.round(imageWidth * getScale())
  }
  val scaledHeight:Int
  get() {
    return Math.round(imageHeight * getScale())
  }
  val imageWidth:Int
  get() {
    if (drawable != null)
    {
      return drawable.getIntrinsicWidth()
    }
    return 0
  }
  val imageHeight:Int
  get() {
    if (drawable != null)
    {
      return drawable.getIntrinsicHeight()
    }
    return 0
  }
  var imageMatrix:Matrix
  get() {
    if (isStrict)
    {
      throw UnsupportedOperationException("Not supported")
    }
    return super.getImageMatrix()
  }
  set(matrix) {
    if (isStrict)
    {
      throw UnsupportedOperationException("Not supported")
    }
  }
  val isLandscape:Boolean
  get() {
    return imageWidth >= imageHeight
  }
  val isPortrait:Boolean
  get() {
    return imageWidth <= imageHeight
  }
  /**
	 * Returns true if the image dimensions are aligned with the orientation of the device.
	 * @return
	 */
  val isOrientationAligned:Boolean
  get() {
    if (deviceOrientation == Configuration.ORIENTATION_LANDSCAPE)
    {
      return isLandscape
    }
    else if (deviceOrientation == Configuration.ORIENTATION_PORTRAIT)
    {
      return isPortrait
    }
    return true
  }
  constructor(context:Context, attrs:AttributeSet, defStyle:Int) : this(context, attrs) {}
  constructor(context:Context, attrs:AttributeSet) : super(context, attrs) {
    val scaleType = attrs.getAttributeValue(GLOBAL_NS, "scaleType")
    if (scaleType == null || scaleType.trim({ it <= ' ' }).length == 0)
    {
      setScaleType(ScaleType.CENTER_INSIDE)
    }
    val strStartX = attrs.getAttributeValue(LOCAL_NS, "start-x")
    val strStartY = attrs.getAttributeValue(LOCAL_NS, "start-y")
    if (strStartX != null && strStartX.trim({ it <= ' ' }).length > 0)
    {
      startX = java.lang.Float.parseFloat(strStartX)
    }
    if (strStartY != null && strStartY.trim({ it <= ' ' }).length > 0)
    {
      startY = java.lang.Float.parseFloat(strStartY)
    }
    setStartingScale(attrs.getAttributeFloatValue(LOCAL_NS, "start-scale", startingScale))
    setMinScale(attrs.getAttributeFloatValue(LOCAL_NS, "min-scale", minScale))
    setMaxScale(attrs.getAttributeFloatValue(LOCAL_NS, "max-scale", maxScale))
    isStrict = attrs.getAttributeBooleanValue(LOCAL_NS, "strict", isStrict)
    isRecycle = attrs.getAttributeBooleanValue(LOCAL_NS, "recycle", isRecycle)
    initImage()
  }
  constructor(context:Context) : super(context) {
    setScaleType(ScaleType.CENTER_INSIDE)
    initImage()
  }
  protected fun onMeasure(widthMeasureSpec:Int, heightMeasureSpec:Int) {
    if (drawable != null)
    {
      val orientation = getResources().getConfiguration().orientation
      if (orientation == Configuration.ORIENTATION_LANDSCAPE)
      {
        displayHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (getLayoutParams().width === LayoutParams.WRAP_CONTENT)
        {
          val ratio = imageWidth.toFloat() / imageHeight.toFloat()
          displayWidth = Math.round(displayHeight.toFloat() * ratio)
        }
        else
        {
          displayWidth = MeasureSpec.getSize(widthMeasureSpec)
        }
      }
      else
      {
        displayWidth = MeasureSpec.getSize(widthMeasureSpec)
        if (getLayoutParams().height === LayoutParams.WRAP_CONTENT)
        {
          val ratio = imageHeight.toFloat() / imageWidth.toFloat()
          displayHeight = Math.round(displayWidth.toFloat() * ratio)
        }
        else
        {
          displayHeight = MeasureSpec.getSize(heightMeasureSpec)
        }
      }
    }
    else
    {
      displayHeight = MeasureSpec.getSize(heightMeasureSpec)
      displayWidth = MeasureSpec.getSize(widthMeasureSpec)
    }
    setMeasuredDimension(displayWidth, displayHeight)
  }
  protected fun onLayout(changed:Boolean, left:Int, top:Int, right:Int, bottom:Int) {
    super.onLayout(changed, left, top, right, bottom)
    if (changed || !layout)
    {
      setupCanvas(displayWidth, displayHeight, getResources().getConfiguration().orientation)
    }
  }
  protected fun setupCanvas(measuredWidth:Int, measuredHeight:Int, orientation:Int) {
    if (deviceOrientation != orientation)
    {
      layout = false
      deviceOrientation = orientation
    }
    if (drawable != null && !layout)
    {
      val imageWidth = imageWidth
      val imageHeight = imageHeight
      hWidth = Math.round((imageWidth.toFloat() / 2.0f))
      hHeight = Math.round((imageHeight.toFloat() / 2.0f))
      measuredWidth -= (getPaddingLeft() + getPaddingRight())
      measuredHeight -= (getPaddingTop() + getPaddingBottom())
      computeCropScale(imageWidth, imageHeight, measuredWidth, measuredHeight)
      if (startingScale <= 0.0f)
      {
        computeStartingScale(imageWidth, imageHeight, measuredWidth, measuredHeight)
      }
      scaleAdjust = startingScale
      this.centerX = measuredWidth.toFloat() / 2.0f
      this.centerY = measuredHeight.toFloat() / 2.0f
      if (startX == null)
      {
        imageX = centerX
      }
      else
      {
        imageX = startX
      }
      if (startY == null)
      {
        imageY = centerY
      }
      else
      {
        imageY = startY
      }
      gestureImageViewTouchListener = GestureImageViewTouchListener(this, measuredWidth, measuredHeight)
      if (isLandscape)
      {
        gestureImageViewTouchListener.setMinScale(minScale * fitScaleHorizontal)
      }
      else
      {
        gestureImageViewTouchListener.setMinScale(minScale * fitScaleVertical)
      }
      gestureImageViewTouchListener.setMaxScale(maxScale * startingScale)
      gestureImageViewTouchListener.setFitScaleHorizontal(fitScaleHorizontal)
      gestureImageViewTouchListener.setFitScaleVertical(fitScaleVertical)
      gestureImageViewTouchListener.setCanvasWidth(measuredWidth)
      gestureImageViewTouchListener.setCanvasHeight(measuredHeight)
      gestureImageViewTouchListener.setOnClickListener(onClickListener)
      drawable.setBounds(-hWidth, -hHeight, hWidth, hHeight)
      super.setOnTouchListener(object:OnTouchListener() {
        fun onTouch(v:View, event:MotionEvent):Boolean {
          if (customOnTouchListener != null)
          {
            customOnTouchListener.onTouch(v, event)
          }
          return gestureImageViewTouchListener.onTouch(v, event)
        }
      })
      layout = true
    }
  }
  protected fun computeCropScale(imageWidth:Int, imageHeight:Int, measuredWidth:Int, measuredHeight:Int) {
    fitScaleHorizontal = measuredWidth.toFloat() / imageWidth.toFloat()
    fitScaleVertical = measuredHeight.toFloat() / imageHeight.toFloat()
  }
  protected fun computeStartingScale(imageWidth:Int, imageHeight:Int, measuredWidth:Int, measuredHeight:Int) {
    when (getScaleType()) {
      CENTER ->
      // Center the image in the view, but perform no scaling.
      startingScale = 1.0f
      CENTER_CROP ->
      // Scale the image uniformly (maintain the image's aspect ratio) so that both dimensions
      // (width and height) of the image will be equal to or larger than the corresponding dimension of the view (minus padding).
      startingScale = Math.max(measuredHeight.toFloat() / imageHeight.toFloat(), measuredWidth.toFloat() / imageWidth.toFloat())
      CENTER_INSIDE -> {
        // Scale the image uniformly (maintain the image's aspect ratio) so that both dimensions
        // (width and height) of the image will be equal to or less than the corresponding dimension of the view (minus padding).
        val wRatio = imageWidth.toFloat() / measuredWidth.toFloat()
        val hRatio = imageHeight.toFloat() / measuredHeight.toFloat()
        if (wRatio > hRatio)
        {
          startingScale = fitScaleHorizontal
        }
        else
        {
          startingScale = fitScaleVertical
        }
      }
    }
  }
  protected fun recycle() {
    if (isRecycle && drawable != null && drawable is BitmapDrawable)
    {
      val bitmap = (drawable as BitmapDrawable).getBitmap()
      if (bitmap != null)
      {
        bitmap.recycle()
      }
    }
  }
  protected fun onDraw(canvas:Canvas) {
    if (layout)
    {
      if (drawable != null && !isRecycled)
      {
        canvas.save()
        val adjustedScale = scale * scaleAdjust
        canvas.translate(imageX, imageY)
        if (rotation != 0.0f)
        {
          canvas.rotate(rotation)
        }
        if (adjustedScale != 1.0f)
        {
          canvas.scale(adjustedScale, adjustedScale)
        }
        drawable.draw(canvas)
        canvas.restore()
      }
      if (drawLock.availablePermits() <= 0)
      {
        drawLock.release()
      }
    }
  }
  /**
	 * Waits for a draw
	 * @param max time to wait for draw (ms)
	 * @throws InterruptedException
	 */
  @Throws(InterruptedException::class)
  fun waitForDraw(timeout:Long):Boolean {
    return drawLock.tryAcquire(timeout, TimeUnit.MILLISECONDS)
  }
  protected fun onAttachedToWindow() {
    animator = Animator(this, "GestureImageViewAnimator")
    animator.start()
    if (resId >= 0 && drawable == null)
    {
      setImageResource(resId)
    }
    super.onAttachedToWindow()
  }
  fun animationStart(animation:Animation) {
    if (animator != null)
    {
      animator.play(animation)
    }
  }
  fun animationStop() {
    if (animator != null)
    {
      animator.cancel()
    }
  }
  protected fun onDetachedFromWindow() {
    if (animator != null)
    {
      animator.finish()
    }
    if (isRecycle && drawable != null && !isRecycled)
    {
      recycle()
      drawable = null
    }
    super.onDetachedFromWindow()
  }
  protected fun initImage() {
    if (this.drawable != null)
    {
      this.drawable.setAlpha(alpha)
      this.drawable.setFilterBitmap(true)
      if (colorFilter != null)
      {
        this.drawable.setColorFilter(colorFilter)
      }
    }
    if (!layout)
    {
      requestLayout()
      redraw()
    }
  }
  fun setImageBitmap(image:Bitmap) {
    this.drawable = BitmapDrawable(getResources(), image)
    initImage()
  }
  fun setImageDrawable(drawable:Drawable) {
    this.drawable = drawable
    initImage()
  }
  fun setImageResource(id:Int) {
    if (this.drawable != null)
    {
      this.recycle()
    }
    if (id >= 0)
    {
      this.resId = id
      setImageDrawable(getContext().getResources().getDrawable(id))
    }
  }
  fun moveBy(x:Float, y:Float) {
    this.imageX += x
    this.imageY += y
  }
  fun setPosition(x:Float, y:Float) {
    this.imageX = x
    this.imageY = y
  }
  fun redraw() {
    postInvalidate()
  }
  fun setMinScale(min:Float) {
    this.minScale = min
    if (gestureImageViewTouchListener != null)
    {
      gestureImageViewTouchListener.setMinScale(min * fitScaleHorizontal)
    }
  }
  fun setMaxScale(max:Float) {
    this.maxScale = max
    if (gestureImageViewTouchListener != null)
    {
      gestureImageViewTouchListener.setMaxScale(max * startingScale)
    }
  }
  fun setScale(scale:Float) {
    scaleAdjust = scale
  }
  fun getScale():Float {
    return scaleAdjust
  }
  fun reset() {
    imageX = centerX
    imageY = centerY
    scaleAdjust = startingScale
    if (gestureImageViewTouchListener != null)
    {
      gestureImageViewTouchListener.reset()
    }
    redraw()
  }
  fun setRotation(rotation:Float) {
    this.rotation = rotation
  }
  fun setAlpha(alpha:Int) {
    this.alpha = alpha
    if (drawable != null)
    {
      drawable.setAlpha(alpha)
    }
  }
  fun setColorFilter(cf:ColorFilter) {
    this.colorFilter = cf
    if (drawable != null)
    {
      drawable.setColorFilter(cf)
    }
  }
  fun setImageURI(mUri:Uri) {
    if ("content" == mUri.getScheme())
    {
      try
      {
        val orientationColumn = arrayOf<String>(MediaStore.Images.Media.ORIENTATION)
        val cur = getContext().getContentResolver().query(mUri, orientationColumn, null, null, null)
        if (cur != null && cur.moveToFirst())
        {
          imageOrientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
        }
        val `in`:InputStream = null
        try
        {
          `in` = getContext().getContentResolver().openInputStream(mUri)
          val bmp = BitmapFactory.decodeStream(`in`)
          if (imageOrientation != 0)
          {
            val m = Matrix()
            m.postRotate(imageOrientation)
            val rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true)
            bmp.recycle()
            setImageDrawable(BitmapDrawable(getResources(), rotated))
          }
          else
          {
            setImageDrawable(BitmapDrawable(getResources(), bmp))
          }
        }
        finally
        {
          if (`in` != null)
          {
            `in`.close()
          }
          if (cur != null)
          {
            cur.close()
          }
        }
      }
      catch (e:Exception) {
        Log.w("GestureImageView", "Unable to open content: " + mUri, e)
      }
    }
    else
    {
      setImageDrawable(Drawable.createFromPath(mUri.toString()))
    }
    if (drawable == null)
    {
      Log.e("GestureImageView", "resolveUri failed on bad bitmap uri: " + mUri)
      // Don't try again.
      mUri = null
    }
  }
  fun setScaleType(scaleType:ScaleType) {
    if ((scaleType === ScaleType.CENTER ||
         scaleType === ScaleType.CENTER_CROP ||
         scaleType === ScaleType.CENTER_INSIDE))
    {
      super.setScaleType(scaleType)
    }
    else if (isStrict)
    {
      throw UnsupportedOperationException("Not supported")
    }
  }
  fun invalidateDrawable(dr:Drawable) {
    if (isStrict)
    {
      throw UnsupportedOperationException("Not supported")
    }
    super.invalidateDrawable(dr)
  }
  fun onCreateDrawableState(extraSpace:Int):IntArray {
    if (isStrict)
    {
      throw UnsupportedOperationException("Not supported")
    }
    return super.onCreateDrawableState(extraSpace)
  }
  fun setAdjustViewBounds(adjustViewBounds:Boolean) {
    if (isStrict)
    {
      throw UnsupportedOperationException("Not supported")
    }
    super.setAdjustViewBounds(adjustViewBounds)
  }
  fun setImageLevel(level:Int) {
    if (isStrict)
    {
      throw UnsupportedOperationException("Not supported")
    }
    super.setImageLevel(level)
  }
  fun setImageState(state:IntArray, merge:Boolean) {
    if (isStrict)
    {
      throw UnsupportedOperationException("Not supported")
    }
  }
  fun setSelected(selected:Boolean) {
    if (isStrict)
    {
      throw UnsupportedOperationException("Not supported")
    }
    super.setSelected(selected)
  }
  fun setOnTouchListener(l:OnTouchListener) {
    this.customOnTouchListener = l
  }
  fun setStartingScale(startingScale:Float) {
    this.startingScale = startingScale
  }
  fun setStartingPosition(x:Float, y:Float) {
    this.startX = x
    this.startY = y
  }
  fun setOnClickListener(l:OnClickListener) {
    this.onClickListener = l
    if (gestureImageViewTouchListener != null)
    {
      gestureImageViewTouchListener.setOnClickListener(l)
    }
  }
  companion object {
    val GLOBAL_NS = "http://schemas.android.com/apk/res/android"
    val LOCAL_NS = "http://schemas.polites.com/android"
  }
}
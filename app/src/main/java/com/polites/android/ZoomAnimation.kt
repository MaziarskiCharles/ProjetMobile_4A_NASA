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
import android.graphics.PointF
/**
* @author Jason Polites
*
*/
class ZoomAnimation:Animation {
  private val firstFrame = true
  var touchX:Float = 0.toFloat()
  var touchY:Float = 0.toFloat()
  var zoom:Float = 0.toFloat()
  private val startX:Float = 0.toFloat()
  private val startY:Float = 0.toFloat()
  private val startScale:Float = 0.toFloat()
  private val xDiff:Float = 0.toFloat()
  private val yDiff:Float = 0.toFloat()
  private val scaleDiff:Float = 0.toFloat()
  var animationLengthMS:Long = 200
  private val totalTime:Long = 0
  var zoomAnimationListener:ZoomAnimationListener
  /* (non-Javadoc)
	 * @see com.polites.android.Animation#update(com.polites.android.GestureImageView, long)
	 */
  fun update(view:GestureImageView, time:Long):Boolean {
    if (firstFrame)
    {
      firstFrame = false
      startX = view.getImageX()
      startY = view.getImageY()
      startScale = view.getScale()
      scaleDiff = (zoom * startScale) - startScale
      if (scaleDiff > 0)
      {
        // Calculate destination for midpoint
        val vector = VectorF()
        // Set the touch point as start because we want to move the end				
        vector.setStart(PointF(touchX, touchY))
        vector.setEnd(PointF(startX, startY))
        vector.calculateAngle()
        // Get the current length
        val length = vector.calculateLength()
        // Multiply length by zoom to get the new length
        vector.length = length * zoom
        // Now deduce the new endpoint
        vector.calculateEndPoint()
        xDiff = vector.end.x - startX
        yDiff = vector.end.y - startY
      }
      else
      {
        // Zoom out to center
        xDiff = view.getCenterX() - startX
        yDiff = view.getCenterY() - startY
      }
    }
    totalTime += time
    val ratio = totalTime.toFloat() / animationLengthMS.toFloat()
    if (ratio < 1)
    {
      if (ratio > 0)
      {
        // we still have time left
        val newScale = (ratio * scaleDiff) + startScale
        val newX = (ratio * xDiff) + startX
        val newY = (ratio * yDiff) + startY
        if (zoomAnimationListener != null)
        {
          zoomAnimationListener.onZoom(newScale, newX, newY)
        }
      }
      return true
    }
    else
    {
      val newScale = scaleDiff + startScale
      val newX = xDiff + startX
      val newY = yDiff + startY
      if (zoomAnimationListener != null)
      {
        zoomAnimationListener.onZoom(newScale, newX, newY)
        zoomAnimationListener.onComplete()
      }
      return false
    }
  }
  fun reset() {
    firstFrame = true
    totalTime = 0
  }
}
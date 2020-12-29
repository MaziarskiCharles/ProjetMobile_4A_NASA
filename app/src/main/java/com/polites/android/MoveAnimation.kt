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
/**
* @author Jason Polites
*
*/
class MoveAnimation:Animation {
  private val firstFrame = true
  private val startX:Float = 0.toFloat()
  private val startY:Float = 0.toFloat()
  var targetX:Float = 0.toFloat()
  var targetY:Float = 0.toFloat()
  var animationTimeMS:Long = 100
  private val totalTime:Long = 0
  private val moveAnimationListener:MoveAnimationListener
  /* (non-Javadoc)
	 * @see com.polites.android.Animation#update(com.polites.android.GestureImageView, long)
	 */
  fun update(view:GestureImageView, time:Long):Boolean {
    totalTime += time
    if (firstFrame)
    {
      firstFrame = false
      startX = view.getImageX()
      startY = view.getImageY()
    }
    if (totalTime < animationTimeMS)
    {
      val ratio = totalTime.toFloat() / animationTimeMS
      val newX = ((targetX - startX) * ratio) + startX
      val newY = ((targetY - startY) * ratio) + startY
      if (moveAnimationListener != null)
      {
        moveAnimationListener.onMove(newX, newY)
      }
      return true
    }
    else
    {
      if (moveAnimationListener != null)
      {
        moveAnimationListener.onMove(targetX, targetY)
      }
    }
    return false
  }
  fun reset() {
    firstFrame = true
    totalTime = 0
  }
  fun setMoveAnimationListener(moveAnimationListener:MoveAnimationListener) {
    this.moveAnimationListener = moveAnimationListener
  }
}
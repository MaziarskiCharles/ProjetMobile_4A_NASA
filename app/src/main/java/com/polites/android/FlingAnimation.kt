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
class FlingAnimation:Animation {
  private val velocityX:Float = 0.toFloat()
  private val velocityY:Float = 0.toFloat()
  private val factor = 0.95f
  private val threshold = 10f
  private val listener:FlingAnimationListener
  /* (non-Javadoc)
	 * @see com.polites.android.Transformer#update(com.polites.android.GestureImageView, long)
	 */
  fun update(view:GestureImageView, time:Long):Boolean {
    val seconds = time.toFloat() / 1000.0f
    val dx = velocityX * seconds
    val dy = velocityY * seconds
    velocityX *= factor
    velocityY *= factor
    val active = (Math.abs(velocityX) > threshold && Math.abs(velocityY) > threshold)
    if (listener != null)
    {
      listener.onMove(dx, dy)
      if (!active)
      {
        listener.onComplete()
      }
    }
    return active
  }
  fun setVelocityX(velocityX:Float) {
    this.velocityX = velocityX
  }
  fun setVelocityY(velocityY:Float) {
    this.velocityY = velocityY
  }
  fun setFactor(factor:Float) {
    this.factor = factor
  }
  fun setListener(listener:FlingAnimationListener) {
    this.listener = listener
  }
}
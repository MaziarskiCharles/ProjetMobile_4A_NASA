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
import android.view.MotionEvent
class MathUtils {
  /**
	 * Rotates p1 around p2 by angle degrees.
	 * @param p1
	 * @param p2
	 * @param angle
	 */
  fun rotate(p1:PointF, p2:PointF, angle:Float) {
    val px = p1.x
    val py = p1.y
    val ox = p2.x
    val oy = p2.y
    p1.x = (Math.cos(angle.toDouble()) * (px - ox) - Math.sin(angle.toDouble()) * (py - oy) + ox).toFloat()
    p1.y = (Math.sin(angle.toDouble()) * (px - ox) + Math.cos(angle.toDouble()) * (py - oy) + oy.toDouble()).toFloat()
  }
  companion object {
    fun distance(event:MotionEvent):Float {
      val x = event.getX(0) - event.getX(1)
      val y = event.getY(0) - event.getY(1)
      return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }
    fun distance(p1:PointF, p2:PointF):Float {
      val x = p1.x - p2.x
      val y = p1.y - p2.y
      return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }
    fun distance(x1:Float, y1:Float, x2:Float, y2:Float):Float {
      val x = x1 - x2
      val y = y1 - y2
      return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }
    fun midpoint(event:MotionEvent, point:PointF) {
      val x1 = event.getX(0)
      val y1 = event.getY(0)
      val x2 = event.getX(1)
      val y2 = event.getY(1)
      midpoint(x1, y1, x2, y2, point)
    }
    fun midpoint(x1:Float, y1:Float, x2:Float, y2:Float, point:PointF) {
      point.x = (x1 + x2) / 2.0f
      point.y = (y1 + y2) / 2.0f
    }
    fun angle(p1:PointF, p2:PointF):Float {
      return angle(p1.x, p1.y, p2.x, p2.y)
    }
    fun angle(x1:Float, y1:Float, x2:Float, y2:Float):Float {
      return Math.atan2((y2 - y1).toDouble(), (x2 - x1).toDouble()).toFloat()
    }
  }
}

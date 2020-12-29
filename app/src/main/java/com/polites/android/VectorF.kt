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
import android.util.FloatMath
import android.view.MotionEvent
class VectorF {
  var angle:Float = 0.toFloat()
  var length:Float = 0.toFloat()
  val start = PointF()
  val end = PointF()
  fun calculateEndPoint() {
    end.x = (Math.cos(angle.toDouble()) * length + start.x) as Float
    end.y = (Math.sin(angle.toDouble()) * length + start.y) as Float
  }
  fun setStart(p:PointF) {
    this.start.x = p.x
    this.start.y = p.y
  }
  fun setEnd(p:PointF) {
    this.end.x = p.x
    this.end.y = p.y
  }
  fun set(event:MotionEvent) {
    this.start.x = event.getX(0)
    this.start.y = event.getY(0)
    this.end.x = event.getX(1)
    this.end.y = event.getY(1)
  }
  fun calculateLength():Float {
    length = MathUtils.distance(start, end)
    return length
  }
  fun calculateAngle():Float {
    angle = MathUtils.angle(start, end)
    return angle
  }
}
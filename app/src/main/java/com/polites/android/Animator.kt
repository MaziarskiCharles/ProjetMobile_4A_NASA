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
class Animator(view:GestureImageView, threadName:String):Thread(threadName) {
  private val view:GestureImageView
  private val animation:Animation
  private val running = false
  private val active = false
  private val lastTime = -1L
  init{
    this.view = view
  }
  public override fun run() {
    running = true
    while (running)
    {
      while (active && animation != null)
      {
        val time = System.currentTimeMillis()
        active = animation.update(view, time - lastTime)
        view.redraw()
        lastTime = time
        while (active)
        {
          try
          {
            if (view.waitForDraw(32))
            { // 30Htz
              break
            }
          }
          catch (ignore:InterruptedException) {
            active = false
          }
        }
      }
      synchronized (this) {
        if (running)
        {
          try
          {
            wait()
          }
          catch (ignore:InterruptedException) {}
        }
      }
    }
  }
  @Synchronized fun finish() {
    running = false
    active = false
    notifyAll()
  }
  fun play(transformer:Animation) {
    if (active)
    {
      cancel()
    }
    this.animation = transformer
    activate()
  }
  @Synchronized fun activate() {
    lastTime = System.currentTimeMillis()
    active = true
    notifyAll()
  }
  fun cancel() {
    active = false
  }
}
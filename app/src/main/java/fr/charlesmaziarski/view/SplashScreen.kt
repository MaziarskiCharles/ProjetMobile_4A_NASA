package fr.charlesmaziarski.view
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import fr.charlesmaziarski.R
class SplashScreen:AppCompatActivity(), View.OnClickListener {
  protected fun onCreate(savedInstanceState:Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.splashscreen)
    val l = findViewById(R.id.splash)
    l.setOnClickListener(this)
    val timer = object:Thread() {
      public override fun run() {
        try
        {
          Thread.sleep(2000)
        }
        catch (e:Exception) {
          e.printStackTrace()
        }
        finally
        {
          val intent = Intent(this@SplashScreen, MainActivity::class.java)
          startActivity(intent)
        }
      }
    }
    timer.start()
  }
  fun onClick(v:View) {}
}
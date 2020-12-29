package fr.charlesmaziarski.model
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import fr.charlesmaziarski.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class NASA_Call_Back(app:AppCompatActivity):Callback<NASA_Item> {
  private val app:AppCompatActivity
  val item:NASA_Item
  init{
    this.app = app
  }
  fun onResponse(call:Call<NASA_Item>, response:Response<NASA_Item>) {
    item = response.body()
    if (item != null)
    {
      Picasso.get().load(item.getUrl()).into(app.findViewById(R.id.image) as ImageView)
      val texte = app.findViewById(R.id.title)
      texte.setText(item.getTitle())
    }
  }
  fun onFailure(call:Call<NASA_Item>, throwable:Throwable) {
    println("Erreur dans la requête GET :")
    throwable.printStackTrace()
  }
}
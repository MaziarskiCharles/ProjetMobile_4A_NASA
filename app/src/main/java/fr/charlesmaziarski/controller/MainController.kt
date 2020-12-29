package fr.charlesmaziarski.controller
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone
import fr.charlesmaziarski.model.NASA_Item
import fr.charlesmaziarski.view.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class MainController(mainActivity:MainActivity) {
  private val mainActivity:MainActivity
  private val alNASA:ArrayList<NASA_Item>
  init{
    this.mainActivity = mainActivity
    this.alNASA = ArrayList<NASA_Item>()
  }
  fun start() {
    val today = Date()
    val cal = GregorianCalendar()
    cal.setTime(today)
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    formatter.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"))
    val r = RetroFitClient()
    val callAsync:Call<NASA_Item>
    for (i in 0..9)
    {
      callAsync = r.getService(RetroFitClient.API_URL).getNASA_Item(RetroFitClient.API_KEY, formatter.format(cal.getTime()))
      callAsync.enqueue(object:Callback<NASA_Item>() {
        fun onResponse(call:Call<NASA_Item>, response:Response<NASA_Item>) {
          alNASA.add(response.body())
          Collections.sort(alNASA, Collections.reverseOrder<NASA_Item>())
          mainActivity.showList(alNASA)
        }
        fun onFailure(call:Call<NASA_Item>, throwable:Throwable) {
          println("Erreur dans la requête GET :")
          throwable.printStackTrace()
        }
      })
      cal.add(Calendar.DAY_OF_MONTH, -1)
    }
  }
}

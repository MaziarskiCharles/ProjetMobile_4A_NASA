package fr.charlesmaziarski.controller
import java.util.ArrayList
import fr.charlesmaziarski.model.NASA_Item
import fr.charlesmaziarski.view.Detail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import fr.charlesmaziarski.controller.RetroFitClient.API_KEY
import fr.charlesmaziarski.controller.RetroFitClient.API_URL
class DetailController(detailActivity:Detail) {
  private val detailActivity:Detail
  private val alNASA:ArrayList<NASA_Item>
  init{
    this.detailActivity = detailActivity
    this.alNASA = ArrayList<NASA_Item>()
  }
  fun start() {
    val r = RetroFitClient()
    val callAsync = r.getService(API_URL).getNASA_Item(API_KEY, detailActivity.getDate())
    callAsync.enqueue(object:Callback<NASA_Item>() {
      fun onResponse(call:Call<NASA_Item>, response:Response<NASA_Item>) {
        detailActivity.showNASA_ItemFromDate(response.body())
      }
      fun onFailure(call:Call<NASA_Item>, throwable:Throwable) {
        println("Erreur dans la requête GET :")
        throwable.printStackTrace()
      }
    })
  }
}
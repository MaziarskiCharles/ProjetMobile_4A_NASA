package fr.charlesmaziarski.controller
import fr.charlesmaziarski.model.NASA_Service
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class RetroFitClient {
  fun getService(url:String):NASA_Service {
    val httpClient = OkHttpClient.Builder()
    val retrofit = Retrofit.Builder()
    .baseUrl(url)
    .addConverterFactory(GsonConverterFactory.create())
    .client(httpClient.build())
    .build()
    return retrofit.create(NASA_Service::class.java)
  }
  companion object {
    val API_URL = "https://api.nasa.gov/"
    val API_KEY = "NjtGhAKtV5JsG1wyu9Kir7ZD70IQmu95VbPNGzJW"
  }
}
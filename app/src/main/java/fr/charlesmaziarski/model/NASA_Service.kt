package fr.charlesmaziarski.model
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
interface NASA_Service {
  @GET("planetary/apod")
  fun getNASA_Item(@Query("api_key") api_key:String, @Query("date") date:String):Call<NASA_Item>
}
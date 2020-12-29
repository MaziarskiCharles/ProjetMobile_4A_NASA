package fr.charlesmaziarski.model
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
class NASA_Item protected constructor(`in`:Parcel):Comparable<NASA_Item>, Parcelable {
  @SerializedName("copyright")
  @Expose
  var copyright:String
  @SerializedName("date")
  @Expose
  var date:String
  @SerializedName("explanation")
  @Expose
  var explanation:String
  @SerializedName("hdurl")
  @Expose
  var hdurl:String
  @SerializedName("media_type")
  @Expose
  var mediaType:String
  @SerializedName("service_version")
  @Expose
  var serviceVersion:String
  @SerializedName("title")
  @Expose
  var title:String
  @SerializedName("url")
  @Expose
  var url:String
  public override fun compareTo(o:NASA_Item):Int {
    val current = this.date.split(("-").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
    val compare = o.date.split(("-").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
    if (Integer.parseInt(current[0]) == Integer.parseInt(compare[0]))
    {
      if (Integer.parseInt(current[1]) == Integer.parseInt(compare[1]))
      {
        if (Integer.parseInt(current[2]) == Integer.parseInt(compare[2]))
        {
          return 0
        }
        else
        {
          return Integer.compare(Integer.parseInt(current[2]), Integer.parseInt(compare[2]))
        }
      }
      else
      {
        return Integer.compare(Integer.parseInt(current[1]), Integer.parseInt(compare[1]))
      }
    }
    return Integer.compare(Integer.parseInt(current[0]), Integer.parseInt(compare[0]))
  }
  init{
    explanation = `in`.readString()
    title = `in`.readString()
    url = `in`.readString()
    mediaType = `in`.readString()
    date = `in`.readString()
  }
  fun describeContents():Int {
    return 0
  }
  fun writeToParcel(dest:Parcel, flags:Int) {
    dest.writeString(explanation)
    dest.writeString(title)
    dest.writeString(url)
    dest.writeString(mediaType)
    dest.writeString(date)
  }
  companion object {
    val CREATOR:Parcelable.Creator<NASA_Item> = object:Parcelable.Creator<NASA_Item>() {
      fun createFromParcel(`in`:Parcel):NASA_Item {
        return NASA_Item(`in`)
      }
      fun newArray(size:Int):Array<NASA_Item> {
        return arrayOfNulls<NASA_Item>(size)
      }
    }
  }
}
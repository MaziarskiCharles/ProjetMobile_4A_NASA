package fr.charlesmaziarski.controller
import java.util.regex.Matcher
import java.util.regex.Pattern
object Utils {
  fun getYoutubeIDFromURL(url:String):String {
    val pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*"
    val compiledPattern = Pattern.compile(pattern)
    val matcher = compiledPattern.matcher(url)
    val id = ""
    if (matcher.find())
    {
      return matcher.group()
    }
    return id
  }
}
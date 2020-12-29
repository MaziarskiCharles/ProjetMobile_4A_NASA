package fr.charlesmaziarski.view
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.polites.android.GestureImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import fr.charlesmaziarski.controller.DetailController
import fr.charlesmaziarski.controller.Utils
import fr.charlesmaziarski.model.NASA_Item
import fr.charlesmaziarskin.R
class Detail:AppCompatActivity() {
  private val controller:DetailController
  val date:String
  private val extras:Bundle
  internal var textDesc:TextView
  internal var textTitle:TextView
  internal var imageView:GestureImageView
  protected fun onCreate(savedInstanceState:Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.fragment_detail)
    extras = getIntent().getExtras()
    this.setViews()
    this.controller = DetailController(this)
    if (extras.getString("mode").equals("date"))
    {
      this.date = extras.getString("date")
      this.controller.start()
    }
    else
    {
      val item = extras.getParcelable("item")
      this.showNASA_Item(item)
    }
  }
  fun setViews() {
    this.textDesc = findViewById(R.id.desc)
    this.textTitle = findViewById(R.id.title)
    this.imageView = findViewById(R.id.image)
  }
  fun showNASA_ItemFromDate(item:NASA_Item) {
    val title = item.getTitle() + "\n" + item.getDate()
    textTitle.setText(title)
    textDesc.setText(item.getExplanation())
    if (item.getMediaType().equals("video"))
    {
      val url = "https://img.youtube.com/vi/" + Utils.getYoutubeIDFromURL(item.getUrl()) + "/hqdefault.jpg"
      Picasso.get().load(url).into(imageView)
    }
    else
    {
      Picasso.get().load(item.getUrl()).into(imageView)
    }
  }
  fun showNASA_Item(item:NASA_Item) {
    val title = item.getTitle() + "\n" + item.getDate()
    textTitle.setText(title)
    textDesc.setText(item.getExplanation())
    val imageTransitionName = extras.getString("transition")
    imageView.setTransitionName(imageTransitionName)
    Picasso.get().load(item.getUrl()).noFade().into(imageView,
                                                    object:Callback() {
                                                      fun onSuccess() {
                                                        supportStartPostponedEnterTransition()
                                                      }
                                                      fun onError(e:Exception) {
                                                        supportStartPostponedEnterTransition()
                                                      }
                                                    }
                                                   )
  }
}
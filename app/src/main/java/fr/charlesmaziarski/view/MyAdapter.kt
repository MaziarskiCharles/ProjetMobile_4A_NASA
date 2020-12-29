package fr.charlesmaziarski.view
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import java.util.Random
import java.util.TimeZone
import fr.charlesmaziarski.R
import fr.charlesmaziarski.controller.Utils
import fr.charlesmaziarski.model.ItemClickListener
import fr.charlesmaziarski.model.NASA_Item
class MyAdapter//Constructeur
(myDataset:List<NASA_Item>, context:MainActivity):RecyclerView.Adapter<MyAdapter.MyViewHolder>(), Filterable {
  //private final AdapterView.OnItemClickListener listener;
  private val local_Dataset:List<NASA_Item>
  private val base_Dataset:List<NASA_Item>
  private val context:MainActivity
  private val listener:ItemClickListener
  val filter:Filter
  get() {
    return object:Filter() {
      protected fun publishResults(constraint:CharSequence, results:FilterResults) {
        local_Dataset = results.values as List<NASA_Item>
        notifyDataSetChanged()
      }
      protected fun performFiltering(constraint:CharSequence):FilterResults {
        val filteredResults:List<NASA_Item> = null
        if (constraint.length == 0)
        {
          filteredResults = base_Dataset
        }
        else
        {
          filteredResults = getFilteredResults(constraint.toString().toLowerCase())
        }
        val results = FilterResults()
        results.values = filteredResults
        return results
      }
    }
  }
  //renvoi la taille du dataset
  val itemCount:Int
  get() {
    return local_Dataset.size
  }
  protected fun getFilteredResults(constraint:String):List<NASA_Item> {
    val results = ArrayList<NASA_Item>()
    for (item in base_Dataset)
    {
      if (item.getTitle().toLowerCase().contains(constraint))
      {
        results.add(item)
      }
    }
    return results
  }
  //gere la vue de chaque donnee
  inner class MyViewHolder(v:View):RecyclerView.ViewHolder(v) {
    var layout:View
    private val textViewView:TextView
    private val month:TextView
    private val day:TextView
    private val imageView:ImageView
    private val linearLayout:LinearLayout
    init{
      layout = v
      textViewView = itemView.findViewById(R.id.text) as TextView
      linearLayout = itemView.findViewById(R.id.linear) as LinearLayout
      month = itemView.findViewById(R.id.month) as TextView
      day = itemView.findViewById(R.id.day) as TextView
      imageView = itemView.findViewById(R.id.image) as ImageView
      val r = Random().nextInt(5)
    }
  }
  init{
    this.listener = context
    this.context = context
    local_Dataset = (myDataset)
    base_Dataset = (myDataset)
  }
  //creation des nouvelles vues par le layout manager
  fun onCreateViewHolder(parent:ViewGroup, viewType:Int):MyAdapter.MyViewHolder {
    //creation d'une nouvelle vue
    val v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_list, parent, false)
    val vh = MyViewHolder(v)
    return vh
  }
  fun filter(text:String) {
    local_Dataset.clear()
    if (text.isEmpty())
    {
      local_Dataset.addAll(base_Dataset)
    }
    else
    {
      text = text.toLowerCase()
      for (item in base_Dataset)
      {
        if (item.getTitle().toLowerCase().contains(text))
        {
          local_Dataset.add(item)
        }
      }
    }
    notifyDataSetChanged()
  }
  //remplace le contenu de la vue
  fun onBindViewHolder(holder:MyViewHolder, position:Int) {
    //on prend un l'element de notre base a cette position
    //Remplace la vue par son contenu
    val item = local_Dataset.get(position)
    val cal = GregorianCalendar()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    try
    {
      cal.setTime(sdf.parse(item.getDate()))
    }
    catch (e:ParseException) {
      e.printStackTrace()
    }
    sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"))
    holder.month.setText(SimpleDateFormat("MMMM", Locale.ENGLISH).format(cal.getTime()))
    holder.day.setText(String.format("%s", cal.get(Calendar.DAY_OF_MONTH)))
    val title = item.getTitle()
    val custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/stellar.otf")
    holder.textViewView.setTypeface(custom_font)
    holder.textViewView.setText(title)
    if (item.getMediaType().equals("video"))
    {
      val url = "https://img.youtube.com/vi/" + Utils.getYoutubeIDFromURL(item.getUrl()) + "/hqdefault.jpg"
      Picasso.get().load(url).into(holder.imageView)
    }
    else
    {
      Picasso.get().load(item.getUrl()).into(holder.imageView)
    }
    holder.itemView.setOnClickListener(object:View.OnClickListener() {
      fun onClick(v:View) {
        listener.onItemClick(item, holder.imageView)
      }
    })
  }
}
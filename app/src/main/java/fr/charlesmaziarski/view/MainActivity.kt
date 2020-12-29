package fr.charlesmaziarski.view
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import fr.charlesmaziarski.R
import fr.charlesmaziarski.controller.MainController
import fr.charlesmaziarski.model.ItemClickListener
import fr.charlesmaziarski.model.NASA_Item
class MainActivity:AppCompatActivity(), ItemClickListener, View.OnClickListener {
  var controller:MainController
  private val adapter:MyAdapter
  private val myCalendar = Calendar.getInstance()
  internal var recyclerView:RecyclerView
  private val textTitle:TextView
  private val button:ImageButton
  private val searchView:SearchView
  internal var date:DatePickerDialog.OnDateSetListener
  protected fun onCreate(savedInstanceState:Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    this.setViews()
    this.setFont()
    searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener() {
      fun onQueryTextSubmit(text:String):Boolean {
        return false
      }
      fun onQueryTextChange(text:String):Boolean {
        adapter.getFilter().filter(text)
        return true
      }
    })
    date = object:DatePickerDialog.OnDateSetListener() {
      fun onDateSet(view:DatePicker, year:Int, month:Int, dayOfMonth:Int) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, month)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        openDetail(sdf.format(myCalendar.getTime()))
      }
    }
    controller = MainController(this)
    controller.start()
  }
  fun setViews() {
    findViewById(R.id.fragment_container).setVisibility(View.GONE)
    textTitle = findViewById(R.id.toolbar_title)
    button = findViewById(R.id.button)
    button.setOnClickListener(this)
    searchView = findViewById(R.id.search)
  }
  fun setFont() {
    val custom_font = Typeface.createFromAsset(getAssets(), "fonts/stargaze.ttf")
    textTitle.setTypeface(custom_font)
  }
  fun openDetail(date:String) {
    val intent = Intent(this, Detail::class.java)
    intent.putExtra("mode", "date")
    intent.putExtra("date", date)
    startActivity(intent)
  }
  fun showList(list:List<NASA_Item>) {
    recyclerView = findViewById(R.id.recyclerView)
    if (getResources().getConfiguration().orientation === Configuration.ORIENTATION_LANDSCAPE)
    {
      recyclerView.setLayoutManager(GridLayoutManager(this, 2))
    }
    else
    {
      recyclerView.setLayoutManager(LinearLayoutManager(this))
    }
    adapter = MyAdapter(list, this)
    recyclerView.setAdapter(adapter)
  }
  fun onItemClick(item:NASA_Item, imageView:ImageView) {
    val intent = Intent(this, Detail::class.java)
    intent.putExtra("item", item)
    intent.putExtra("mode", "transition")
    intent.putExtra("transition", ViewCompat.getTransitionName(imageView))
    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
      this, imageView, ViewCompat.getTransitionName(imageView))
    startActivity(intent, options.toBundle())
  }
  fun onClick(v:View) {
    val format = SimpleDateFormat("dd-MM-YYYY", Locale.US)
    val d:Date
    val c = Calendar.getInstance()
    c.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"))
    val dp = DatePickerDialog(this@MainActivity, this.date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH) - 1)
    c.add(Calendar.DAY_OF_MONTH, -1)
    dp.getDatePicker().setMaxDate(c.getTimeInMillis())
    try
    {
      d = format.parse("16-06-1996")
      c.setTime(d)
      dp.getDatePicker().setMinDate(c.getTimeInMillis())
    }
    catch (e:ParseException) {
      e.printStackTrace()
    }
    dp.show()
  }
  fun onBackPressed() {
    //do noting
  }
}
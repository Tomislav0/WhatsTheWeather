package com.example.whatstheweather
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.view.Gravity
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatstheweather.Adapters.HourRVAdapter
import com.example.whatstheweather.Adapters.SearchedRVAdapter
import com.example.whatstheweather.Adapters.WeeklyRVAdapter
import com.example.whatstheweather.Data.DTOs.Weather
import com.example.whatstheweather.Data.Modals.HourRVModal
import com.example.whatstheweather.Data.Modals.SearchedRVModal
import com.example.whatstheweather.Data.Modals.WeeklyRVModal
import com.example.whatstheweather.Services.APIEndpoints
import com.example.whatstheweather.Services.ServiceBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.*
import javax.annotation.RegEx

class MainActivity : AppCompatActivity() ,SearchedRVAdapter.OnItemClickListener{
    lateinit var searchET :EditText
    lateinit var searchIV :ImageView
    lateinit var db : FirebaseFirestore
    lateinit var cityNameTV:TextView
    lateinit var weeklyRVModalElements :  ArrayList<WeeklyRVModal>
    lateinit var temperatureTV : TextView
    lateinit var mainIconIV:ImageView
    lateinit var mainInfoTV :TextView
    lateinit var weeklyRVAdapter:WeeklyRVAdapter
    lateinit var weeklyRV : RecyclerView
    lateinit var precipTV: TextView
    lateinit var humidityTV:TextView
    lateinit var windTV : TextView
    lateinit var searchedRVModalElements : ArrayList<SearchedRVModal>
    lateinit var searchedRVAdapter : SearchedRVAdapter
    lateinit var searchedRV:RecyclerView
    lateinit var settingsIV :ImageView
    val PERMISSION_CODE :Int= 1
    lateinit var clockIV :ImageView
    lateinit var hourRVModalElements : ArrayList<HourRVModal>
    lateinit var hourRVAdapter : HourRVAdapter
    lateinit var progressBar: ProgressBar
    lateinit var mainCL :ConstraintLayout
    lateinit var lastUpdTV:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDeclaration()
        progressBar.visibility = View.VISIBLE
        mainCL.visibility=View.INVISIBLE



        predefinedLocations()
        val handler = Handler()
        handler.postDelayed(Runnable {
            progressBar.visibility = View.INVISIBLE
            mainCL.visibility=View.VISIBLE
        }, 1000)
        searchIV.setOnClickListener{
            val city:String = searchET.text.toString()
            if(city.isEmpty()){
                Toast.makeText(this,"Please enter city name", Toast.LENGTH_SHORT).show()
            } else{
                searchET.text.clear()
                addToSearchedDB(city)
                weeklyRVModalElements.clear()
                searchedRVModalElements.clear()
                cityNameTV.setText(city)
                getWeatherInfo(city)
                getLastSearched()
            }
        }


        settingsIV.setOnClickListener{

            val view = View.inflate(this,R.layout.settings_dialog,null)
            val builder = AlertDialog.Builder(this,R.style.WrapContentDialog)
            builder.setView(view)
            val dialog = builder.create()
            dialog.window!!.setBackgroundDrawable(getDrawable(R.drawable.dialog_background))
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window!!.setGravity(Gravity.CENTER);
            dialog.setCanceledOnTouchOutside(false)
            val curLocBtn = view.findViewById<RadioButton>(R.id.idRBOpt1)
            val customLocBtn = view.findViewById<RadioButton>(R.id.idRBOpt2)
            val locET = view.findViewById<EditText>(R.id.idETLocation)
            locET.isVisible = false
            db.collection("startup").document("1")
                .get()
                .addOnSuccessListener {
                    val data=it["isCurrLoc"] as Boolean
                    if(data){
                        curLocBtn.isChecked=true
                    }
                    else{
                        db.collection("startup").document("2")
                            .get()
                            .addOnSuccessListener {
                                val city = it["location"] as String
                                customLocBtn.isChecked=true
                                locET.isVisible = true
                                locET.setText(city)
                            }
                            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                    }

                }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
            dialog.show()
            curLocBtn.setOnClickListener{
                locET.isVisible = false
                }
            customLocBtn.setOnClickListener{
                locET.isVisible = true
            }


            view.findViewById<Button>(R.id.idBtnSave).setOnClickListener{
                Toast.makeText(this , "Successfully saved", Toast.LENGTH_SHORT).show()
                if(curLocBtn.isChecked){
                    val data = hashMapOf("isCurrLoc" to true)
                    db.collection("startup").document("1")
                        .set(data, SetOptions.merge())
                }
                else{
                    val data = hashMapOf("location" to locET.text.toString())
                    db.collection("startup").document("2")
                        .set(data, SetOptions.merge())
                    val data1 = hashMapOf("isCurrLoc" to false)
                    db.collection("startup").document("1")
                        .set(data1, SetOptions.merge())
                }
                predefinedLocations()
                dialog.dismiss()
                }
            view.findViewById<Button>(R.id.idBtnCancle).setOnClickListener{
                dialog.dismiss()
            }
        }
        clockIV.setOnClickListener{
            val dialogBox = Dialog(this)
            dialogBox.setContentView(R.layout.hour_dialog)
            dialogBox.findViewById<RecyclerView>(R.id.idRVHour).apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = HourRVAdapter(this@MainActivity,hourRVModalElements)
                dialogBox.window!!.setBackgroundDrawable(getDrawable(R.drawable.dialog_background))
                dialogBox.show()
            }


        }


    }

    fun predefinedLocations(){
        db.collection("startup").document("1")
            .get()
            .addOnSuccessListener {
                val data=it["isCurrLoc"] as Boolean
                if(data){
                    val loactionManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    if(ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(this, arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_CODE)
                    }
                    var location : Location = loactionManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) as Location
                    val city = getCityName(location.longitude,location.latitude)
                    weeklyRVModalElements.clear()
                    searchedRVModalElements.clear()
                    cityNameTV.setText(city)
                    getWeatherInfo(city)
                    getLastSearched()
                }
                else{
                    db.collection("startup").document("2")
                        .get()
                        .addOnSuccessListener {
                            val city = it["location"] as String
                            weeklyRVModalElements.clear()
                            searchedRVModalElements.clear()
                            cityNameTV.setText(city)
                            getWeatherInfo(city)
                            getLastSearched()
                        }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                }

            }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

    }
    private fun getCityName(longitude :Double,latitude :Double):String{
        var cityName:String = "Not found"
        val geo : Geocoder = Geocoder(baseContext, Locale.getDefault())
        try{
            val adresses : List<Address> = geo.getFromLocation(latitude, longitude,10)
            for(adr: Address in adresses){
                if(adr!=null){
                    var city:String = adr.locality
                    if(city!=null && !city.equals("")){
                        cityName = city
                    }else{
                        Log.d("TAG","CITY NOT FOUND")
                        Toast.makeText(this,"User city not found",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return cityName
    }

    fun addToSearchedDB(city:String){
        val searched = hashMapOf(
            "name" to city,
            "date" to Calendar.getInstance().time
        )

        db.collection("search").document()
            .set(searched)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }
    private fun getSearchedFromApi(cities:ArrayList<String>) {
        val request = ServiceBuilder.buildService(APIEndpoints::class.java)
        for (i in 0..cities.size-1){
            val call = request.getWeather(cities[i])
            call.enqueue(object : Callback<Weather> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<Weather>, response:
                Response<Weather>
                ) {
                    if(response.isSuccessful) {
                        val nameSearched = cities[i]
                        val imageSearched = response.body()!!.current.condition.icon
                        val temperatureSearched = response.body()!!.current.temp_c
                        searchedRVModalElements.add(SearchedRVModal(nameSearched,imageSearched,temperatureSearched))

                        findViewById<RecyclerView>(R.id.idRVReacentlySearched).apply {
                            adapter =
                                SearchedRVAdapter(this@MainActivity,searchedRVModalElements,this@MainActivity)
                        }


                    }
                }
                override fun onFailure(call: Call<Weather>, t: Throwable)
                {
                    Toast.makeText(this@MainActivity,"Please enter valid city name!", Toast.LENGTH_SHORT).show()
                    Log.d("FAIL", t.message.toString())

                }
            })

        }

    }

    private fun getLastSearched():ArrayList<String> {
        val searchedData = arrayListOf<SearchedData>()
        val cities = arrayListOf<String>()
        db.collection("search").orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty){
                    Toast.makeText(this,"There is no search history",Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                for(doc in documents){
                    val searchedModel = doc.toObject(SearchedData::class.java)

                    searchedData.add(searchedModel)
                }
                //searchedData.sortByDescending{ it.date }
                for(model in searchedData){
                    Log.d(TAG,"${model.name}")
                }
                for(i in 0..searchedData.size-1) {
                    if (i == 0) {
                        cities.add(searchedData[i].name)
                    } else {
                        if (!cities.contains(searchedData[i].name)) {
                           cities.add(searchedData[i].name)
                      }
                    }
                }
                getSearchedFromApi(cities)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        return cities
    }

    private fun getWeatherInfo(cityName:String){

        val request = ServiceBuilder.buildService(APIEndpoints::class.java)
        val call = request.getWeather(cityName)

        call.enqueue(object : Callback<Weather> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<Weather>, response:
            Response<Weather>
            ) {
                if(response.isSuccessful) {
                    val forecastArray = response.body()!!.forecast.forecastday
                    for (i in 0 until forecastArray.size){
                        val dayObj = forecastArray[i].day
                        val date = forecastArray[i].date
                        val day: String = LocalDate.parse(date).getDayOfWeek().toString()
                        val icon:String= dayObj.condition.icon
                        val max_temp:String= dayObj.maxtemp_c
                        val min_temp:String= dayObj.mintemp_c
                        weeklyRVModalElements.add(WeeklyRVModal(day.toString(),icon,max_temp,min_temp))
                    }
                    val temperature: String = response.body()!!.current.temp_c.split(".").get(0)
                    temperatureTV.setText(temperature+"°C")
                    var isDay:Int =response.body()!!.current.is_day
                    val condition:String =response.body()!!.current.condition.text
                    val conditionIcon:String =response.body()!!.current.condition.icon
                    var day: String = LocalDate.parse(response.body()!!.current.last_updated.split(" ").get(0)).getDayOfWeek().toString()
                    day = day[0].uppercase() +day.substring(1).lowercase()
                    Picasso.get().load("https:".plus(conditionIcon)).into(mainIconIV)
                    mainInfoTV.setText(day+ ", " +condition)
                    val precip = response.body()!!.current.precip_mm
                    val humidity = response.body()!!.current.humidity
                    val wind = response.body()!!.current.wind_kph
                    val lastUpdate = response.body()!!.current.last_updated
                    precipTV.setText("Precip: " + precip + " mm")
                    humidityTV.setText("Humidity: "+humidity+ " %")
                    windTV.setText("Wind: "+ wind + " Km/h")
                    lastUpdTV.setText("Last updated: "+lastUpdate)
                    /*if(isDay==1){
                        Picasso.get().load("https://i.pinimg.com/564x/67/f2/d0/67f2d066cfc38ef66462c4219ad1ae9f.jpg").into(backIV)
                    }else {
                        Picasso.get().load("https://i.pinimg.com/564x/e8/3f/1f/e83f1f000ea89fafe6bd5e12c44af55c.jpg").into(backIV)
                    }*/
                    findViewById<RecyclerView>(R.id.idRVWeekly).apply {
                        adapter =
                            WeeklyRVAdapter(this@MainActivity,weeklyRVModalElements)
                    }
                    hourRVModalElements.clear()
                    val hours = response.body()!!.forecast.forecastday[0].hour
                    for (hour in hours){
                        val time = hour.time
                        val icon = hour.condition.icon
                        val temp = hour.temp_c
                        hourRVModalElements.add(HourRVModal(time,icon,temp))
                    }

                    //Toast.makeText(this@MainActivity,response.body()!!.current.temp_c, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Weather>, t: Throwable)
            {
                Toast.makeText(this@MainActivity,"Please enter valid city name!", Toast.LENGTH_SHORT).show()
                Log.d("FAIL", t.message.toString())

            }
        })
    }
    private fun initDeclaration(){
        searchET = findViewById(R.id.idETSearch)
        searchIV = findViewById(R.id.idIVSearch)
        cityNameTV = findViewById(R.id.idTVCityName)
        temperatureTV = findViewById(R.id.idTVTemperature)
        mainIconIV = findViewById(R.id.idIVMainIcon)
        mainInfoTV = findViewById(R.id.idTVInfo)
        weeklyRVModalElements = ArrayList()
        weeklyRVAdapter = WeeklyRVAdapter(this,weeklyRVModalElements)
        weeklyRV = findViewById(R.id.idRVWeekly)
        weeklyRV.adapter = weeklyRVAdapter
        precipTV = findViewById(R.id.idTVPrecip)
        humidityTV = findViewById(R.id.idTVHumidity)
        windTV = findViewById(R.id.idTVWind)
        searchedRVModalElements = ArrayList()
        searchedRVAdapter = SearchedRVAdapter(this,searchedRVModalElements,this)
        searchedRV = findViewById(R.id.idRVReacentlySearched)
        searchedRV.adapter = searchedRVAdapter
        settingsIV = findViewById(R.id.idIVSettings)
        clockIV = findViewById(R.id.idIVClock)
        hourRVModalElements = ArrayList()
        hourRVAdapter = HourRVAdapter(this,hourRVModalElements)
        progressBar = findViewById(R.id.idPBLoading)
        mainCL = findViewById(R.id.idCLMain)
        lastUpdTV = findViewById(R.id.idTVLastUpdate)
        progressBar.visibility = View.VISIBLE
        mainCL.visibility=View.INVISIBLE
        db = Firebase.firestore
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==PERMISSION_CODE){
            if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permissions granted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Please provide the permissions", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onItemClick(position: Int) {
        val city= searchedRVModalElements[position].cityName
        weeklyRVModalElements.clear()
        cityNameTV.setText(city)
        getWeatherInfo(city)
    }
}

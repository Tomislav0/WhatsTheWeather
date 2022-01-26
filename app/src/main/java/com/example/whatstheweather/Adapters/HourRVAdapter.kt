package com.example.whatstheweather.Adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatstheweather.Data.Modals.HourRVModal
import com.example.whatstheweather.Data.Modals.WeeklyRVModal
import com.example.whatstheweather.R
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class HourRVAdapter(private val context: Context, private val elements:List <HourRVModal>) : RecyclerView.Adapter<HourRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourRVAdapter.ViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.time_rv_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourRVAdapter.ViewHolder, position: Int) {
        val modal : HourRVModal = elements.get(position)
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date()).subSequence(10,12)

        val time = modal.time.subSequence(11,16)
        holder.timeTV.typeface= Typeface.DEFAULT
        if(currentDate==time.subSequence(0,2)){
            holder.timeTV.typeface= Typeface.DEFAULT_BOLD
        }
        else{
            holder.timeTV.typeface= Typeface.DEFAULT
        }
        holder.timeTV.setText(time)
        Picasso.get().load("https:".plus(modal.icon)).into(holder.iconIV)
        holder.tempTV.setText(modal.temperature.split(".").get(0) + "°C")
    }

    override fun getItemCount(): Int {
        return elements.size
    }
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val timeTV : TextView = itemView.findViewById(R.id.idTVHour)
        val iconIV: ImageView =itemView.findViewById(R.id.idIVHourIcon)
        val tempTV : TextView = itemView.findViewById(R.id.idTVHourTemp)
    }
}
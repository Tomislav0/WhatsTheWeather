package com.example.whatstheweather.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatstheweather.Data.Modals.WeeklyRVModal
import com.example.whatstheweather.R
import com.squareup.picasso.Picasso
import kotlin.math.min

class WeeklyRVAdapter(private val context: Context, private val elements:List <WeeklyRVModal>) : RecyclerView.Adapter<WeeklyRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyRVAdapter.ViewHolder {
        val view :View = LayoutInflater.from(context).inflate(R.layout.weekly_rv_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeeklyRVAdapter.ViewHolder, position: Int) {
        val modal :WeeklyRVModal = elements.get(position)
        holder.dayTV.setText(modal.day.subSequence(0,3))
        Picasso.get().load("https:".plus(modal.icon)).into(holder.iconIV)
        var maxtemp= modal.max_temp.split(".").get(0)
        var mintemp= modal.min_temp.split(".").get(0)
        if(maxtemp.startsWith("-") and maxtemp.contains("0")){
            maxtemp = maxtemp.get(1).toString()
        }
        if(mintemp.startsWith("-") and mintemp.contains("0")){
            mintemp = mintemp.get(1).toString()
        }
        holder.max_tempTV.setText(maxtemp + "°C")
        holder.min_tempTV.setText(mintemp+ "°C")
    }

    override fun getItemCount(): Int {
        return elements.size
    }
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val dayTV :TextView = itemView.findViewById(R.id.idTVDay)
        val iconIV:ImageView=itemView.findViewById(R.id.idTVIcon)
        val max_tempTV : TextView = itemView.findViewById(R.id.idTVMaxTemp)
        val min_tempTV: TextView = itemView.findViewById(R.id.idTVMinTemp)
    }
}
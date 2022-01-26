package com.example.whatstheweather.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatstheweather.Data.Modals.SearchedRVModal
import com.example.whatstheweather.Data.Modals.WeeklyRVModal
import com.example.whatstheweather.R
import com.example.whatstheweather.RecyclerItemClickListener
import com.squareup.picasso.Picasso


class SearchedRVAdapter(private val context: Context, private val elements:List <SearchedRVModal>,private val listener:OnItemClickListener) : RecyclerView.Adapter<SearchedRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchedRVAdapter.ViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.searched_rv_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchedRVAdapter.ViewHolder, position: Int) {
        val modal : SearchedRVModal = elements.get(position)
        holder.cityNameTV.setText(modal.cityName)
        Picasso.get().load("https:".plus(modal.icon)).into(holder.iconIV)
        var temp = modal.temperature.split(".").get(0)
        if(temp.startsWith("-") and temp.contains("0")){
            temp = temp.get(1).toString()
        }
        holder.temperatureTV.setText( temp)
    }

    override fun getItemCount(): Int {
        return elements.size
    }
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        val cityNameTV : TextView = itemView.findViewById(R.id.idTVCityNameSearched)
        val iconIV:ImageView=itemView.findViewById(R.id.idIVIconSearched)
        val temperatureTV: TextView=itemView.findViewById(R.id.idTVTemperatureSearcherd)
    init {
        itemView.setOnClickListener(this)
    }
        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

    }
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

}
/*package com.example.whatstheweather

import androidx.recyclerview.widget.LinearLayoutManager

import android.R

import androidx.recyclerview.widget.RecyclerView

import android.os.Bundle

import android.view.ViewGroup

import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import com.example.whatstheweather.Adapters.HourRVAdapter


class MyDialogFragment : DialogFragment() {
    private var mRecyclerView: RecyclerView? = null
    private val adapter: HourRVAdapter? = null

    // this method create view for your Dialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //inflate layout with recycler view
        val v: View = inflater.inflate(R.layout.hour_dialog, container, false)
        mRecyclerView = v.findViewById(R.id.idRVHour)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
        //setadapter
        val adapter: HourRVAdapter = HourRVAdapter(context, customList)
        mRecyclerView!!.adapter = adapter
        //get your recycler view and populate it.
        return v
    }
}*/
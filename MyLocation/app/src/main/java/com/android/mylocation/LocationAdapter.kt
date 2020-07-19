package com.android.mylocation

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_items_locations.view.*
import java.util.*

class LocationAdapter(private val context: Context, private val locationList: ArrayList<Location>) :
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_items_locations, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val list = locationList[position]
        lateinit var addressList: List<Address>
        val geoCoder: Geocoder = Geocoder(context, Locale.getDefault())
        addressList = geoCoder.getFromLocation(list.latitude, list.longitude!!, 1)
        val address = (addressList as MutableList<Address>?)?.get(0)?.getAddressLine(0)

        val currentTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        } else {
            TODO("VERSION.SDK_INT < N")
        }

        holder.latitude?.text = list.latitude.toString()
        holder.longitude?.text = list.longitude.toString()
        holder.address?.text = address
        holder.time?.text = currentTime
    }

    class LocationViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {

        val latitude = view?.textLatitude
        val longitude = view?.textLongitude
        val address = view?.textAddress
        val time = view?.textTime

    }


}
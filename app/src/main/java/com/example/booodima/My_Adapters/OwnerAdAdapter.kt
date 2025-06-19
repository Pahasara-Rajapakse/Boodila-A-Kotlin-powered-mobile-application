package com.example.booodima.My_Adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.booodima.My_Data_Classes.Room
import com.example.booodima.R
import com.example.booodima.fragment_show_owner_ad_information
import java.io.File
import java.text.NumberFormat

class OwnerAdAdapter(
    private val context: Context,
    private var addList: List<Room>
) : RecyclerView.Adapter<OwnerAdAdapter.RoomViewHolder>() {

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomId: TextView = itemView.findViewById(R.id.roomId)
        val roomType: TextView = itemView.findViewById(R.id.roomType)
        val roomBeds: TextView = itemView.findViewById(R.id.roomBeds)
        val roomAddress: TextView = itemView.findViewById(R.id.roomAddress)
        val roomPrice: TextView = itemView.findViewById(R.id.roomPrice)
        val roomImage: ImageView = itemView.findViewById(R.id.roomImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.room_entry, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = addList[position]

        val formattedPrice = formatPrice(room.price)
        val formattedDistance = formatDistance(room.address)

        holder.roomId.text = room.roomId.toString()
        holder.roomType.text = room.roomType
        holder.roomBeds.text = context.getString(R.string.beds_format, room.beds)
        holder.roomAddress.text = context.getString(R.string.ditance, formattedDistance)
        holder.roomPrice.text = context.getString(R.string.price_format, formattedPrice)
        loadImageFromInternalStorage(room.roomId, holder.roomImage)

        holder.itemView.setOnClickListener {
            try {
                val fragment = fragment_show_owner_ad_information().apply {
                    arguments = Bundle().apply {
                        putInt("roomId", room.roomId)
                    }
                }

                if (context is FragmentActivity) {
                    context.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    Log.e("OwnerAdAdapter", "Context is not a FragmentActivity")
                }
            } catch (e: Exception) {
                Log.e("RoomAdapter", "Error showing fragment: ${e.message}")
            }
        }
    }

    override fun getItemCount(): Int = addList.size

    fun updateAddList(newRoomList: List<Room>) {
        addList = newRoomList
        notifyDataSetChanged()
    }

    private fun formatPrice(price: String): String {
        return try {
            NumberFormat.getCurrencyInstance().format(price.toDouble())
        } catch (e: NumberFormatException) {
            price
        }
    }

    private fun formatDistance(address: String): String {
        return address
    }

    private fun loadImageFromInternalStorage(roomId: Int, imageView: ImageView) {
        try {
            val fileName = "room_$roomId.jpg"
            val file = File(context.filesDir, fileName)

            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                imageView.setImageBitmap(bitmap)
            } else {
                Log.w("RoomAdapter", "Image not found for room ID: $roomId")
                imageView.setImageResource(R.drawable.img_1)
            }
        } catch (e: Exception) {
            Log.e("RoomAdapter", "Error loading image for room ID: $roomId", e)
        }
    }
}

// This adapter helps to show the add in the owner end. like a card view.

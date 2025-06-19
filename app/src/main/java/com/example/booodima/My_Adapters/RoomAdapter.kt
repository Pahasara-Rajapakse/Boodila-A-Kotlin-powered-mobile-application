package com.example.booodima.My_Adapters

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.booodima.My_Data_Classes.Room
import com.example.booodima.R
import com.example.booodima.fragment_show_room_ad_information_for_user
import java.io.File
import java.text.NumberFormat

class RoomAdapter(
    private val context: android.content.Context,
    private val fragmentManager: FragmentManager,
    private var roomList: List<Room>
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomId: TextView = itemView.findViewById(R.id.roomId)
        val roomType: TextView = itemView.findViewById(R.id.roomType)
        val roomBeds: TextView = itemView.findViewById(R.id.roomBeds)
        val roomAddress: TextView = itemView.findViewById(R.id.roomAddress)
        val roomPrice: TextView = itemView.findViewById(R.id.roomPrice)
        val roomImage: ImageView = itemView.findViewById(R.id.roomImage)

        fun bind(room: Room) {
            val formattedPrice = try {
                NumberFormat.getCurrencyInstance().format(room.price.toDouble())
            } catch (e: NumberFormatException) {
                Log.w("RoomAdapter", "Invalid price format for room ID: ${room.roomId}, price: ${room.price}")
                room.price
            }

            roomId.text = context.getString(R.string.id_format, room.roomId)
            roomType.text = context.getString(R.string.room_type, room.roomType)
            roomBeds.text = context.getString(R.string.beds_format, room.beds)
            roomAddress.text = context.getString(R.string.ditance, room.address)
            roomPrice.text = context.getString(R.string.price_format, formattedPrice)

            loadImageFromInternalStorage(room.roomId, roomImage)
        }
    }

    private fun loadImageFromInternalStorage(roomId: Int, imageView: ImageView) {
        try {
            // Path to the image file
            val fileName = "room_$roomId.jpg"
            val file = File(context.filesDir, fileName)

            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                imageView.setImageBitmap(bitmap)  // Set the image in the ImageView
            } else {
                Log.w("RoomAdapter", "Image not found for room ID: $roomId")
                // default image
                imageView.setImageResource(R.drawable.img_1)
            }
        } catch (e: Exception) {
            Log.e("RoomAdapter", "Error loading image for room ID: $roomId", e)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.room_entry, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = roomList[position]
        holder.bind(room)

        holder.itemView.setOnClickListener {
            try {
                val fragment = fragment_show_room_ad_information_for_user().apply {
                    arguments = Bundle().apply {
                        // Pass roomId as an argument
                        putInt("roomId", room.roomId)
                    }
                }

                fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_student, fragment)
                    .addToBackStack(null)
                    .commit()

            } catch (e: Exception) {
                Log.e("RoomAdapter", "Error showing fragment: ${e.message}", e)
            }
        }
    }

    fun updateData(newRoomList: List<Room>) {
        val diffResult = DiffUtil.calculateDiff(RoomDiffCallback(roomList, newRoomList))
        roomList = newRoomList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = roomList.size

    class RoomDiffCallback(
        private val oldList: List<Room>,
        private val newList: List<Room>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].roomId == newList[newItemPosition].roomId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}

// This adapter helps to show the add in the student end like ownerAdapter.

package com.example.booodima

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.booodima.My_Database.DatabaseHelper
import java.io.File

class fragment_show_room_ad_information_for_user : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(
            R.layout.fragment_show_room_ad_information_for_user,
            container,
            false
        )

        val roomId = arguments?.getInt("roomId", -1) ?: -1
        if (roomId == -1) {
            Toast.makeText(requireContext(), "Invalid room ID", Toast.LENGTH_SHORT).show()
            return rootView
        } else {
            Log.e("from ShowADInfo", "$roomId")
        }

        val databaseHelper = DatabaseHelper(requireContext())
        val adInfo = databaseHelper.getRoomDetailsWithFacilitiesAndOwner(roomId)

        val roomDetails = adInfo["roomDetails"] as? Map<String, Any>
        roomDetails?.let {
            val roomType = it["room_type"] as? String ?: "N/A"
            val price = it["price"] as? Double ?: 0.0
            val location = it["roomLocation"] as? String ?: "N/A"
            val description = it["description"] as? String ?: "N/A"
            val beds = it["num_beds"]?.toString() ?: "N/A"

            rootView.findViewById<TextView>(R.id.boardingPrice).text = "Rs. ${price}0 /="
            rootView.findViewById<TextView>(R.id.adRoomType).text = "$roomType Room"
            rootView.findViewById<TextView>(R.id.numberOfBedsUser).text = "$beds"
            rootView.findViewById<TextView>(R.id.locationDetails).text = "Distance from the University : $location Km"
            rootView.findViewById<TextView>(R.id.distance).text = "Distance from the University : $location Km"
            rootView.findViewById<TextView>(R.id.displayDescription).text = "$description"
        }

        // Extract and display the facilities
        val facilities = adInfo["facilities"] as? List<String> ?: emptyList()
        val facilitiesText = if (facilities.isNotEmpty()) {
            facilities.joinToString(" \n")
        } else {
            "No facilities listed"
        }

        rootView.findViewById<TextView>(R.id.displayFacilities).text = facilitiesText

        // Extract and display the owner details
        val ownerDetails = adInfo["ownerDetails"] as? Map<String, String>
        ownerDetails?.let {
            val ownerName = it["owner_name"] ?: "N/A"
            val ownerMobile = it["mobile"] ?: "N/A"
            val ownerEmail = it["email"] ?: "N/A"
            val ownerAddress = it["address"] ?: "N/A"

            rootView.findViewById<TextView>(R.id.ownerName).text =    "Owner Name       -    $ownerName"
            rootView.findViewById<TextView>(R.id.ownerAddress).text = "Address               -    $ownerAddress"
            rootView.findViewById<TextView>(R.id.ownerPhone).text =   "Mobile                  -    $ownerMobile"
            rootView.findViewById<TextView>(R.id.ownerEmail).text =   "Email                    -    $ownerEmail"
        }

        // Handle the Call button
        rootView.findViewById<Button>(R.id.callButton).setOnClickListener {
            val phone = ownerDetails?.get("mobile")
            phone?.let {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$it"))
                startActivity(dialIntent)
            }
        }

        // Handle the Email button
        rootView.findViewById<Button>(R.id.emailButton).setOnClickListener {
            val email = ownerDetails?.get("email")
            email?.let {
                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$it"))
                startActivity(Intent.createChooser(emailIntent, "Send Email"))
            }
        }

        val imageViewer: ImageView = rootView.findViewById(R.id.imageViewer)
        loadImageFromInternalStorage(roomId, imageViewer)

        return rootView
    }

    private fun loadImageFromInternalStorage(roomId: Int, imageView: ImageView) {
        try {
            val fileName = "room_$roomId.jpg"
            val file = File(context?.filesDir, fileName)

            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                // Set the image in the ImageView
                imageView.setImageBitmap(bitmap)
            } else {
                Log.w("RoomAdapter", "Image not found for room ID: $roomId")
                // default image.
                imageView.setImageResource(R.drawable.img_1)
            }
        } catch (e: Exception) {
            Log.e("RoomAdapter", "Error loading image for room ID: $roomId", e)
        }
    }
}
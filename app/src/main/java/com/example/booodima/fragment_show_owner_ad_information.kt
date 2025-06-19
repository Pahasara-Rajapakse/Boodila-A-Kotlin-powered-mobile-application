package com.example.booodima

import android.content.Intent
import android.graphics.BitmapFactory
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
import androidx.appcompat.app.AlertDialog
import com.example.booodima.My_Database.DatabaseHelper
import com.example.booodima.My_Database.DatabaseHelper.Companion.COLUMN_ROOM_ID
import com.example.booodima.My_Database.DatabaseHelper.Companion.COLUMN_ROOM_STATUS
import com.example.booodima.My_Database.DatabaseHelper.Companion.TABLE_ROOM
import java.io.File

class fragment_show_owner_ad_information : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_show_owner_ad_information, container, false)

        // Retrieve roomId from arguments
        val roomId = arguments?.getInt("roomId") ?: -1
        if (roomId == -1) {
            Toast.makeText(requireContext(), "Invalid room ID", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            return null
        } else {
            Log.e("from ShowADInfo", "$roomId")
        }

        // Fetch data from database
        val databaseHelper = DatabaseHelper(requireContext())
        val adInfo = databaseHelper.getRoomDetailsWithFacilitiesAndOwner(roomId)

        // Populate room details
        val roomDetails = adInfo["roomDetails"] as? Map<String, Any>
        roomDetails?.let {
            val roomType = it["room_type"] as? String ?: "N/A"
            val price = it["price"] as? Double ?: 0.0
            val location = it["roomLocation"] as? String ?: "N/A"
            val description = it["description"] as? String ?: "N/A"
            val beds = it["num_beds"]?.toString() ?: "N/A"

            rootView.findViewById<TextView>(R.id.boardingPriceOwner).text = "Rs. ${price}0"
            rootView.findViewById<TextView>(R.id.adRoomTypeOwner).text = "$roomType Room"
            rootView.findViewById<TextView>(R.id.numberOfBedsOwner).text = "$beds"
            rootView.findViewById<TextView>(R.id.locationDetailsOwner).text = "Distance from the University : $location Km"
            rootView.findViewById<TextView>(R.id.displayDescriptionOwner).text = "$description"
        }

        // Extract and display facilities
        val facilities = adInfo["facilities"] as? List<String> ?: emptyList()
        val facilitiesText = if (facilities.isNotEmpty()) {
            facilities.joinToString("\n")
        } else {
            "No facilities listed"
        }
        rootView.findViewById<TextView>(R.id.displayFacilitiesOwner).text = facilitiesText

        // Extract and display owner details
        val ownerDetails = adInfo["ownerDetails"] as? Map<String, String>
        ownerDetails?.let {
            val ownerName = it["owner_name"] ?: "N/A"
            val ownerMobile = it["mobile"] ?: "N/A"
            val ownerEmail = it["email"] ?: "N/A"
            val ownerAddress = it["address"] ?: "N/A"

            rootView.findViewById<TextView>(R.id.ownerNameOwner).text = "Your Name           -    $ownerName"
            rootView.findViewById<TextView>(R.id.ownerAddressOwner).text = "Address                -    $ownerAddress"
            rootView.findViewById<TextView>(R.id.ownerPhoneOwner).text = "Mobile                   -    $ownerMobile"
            rootView.findViewById<TextView>(R.id.ownerEmailOwner).text = "Email                     -    $ownerEmail"
        }



        // this function can change the status of the owner add into PENDING and RESERVED. If PENDING then student can see the add and if RESERVED then student cannot see the add in the student end. which is available for owner to change their add status.
        rootView.findViewById<Button>(R.id.updatedOwner).setOnClickListener {
            val builder = AlertDialog.Builder(this.requireContext())
            builder.setTitle("Confirmation")
            builder.setMessage("How do you want to update the status?")

            builder.setPositiveButton("As reserved") { dialog, _ ->
                val db = databaseHelper.writableDatabase
                try {
                    val rawQuery = "UPDATE $TABLE_ROOM SET $COLUMN_ROOM_STATUS = 'RESERVED' WHERE $COLUMN_ROOM_ID = ?"
                    db.execSQL(rawQuery, arrayOf(roomId))
                    Toast.makeText(requireContext(), "Status updated to RESERVED", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("DatabaseUpdate", "Error updating room status", e)
                } finally {
                    db.close()
                }
                dialog.dismiss()
            }

            builder.setNegativeButton("As pending") { dialog, _ ->
                val db = databaseHelper.writableDatabase
                try {
                    val rawQuery = "UPDATE $TABLE_ROOM SET $COLUMN_ROOM_STATUS = 'PENDING' WHERE $COLUMN_ROOM_ID = ?"
                    db.execSQL(rawQuery, arrayOf(roomId))
                    Toast.makeText(requireContext(), "Status updated to PENDING", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("DatabaseUpdate", "Error updating room status", e)
                } finally {
                    db.close()
                }
                dialog.dismiss()
            }

            builder.setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        // Display room image
        val imageViewerOwner = rootView.findViewById<ImageView>(R.id.imageViewerOwner)
        loadImageFromInternalStorage(roomId, imageViewerOwner)

        return rootView
    }

    private fun loadImageFromInternalStorage(roomId: Int, imageView: ImageView) {
        try {
            val fileName = "room_$roomId.jpg"
            val file = File(context?.filesDir, fileName)

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

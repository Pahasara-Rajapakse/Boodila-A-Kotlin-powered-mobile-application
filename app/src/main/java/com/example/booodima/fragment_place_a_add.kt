package com.example.booodima

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booodima.My_Database.DatabaseHelper
import java.io.File
import java.io.FileOutputStream


class fragment_place_a_add : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var email: String
    private lateinit var userName: String

    private lateinit var ownerNameField: TextView
    private lateinit var addressField: TextView
    private lateinit var mobileField: TextView
    private lateinit var emailField: TextView
    private lateinit var descriptionField: EditText
    private lateinit var universityText: TextView
    private lateinit var confirmCheckBox: CheckBox
    private lateinit var etDistance: EditText
    private lateinit var addImageButton: Button
    private lateinit var imagesRecyclerView: RecyclerView
    private lateinit var numberOfBedsField: EditText
    private lateinit var sharedRoomButton: RadioButton
    private lateinit var singleRoomButton: RadioButton
    private lateinit var roomPriceField: EditText
    private lateinit var kitchenCheckBox: CheckBox
    private lateinit var studyCheckBox: CheckBox
    private  lateinit var btnReloadOwner: ImageButton

    private val selectedImages: MutableList<Bitmap> = mutableListOf()
    private val IMAGE_REQUEST_CODE = 100
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_place_a_add, container, false)

        ownerNameField = rootView.findViewById(R.id.etOwnerName)
        addressField = rootView.findViewById(R.id.etAddress)
        mobileField = rootView.findViewById(R.id.etMobile)
        emailField = rootView.findViewById(R.id.etEmail)
        descriptionField = rootView.findViewById(R.id.etDescription)
        universityText = rootView.findViewById(R.id.universityText)
        confirmCheckBox = rootView.findViewById(R.id.checkBoxConfirm)
        etDistance = rootView.findViewById(R.id.etDistance)
        numberOfBedsField = rootView.findViewById(R.id.etBeds)
        roomPriceField = rootView.findViewById(R.id.etPrice)
        addImageButton = rootView.findViewById(R.id.btnUploadImages)
        kitchenCheckBox = rootView.findViewById(R.id.checkBoxKitchen)
        studyCheckBox = rootView.findViewById(R.id.chechBoxStudy)
        sharedRoomButton = rootView.findViewById(R.id.rbShared)
        btnReloadOwner = rootView.findViewById(R.id.btnReloadOwner)
        singleRoomButton = rootView.findViewById(R.id.rbSingle)

        imagesRecyclerView = rootView.findViewById(R.id.imagesRecyclerView)
        imagesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        userName = arguments?.getString("userName") ?: "Default User"

        val btnSubmit: Button = rootView.findViewById(R.id.btnSubmit)
        val btnCancel: Button = rootView.findViewById(R.id.btnCancel)

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper(requireContext())

        // Load owner details
        email = userName
        loadOwnerDetails()

        // Handle Submit button click
        btnSubmit.setOnClickListener {
            if (validateFields()) {
                val roomType = if (sharedRoomButton.isChecked) "Shared" else "Single"

                val roomId = databaseHelper.insertRoomData(
                    emailField.text.toString(),
                    numberOfBedsField.text.toString().toInt(),
                    roomType,
                    roomPriceField.text.toString().toDouble(),
                    descriptionField.text.toString(),
                    etDistance.text.toString(),
                    "PENDING"
                )

                if (roomId > 0) {
                    // Save Facilities
                    val facilities = mutableListOf<String>()
                    if (kitchenCheckBox.isChecked) facilities.add("Kitchen")
                    if (studyCheckBox.isChecked) facilities.add("Study Room")
                    databaseHelper.insertRoomFacilities(roomId.toInt(), email, facilities)

                    // Save Images in the database.
//                    for (image in selectedImages) {
//                        databaseHelper.insertAdImage(roomId.toInt(), image)
//                    }

                    selectedImages.forEach { image ->
                        saveImageToInternalStorage(image, roomId.toInt())
                    }

                    Toast.makeText(context, "Room and images added successfully", Toast.LENGTH_SHORT).show()
                    resetFields()
                } else {
                    Toast.makeText(context, "Failed to add room", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Handle Cancel button click
        btnCancel.setOnClickListener {
            val builder = AlertDialog.Builder(this.requireContext())
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure you want to cancel?")

            builder.setPositiveButton("Yes") { dialog, _ ->
                resetFields()
                dialog.dismiss()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        btnReloadOwner.setOnClickListener{
            loadOwnerDetails()
        }

        // Handle Add Image button click
        addImageButton.setOnClickListener {
            val builder = AlertDialog.Builder(this.requireContext())
            builder.setTitle("Confirmation")
            builder.setMessage("How do you want to add images?")

            builder.setPositiveButton("Open Camera") { dialog, _ ->
                openCamera()
                dialog.dismiss()
            }

            builder.setNegativeButton("Open Device Storage") { dialog, _ ->
                openImagePicker()
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        return rootView
    }

    private fun loadOwnerDetails() {
        val ownerDetails = databaseHelper.getOwnerDetails(email)
        if (ownerDetails != null) {

            ownerNameField.text = (ownerDetails[DatabaseHelper.COLUMN_OWNER_NAME])
            addressField.text = (ownerDetails[DatabaseHelper.COLUMN_ADDRESS])
            mobileField.text = (ownerDetails[DatabaseHelper.COLUMN_MOBILE])
            emailField.text = (ownerDetails[DatabaseHelper.COLUMN_EMAIL])
            universityText.text = (ownerDetails[DatabaseHelper.COLUMN_UNIVERSITY_NAME])

            Toast.makeText(context, "Owner details updated", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(context, "Owner details not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateFields(): Boolean {
        return when {
            TextUtils.isEmpty(numberOfBedsField.text) -> {
                numberOfBedsField.error = "Number of beds is required"
                false
            }TextUtils.isEmpty(roomPriceField.text) -> {
                roomPriceField.error = "Price is required"
                false
            }
            selectedImages.isEmpty() -> {
                Toast.makeText(context, "Please add at least one image", Toast.LENGTH_SHORT).show()
                false
            }
            selectedImages.size > 5 -> {
                Toast.makeText(context, "You can only select up to 5 images", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etDistance.text) -> {
                etDistance.error = "Distance is required"
                false
            }
            !confirmCheckBox.isChecked -> {
                Toast.makeText(context, "Please confirm before submitting", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun resetFields() {
        selectedImages.clear()
        kitchenCheckBox.isChecked = false
        studyCheckBox.isChecked = false
        sharedRoomButton.isChecked = false
        singleRoomButton.isChecked = false
        numberOfBedsField.text.clear()
        roomPriceField.text.clear()
        etDistance.text.clear()
        descriptionField.text.clear()
        confirmCheckBox.isChecked = false
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    if (data?.clipData != null) {
                        val count = data.clipData?.itemCount ?: 0
                        for (i in 0 until count) {
                            val imageUri = data.clipData?.getItemAt(i)?.uri
                            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
                            selectedImages.add(bitmap)
                        }
                    } else if (data?.data != null) {
                        val imageUri = data.data
                        val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
                        selectedImages.add(bitmap)
                    }
                    updateImagesDisplay()
                }

                IMAGE_REQUEST_CODE -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    if (bitmap != null) {
                        selectedImages.add(bitmap)
                    }
                    updateImagesDisplay()
                }
            }
        }
    }

    // upload images into the recycler view.
    private fun updateImagesDisplay() {
        val adapter = ImageRecyclerAdapter(selectedImages) { position ->
            deleteImage(position)
        }
        imagesRecyclerView.adapter = adapter
    }

    // use to delete images.
    private fun deleteImage(position: Int) {
        selectedImages.removeAt(position)
        updateImagesDisplay()
        Toast.makeText(requireContext(), "Image deleted", Toast.LENGTH_SHORT).show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap, roomId: Int) {
        try {
            val fileName = "room_$roomId.jpg"
            val file = File(context?.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Log.d("ImageStorage", "Image saved successfully: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("ImageStorage", "Error saving image: ${e.message}", e)
        }
    }

}


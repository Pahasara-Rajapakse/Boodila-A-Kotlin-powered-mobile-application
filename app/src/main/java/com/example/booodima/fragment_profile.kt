package com.example.booodima

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.booodima.My_Database.DatabaseHelper

class fragment_profile : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var email: String
    private lateinit var userName: String
    private lateinit var ownerNameField: EditText
    private lateinit var addressField: EditText
    private lateinit var mobileField: EditText
    private lateinit var emailField: EditText
    private lateinit var noteField: EditText
    private lateinit var universitySpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        databaseHelper = DatabaseHelper(requireContext())

        ownerNameField = rootView.findViewById(R.id.ownerName)
        addressField = rootView.findViewById(R.id.Address)
        mobileField = rootView.findViewById(R.id.mobileNumber)
        emailField = rootView.findViewById(R.id.emailAddress)
        noteField = rootView.findViewById(R.id.ownerAbout)
        universitySpinner = rootView.findViewById(R.id.universitySpinner)
        val updateButton = rootView.findViewById<Button>(R.id.buttonUpdate)
        val saveButton = rootView.findViewById<Button>(R.id.buttonSave)

        userName = arguments?.getString("userName") ?: "Default User"

        // Load initial owner details
        email = userName
        loadOwnerDetails()

        saveButton.setOnClickListener {
            if (validateFields()) {
                val updatedName = ownerNameField.text.toString().trim()
                val updatedAddress = addressField.text.toString().trim()
                val updatedMobile = mobileField.text.toString().trim()
                val updatedEmail = emailField.text.toString().trim()
                val updatedUniversity = universitySpinner.selectedItem.toString().trim()
                val updatedNote = noteField.text.toString().trim()

                val success = databaseHelper.insertOwnerData(updatedEmail, updatedName, updatedAddress, updatedMobile, updatedUniversity, updatedNote, email)
                if (success) {
                    Toast.makeText(context, "Details saved successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to save details", Toast.LENGTH_SHORT).show()
                }
            }
        }

        updateButton.setOnClickListener {
            //loadOwnerDetails()
            showLogoutDialog()
        }

        return rootView
    }

    private fun loadOwnerDetails() {
        val ownerDetails = databaseHelper.getOwnerDetails(email)
        if (ownerDetails != null) {
            ownerNameField.setText(ownerDetails[DatabaseHelper.COLUMN_OWNER_NAME])
            addressField.setText(ownerDetails[DatabaseHelper.COLUMN_ADDRESS])
            mobileField.setText(ownerDetails[DatabaseHelper.COLUMN_MOBILE])
            emailField.setText(ownerDetails[DatabaseHelper.COLUMN_EMAIL])
            noteField.setText(ownerDetails[DatabaseHelper.COLUMN_ANY_NOTE])

            val universityAdapter = universitySpinner.adapter as? ArrayAdapter<String>
            val universityName = ownerDetails[DatabaseHelper.COLUMN_UNIVERSITY_NAME]

            if (!universityName.isNullOrEmpty() && universityAdapter != null) {
                val position = universityAdapter.getPosition(universityName)
                if (position >= 0) {
                    universitySpinner.setSelection(position)
                }
            }
        } else {
            Toast.makeText(context, "Owner details not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateFields(): Boolean {
        return when {
            TextUtils.isEmpty(ownerNameField.text) -> {
                ownerNameField.error = "Owner name is required"
                false
            }
            TextUtils.isEmpty(addressField.text) -> {
                addressField.error = "Address is required"
                false
            }
            TextUtils.isEmpty(mobileField.text) || mobileField.text.length != 10 -> {
                mobileField.error = "Valid 10-digit mobile number is required"
                false
            }
            TextUtils.isEmpty(emailField.text) || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailField.text).matches() -> {
                emailField.error = "Valid email address is required"
                false
            }
            universitySpinner.selectedItem == null -> {
                Toast.makeText(context, "Please select a university", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle("Confirmation")
        builder.setMessage("Are you sure you want to update your details?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            if (validateFields()) {
                val updatedName = ownerNameField.text.toString().trim()
                val updatedAddress = addressField.text.toString().trim()
                val updatedMobile = mobileField.text.toString().trim()
                val updatedEmail = emailField.text.toString().trim()
                val updatedUniversity = universitySpinner.selectedItem.toString().trim()
                val updatedNote = noteField.text.toString().trim()

                val success = databaseHelper.updateOwnerDetails(updatedEmail, updatedName, updatedAddress, updatedMobile, updatedUniversity, updatedNote)
                if(success){
                    Toast.makeText(context, "Your details updated successfully.", Toast.LENGTH_SHORT).show()
                }else{

                }
            }

            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            loadOwnerDetails()
            dialog.dismiss()
            Toast.makeText(context, "Previous details updated successfully.", Toast.LENGTH_SHORT).show()
        }

        val dialog = builder.create()
        dialog.show()
    }
}


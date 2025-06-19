package com.example.booodima

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.booodima.My_Database.DatabaseHelper

class SignUp : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var editUsername: EditText
    private lateinit var editPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var errorMessage2: TextView
    private lateinit var tipButton: ImageButton
    private lateinit var passwordTipDialog: Dialog
    private lateinit var togglePasswordButton1: ImageButton
    private lateinit var toggleConfirmPasswordButton2: ImageButton
    private var isPasswordVisible = false

    private val TAG = "SignUpActivity" // For logging lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interface_signup)

        Log.d(TAG, "onCreate called")

        databaseHelper = DatabaseHelper(this)

        val signInButton = findViewById<Button>(R.id.signInButtonSignUp)
        val signUpButton = findViewById<Button>(R.id.signUpButtonSignUp)
        editUsername = findViewById(R.id.editUsernameSignUp)
        editPassword = findViewById(R.id.editPasswordSignUp)
        confirmPassword = findViewById(R.id.confirmPassword)
        errorMessage2 = findViewById(R.id.errorMessage2)
        tipButton = findViewById(R.id.tip)
        togglePasswordButton1 = findViewById(R.id.btnTogglePassword1)
        toggleConfirmPasswordButton2 = findViewById(R.id.btnTogglePassword2)

        // Handle Sign-In button click
        signInButton.setOnClickListener {
            try {
                val gotoNextPage = Intent(applicationContext, SignIn::class.java)
                startActivity(gotoNextPage)
                clearFields()
            } catch (e: Exception) {
                Log.e(TAG, "Error navigating to SignIn activity: ${e.message}")
                Toast.makeText(this, "An error occurred while navigating.", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle Sign-Up button click
        signUpButton.setOnClickListener {
            try {
                val username = editUsername.text.toString().trim()
                val password = editPassword.text.toString().trim()
                val confirmPass = confirmPassword.text.toString().trim()

                // Validation checks
                when {
                    username.isEmpty() -> errorMessage2.text = "Username cannot be empty."
                    !isValidEmail(username) -> errorMessage2.text = "Invalid email address."
                    password.isEmpty() -> errorMessage2.text = "Password cannot be empty."
                    !createPassword(password) -> errorMessage2.text = "Password not valid."
                    confirmPass.isEmpty() -> {
                        errorMessage2.text = "Please confirm your password."
                        editPassword.text = null
                        confirmPassword.text = null
                        errorMessage2.text = null
                    }
                    password != confirmPass -> errorMessage2.text = "Passwords do not match."
                    databaseHelper.isUsernameExists(username) -> errorMessage2.text = "Username already exists. Choose another."
                    else -> {
                        val result = databaseHelper.insertUserData(username, password)
                        if (result != -1L) {
                            Toast.makeText(this, "Sign-up successful!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, SignIn::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            errorMessage2.text = "Sign-up failed. Please try again."
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during sign-up process: ${e.message}")
                errorMessage2.text = "An unexpected error occurred. Please try again."
            }
        }

        togglePasswordButton1.setOnClickListener {
            if (isPasswordVisible) {
                editPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordButton1.setImageResource(R.drawable.eye_close)
            } else {
                editPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordButton1.setImageResource(R.drawable.eye)
            }
            editPassword.setSelection(editPassword.text.length)
            isPasswordVisible = !isPasswordVisible
        }

        toggleConfirmPasswordButton2.setOnClickListener {
            if (isPasswordVisible) {
                confirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                toggleConfirmPasswordButton2.setImageResource(R.drawable.eye_close)
            } else {
                confirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                toggleConfirmPasswordButton2.setImageResource(R.drawable.eye)
            }
            confirmPassword.setSelection(confirmPassword.text.length)
            isPasswordVisible = !isPasswordVisible
        }


        passwordTipDialog = Dialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_password_tip, null)
        passwordTipDialog.setContentView(dialogView)

        passwordTipDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        passwordTipDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val buttonUnderstood = dialogView.findViewById<Button>(R.id.buttonUnderstood)

        tipButton.setOnClickListener {
            passwordTipDialog.show()
        }

        // Close the dialog when the "Close" button is clicked
        buttonUnderstood.setOnClickListener {
            passwordTipDialog.dismiss()
        }

    }

    override fun onStop() {
        super.onStop()
        try {
            clearFields()
        } catch (e: Exception) {
            Log.e(TAG, "Error resetting UI elements: ${e.message}")
        }
        Log.e(TAG, "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            clearFields()
        } catch (e: Exception) {
            Log.e(TAG, "Error during onDestroy: ${e.message}")
        }
        Log.e(TAG, "onDestroy called")
    }

    private fun clearFields() {
        editUsername.text = null
        editPassword.text = null
        confirmPassword.text = null
        errorMessage2.text = null
    }

    private fun createPassword(password: String): Boolean {
        if (password.length < 8) return false

        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialSymbol = password.any { !it.isLetterOrDigit() }

        return hasLetter && hasDigit && hasSpecialSymbol
        //Password must be at least 8 characters long, and include at least one letter, one digit, and one special character.
    }

    // Function to validate email format
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return email.matches(emailPattern.toRegex())
    }
}

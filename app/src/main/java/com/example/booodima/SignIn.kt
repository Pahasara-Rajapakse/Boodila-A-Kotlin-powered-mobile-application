package com.example.booodima

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.booodima.My_Database.DatabaseHelper
import com.example.booodima.My_Database.DatabaseHelper.Companion.COLUMN_USER_NAME
import com.example.booodima.My_Database.DatabaseHelper.Companion.COLUMN_USER_STATUS
import com.example.booodima.My_Database.DatabaseHelper.Companion.TABLE_USER

class SignIn : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var editUsername: EditText
    private lateinit var editPassword: EditText
    private lateinit var errorMessage1: TextView
    private lateinit var togglePasswordButton: ImageButton
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interface_signin)

        try {
            // Initialize DatabaseHelper
            databaseHelper = DatabaseHelper(this)

            val signInButton = findViewById<Button>(R.id.signInButton)
            val registerButton = findViewById<Button>(R.id.getRegisterButton)

            editUsername = findViewById(R.id.editUsernameSignIn)
            editPassword = findViewById(R.id.editPasswordSignIn)
            errorMessage1 = findViewById(R.id.errorMessage1)

            // Sign In Button Click Listener
            signInButton.setOnClickListener {
                handleSignIn()
            }

            // Register Button Click Listener
            registerButton.setOnClickListener {
                navigateToSignUp()
            }
        } catch (e: Exception) {
            Log.e("SignInActivity", "Error initializing the activity: ${e.message}")
            Toast.makeText(this, "An unexpected error occurred.", Toast.LENGTH_SHORT).show()
        }

        togglePasswordButton = findViewById(R.id.btnTogglePassword)
        togglePasswordButton.setOnClickListener {
            if (isPasswordVisible) {
                editPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordButton.setImageResource(R.drawable.eye_close)
            } else {
                editPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordButton.setImageResource(R.drawable.eye)
            }
            editPassword.setSelection(editPassword.text.length)
            isPasswordVisible = !isPasswordVisible
        }
    }

    private fun handleSignIn() {
        try {
            val username = editUsername.text.toString().trim()
            val password = editPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                errorMessage1.text = "Username and password cannot be empty."
                return
            }

            val isValid = databaseHelper.checkUserCredentials(username, password)
            if (isValid) {
                updateLoginStatus(username)

                val bundle = Bundle()
                bundle.putString("userName", username)

                val gotoNextPage = Intent(applicationContext, OwnerMainActivity::class.java)
                gotoNextPage.putExtras(bundle)

                Log.d("SignInActivity", "Sign In successful for user: $username")
                startActivity(gotoNextPage)
            } else {
                errorMessage1.text = "Incorrect username or password."
            }
        } catch (e: Exception) {
            Log.e("SignInActivity", "Error during Sign In process: ${e.message}")
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateLoginStatus(username: String) {
        var db = databaseHelper.writableDatabase
        try {
            val query = "UPDATE $TABLE_USER SET $COLUMN_USER_STATUS = 'LOGGED' WHERE $COLUMN_USER_NAME = ?"
            db.execSQL(query, arrayOf(username))
            Log.d("SignInActivity", "User status updated to LOGED for user: $username")
        } catch (e: Exception) {
            Log.e("SignInActivity", "Error updating user status: ${e.message}")
            Toast.makeText(this, "Failed to update login status.", Toast.LENGTH_SHORT).show()
        } finally {
            db.close()
        }
    }

    private fun navigateToSignUp() {
        try {
            val gotoNextPage = Intent(applicationContext, SignUp::class.java)
            startActivity(gotoNextPage)
            clearRecords()
        } catch (e: Exception) {
            Log.e("SignInActivity", "Error during navigation to SignUp: ${e.message}")
            Toast.makeText(this, "An error occurred while navigating.", Toast.LENGTH_SHORT).show()
            clearRecords()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            Log.d("SignInActivity", "Activity is resumed.")
        } catch (e: Exception) {
            Log.e("SignInActivity", "Error during onResume: ${e.message}")
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            Log.d("SignInActivity", "Activity is paused.")
        } catch (e: Exception) {
            Log.e("SignInActivity", "Error during onPause: ${e.message}")
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            clearRecords()
            Log.d("SignInActivity", "Activity is stopped.")
        } catch (e: Exception) {
            Log.e("SignInActivity", "Error during onStop: ${e.message}")
        }
    }

    override fun onRestart() {
        super.onRestart()
        try {
            Log.d("SignInActivity", "Activity is restarting.")
        } catch (e: Exception) {
            Log.e("SignInActivity", "Error during onRestart: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            clearRecords()
            Log.d("SignInActivity", "Activity is being destroyed.")
        } catch (e: Exception) {
            Log.e("SignInActivity", "Error during onDestroy: ${e.message}")
        }
    }

    private fun clearRecords() {
        editUsername.text = null
        editPassword.text = null
        errorMessage1.text = null
    }
}

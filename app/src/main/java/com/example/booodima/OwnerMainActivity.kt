package com.example.booodima

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.booodima.My_Database.DatabaseHelper
import com.example.booodima.My_Database.DatabaseHelper.Companion.COLUMN_USER_NAME
import com.example.booodima.My_Database.DatabaseHelper.Companion.COLUMN_USER_STATUS
import com.example.booodima.My_Database.DatabaseHelper.Companion.TABLE_USER
import com.google.android.material.navigation.NavigationView

class OwnerMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var userName: String
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.owner_main_interface)

        userName = intent.getStringExtra("userName") ?: "Owner"
        drawerLayout = findViewById(R.id.drawer_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        databaseHelper = DatabaseHelper(this)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.user_name)
        userNameTextView.text = userName

        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // If savedInstanceState is null, open the home fragment
        if (savedInstanceState == null) {
            openFragment(fragment_home())
            navigationView.setCheckedItem(R.id.nav_home)
        }


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> openFragment(fragment_home())
            R.id.nav_profile -> openFragment(fragment_profile())
            R.id.nav_place_add -> openFragment(fragment_place_a_add())
            R.id.nav_settings -> openFragment(fragment_settings())
            R.id.nav_about -> openFragment(fragment_about_us())
            R.id.nav_logout -> showLogoutDialog()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Helper function to open fragments and pass userName
    private fun openFragment(fragment: androidx.fragment.app.Fragment) {
        val bundle = Bundle()
        bundle.putString("userName", userName)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmation")
        builder.setMessage("Are you sure you want to logout?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()

//            val gotoNext = Intent(applicationContext, SignIn::class.java)
//            startActivity(gotoNext)
//            finish()

            var db = databaseHelper.writableDatabase
            try {
                val query = "UPDATE $TABLE_USER SET $COLUMN_USER_STATUS = 'LOGOUT' WHERE $COLUMN_USER_NAME = ?"
                db.execSQL(query, arrayOf(userName))
                Log.d("SignInActivity", "User status updated to LOGOUT for user: $userName")
            } catch (e: Exception) {
                Log.e("SignInActivity", "Error updating user status: ${e.message}")
                Toast.makeText(this, "Failed to update LOGOUT status.", Toast.LENGTH_SHORT).show()
            } finally {
                db.close()
            }

        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

}

package com.example.booodima

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class StudentMainInterface : AppCompatActivity(), fragment_filter.FilterCallback {

    private lateinit var universityName: String
    private lateinit var filterButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_interface_student_owner)

        universityName = intent.getStringExtra("universityName").orEmpty()
        filterButton = findViewById(R.id.filterButton)

        val title = findViewById<TextView>(R.id.titleUni)
        title.text = universityName

        // Pass universityName to the fragment
        val fragment = fragment_show_rooms_for_student_recycler().apply {
            arguments = Bundle().apply {
                putString("universityName", universityName)
            }
        }

        filterButton.setOnClickListener {
            val fragmentFilter = fragment_filter().apply {
                arguments = Bundle().apply {
                    putString("universityName", universityName)
                }
            }

            loadFragment(fragmentFilter, true)
        }

        loadFragment(fragment, false)
    }

    private fun loadFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_student, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    // Callback to apply filters
    override fun onFilterApplied(
        roomType: String,
        bedCount: String,
        minPrice: Double?,
        maxPrice: Double?,
        includeKitchen: Boolean,
        includeStudy: Boolean
    ) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_student)
        if (fragment is fragment_show_rooms_for_student_recycler) {
            fragment.onFilterApplied(roomType, bedCount, minPrice, maxPrice)
        }
    }
}

package com.example.booodima
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booodima.My_Adapters.UniversityAdapter

class ChooseUniversity : AppCompatActivity() {
    private val TAG = "ChooseUniActivity"

    private lateinit var recyclerView: RecyclerView
    private lateinit var universityAdapter: UniversityAdapter
    private val universities = listOf(
        "University of Kelaniya",
        "University of Peradeniya",
        "University of Ruhuna",
        "University of Colombo",
        "University of Jaffna",
        "University of Moratuwa",
        "University of Sri Jayewardenepura",
        "Eastern University, Sri Lanka",
        "South Eastern University of Sri Lanka",
        "University of the Visual and Performing Arts",
        "Sabaragamuwa University of Sri Lanka",
        "Wayamba University of Sri Lanka",
        "Uva Wellassa University",
        "Rajarata University of Sri Lanka",
        "The Open University of Sri Lanka",
        "Gampaha Wickramarachchi University of Indigenous Medicine",
        "National Institute of Social Development"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interface_choose_university)

        recyclerView = findViewById(R.id.university_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create and set the adapter
        universityAdapter = UniversityAdapter(universities)
        recyclerView.adapter = universityAdapter
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.e(TAG, "onRestart called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy called")
    }
}
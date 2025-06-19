package com.example.booodima

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booodima.My_Adapters.RoomAdapter
import com.example.booodima.My_Database.DatabaseHelper

class fragment_show_rooms_for_student_recycler : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseHelper: DatabaseHelper
    private var universityName: String? = null
    private lateinit var adapter: RoomAdapter
    private lateinit var noResultsMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            universityName = it.getString("universityName")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_show_rooms_for_student_recycler, container, false)
        noResultsMessage = rootView.findViewById(R.id.noResultsMessage)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.resultsRecyclerView)
        databaseHelper = DatabaseHelper(requireContext())
        loadRecyclerView()
    }

    private fun loadRecyclerView() {
        val roomList = universityName?.let { databaseHelper.getSearchedAdds(it, "PENDING") } ?: emptyList()

        if (roomList.isEmpty()) {
            noResultsMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            noResultsMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter = RoomAdapter(requireContext(), parentFragmentManager, roomList)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter
        }
    }


    fun onFilterApplied(
        roomType: String,
        bedCount: String,
        minPrice: Double?,
        maxPrice: Double?
    ) {
        val allRooms = universityName?.let { databaseHelper.getSearchedAdds(it, "PENDING") } ?: emptyList()

        val filteredRooms = allRooms?.filter { room ->
            val matchesRoomType = roomType == "Any" || room.roomType == roomType
            val matchesBedCount = bedCount == "Any" || room.beds.toString() == bedCount
            val matchesPrice = (minPrice == null || room.price.toDouble() >= minPrice) &&
                    (maxPrice == null || room.price.toDouble() <= maxPrice)

            matchesRoomType && matchesBedCount && matchesPrice
        }

        if (filteredRooms != null) {
            adapter.updateData(filteredRooms)
        }
    }
}

// display room ads in student end.

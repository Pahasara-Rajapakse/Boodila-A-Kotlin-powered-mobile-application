package com.example.booodima

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class fragment_filter : Fragment() {

    private var filterCallback: FilterCallback? = null
    private var universityName: String? = null

    interface FilterCallback {
        fun onFilterApplied(
            roomType: String,
            bedCount: String,
            minPrice: Double?,
            maxPrice: Double?,
            includeKitchen: Boolean,
            includeStudy: Boolean
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            universityName = it.getString("universityName")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            filterCallback = context as FilterCallback
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement FilterCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        filterCallback = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_filter, container, false)

        val roomTypeSpinner = rootView.findViewById<Spinner>(R.id.roomTypeSpinner)
        val bedCountSpinner = rootView.findViewById<Spinner>(R.id.bedCountSpinner)
        val minPriceEditText = rootView.findViewById<EditText>(R.id.minPrice)
        val maxPriceEditText = rootView.findViewById<EditText>(R.id.maxPrice)
        val facilitiesKitchen = rootView.findViewById<CheckBox>(R.id.facilitiesKitchen)
        val facilitiesStudy = rootView.findViewById<CheckBox>(R.id.facilitiesStudy)
        val applyFilterButton = rootView.findViewById<Button>(R.id.applyFilterButton)
        val cancelFiltersButton = rootView.findViewById<Button>(R.id.cancelFiltersButton)

        // filter button function.
        applyFilterButton.setOnClickListener {
            val selectedRoomType = roomTypeSpinner.selectedItem.toString()
            val selectedBedCount = bedCountSpinner.selectedItem.toString()
            val minPrice = minPriceEditText.text.toString().toDoubleOrNull()
            val maxPrice = maxPriceEditText.text.toString().toDoubleOrNull()
            val includeKitchen = facilitiesKitchen.isChecked
            val includeStudy = facilitiesStudy.isChecked

            filterCallback?.onFilterApplied(
                selectedRoomType,
                selectedBedCount,
                minPrice,
                maxPrice,
                includeKitchen,
                includeStudy
            )

            parentFragmentManager.popBackStack()
        }

        cancelFiltersButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return rootView
    }
}

package com.example.booodima

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booodima.My_Adapters.OwnerAdAdapter
import com.example.booodima.My_Database.DatabaseHelper

class fragment_home : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var userName: String
    private lateinit var adapter: OwnerAdAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var titleUser: TextView
    private lateinit var profileTipDialog: Dialog
    private lateinit var addText: TextView
    private lateinit var titleAds: TextView

    companion object {
        private const val TAG = "FragmentHome"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        userName = arguments?.getString("userName") ?: "Default User"
        Log.d(TAG, "Username: $userName")

        databaseHelper = DatabaseHelper(requireContext())
        Log.d(TAG, "DatabaseHelper initialized successfully.")

        titleUser = rootView.findViewById(R.id.titleUser)
        addText = rootView.findViewById(R.id.addText)
        titleAds = rootView.findViewById(R.id.titleAds)

        titleUser.text = userName

        setupRecyclerView(rootView)
        refreshUI(rootView)
        checkAds(rootView)

        return rootView
    }

    private fun setupRecyclerView(rootView: View) {
        recyclerView = rootView.findViewById(R.id.MyAddList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        refreshAds()
    }

    private fun checkAds(rootView: View){
        recyclerView = rootView.findViewById(R.id.MyAddList)
        val result = databaseHelper.isOwnerPostAd(userName)
        if(result){
            addText.visibility = View.GONE
        }else{
            addText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            titleAds.visibility = View.GONE
        }

    }

    private fun refreshAds() {
        val updatedAddList = databaseHelper.getOwnerRooms(userName)
        Log.d(TAG, "Refreshed ads list size: ${updatedAddList.size}")
        adapter = OwnerAdAdapter(requireContext(), updatedAddList)
        recyclerView.adapter = adapter
        adapter.updateAddList(updatedAddList)
    }

    private fun refreshUI(rootView: View) {
        showProfileSetupDialog()
    }

    // this used to popup the dialog in the owner end to guide them to setup their profile. if the user already setup their profile, this will not popup.
    private fun showProfileSetupDialog() {
        if (!databaseHelper.isUserExists(userName)) {
            profileTipDialog = Dialog(requireContext())
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_display_setup_profile_message, null)
            profileTipDialog.setContentView(dialogView)

            profileTipDialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            profileTipDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val buttonUnderstood = dialogView.findViewById<Button>(R.id.buttonUnderstood)
            buttonUnderstood.setOnClickListener {
                profileTipDialog.dismiss()
            }
            profileTipDialog.show()
        }
    }


    override fun onResume() {
        super.onResume()
        refreshAds()
        view?.let { checkAds(rootView = it) }
    }
}

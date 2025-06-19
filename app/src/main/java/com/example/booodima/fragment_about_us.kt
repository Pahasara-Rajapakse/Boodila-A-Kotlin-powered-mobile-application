package com.example.booodima

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class fragment_about_us : Fragment() {

    private lateinit var userName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_about_us, container, false)

        val contactUsButton: Button = rootView.findViewById(R.id.contact_us_button)
        userName = arguments?.getString("userName") ?: "Default User"

        contactUsButton.setOnClickListener {
            val email = "pasindupahasara1@gmail.com"
            val subject = "Inquiry about the app"
            val body = "Hello, I would like to inquire about..."

            val uriText = "mailto:$email?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}"
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse(uriText))

            try {
                startActivity(emailIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        return rootView
    }
}

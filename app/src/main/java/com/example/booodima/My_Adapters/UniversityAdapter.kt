package com.example.booodima.My_Adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.booodima.StudentMainInterface
import com.example.booodima.R

class UniversityAdapter(private val universities: List<String>) : RecyclerView.Adapter<UniversityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.university_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(universities[position])
    }

    override fun getItemCount(): Int {
        return universities.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val universityName: TextView = itemView.findViewById(R.id.university_name)

        fun bind(university: String) {
            universityName.text = university

            itemView.setOnClickListener {
                Log.e("UniversityAdapter", "Navigating to StudentMainInterface with university: $university")

                val gotoNextPage = Intent(itemView.context, StudentMainInterface::class.java).apply {
                    putExtra("universityName", university)
                }

                itemView.context.startActivity(gotoNextPage)
                Toast.makeText(itemView.context, "Clicked: $university", Toast.LENGTH_SHORT).show()
            }


        }
    }

}

// this adapter helps to show (loard) the universities when the use go through as a student.
package com.example.date_me.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.date_me.databinding.FragmentDatingBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DatingFragment : Fragment() {
    private lateinit var binding : FragmentDatingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDatingBinding.inflate(layoutInflater)
        getData()
        return binding.root
    }
    private fun getData() {
        FirebaseDatabase.getInstance().getReference("users")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("Marcel", "onDataChange: ${snapshot.toString()}")
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message,Toast.LENGTH_SHORT).show()
                }
            })
    }
}
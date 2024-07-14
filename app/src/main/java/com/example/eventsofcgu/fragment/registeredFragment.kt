package com.example.eventsofcgu.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.example.eventsofcgu.EventRecylerclass
import com.example.eventsofcgu.R
import com.example.eventsofcgu.eventAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class registeredFragment : Fragment() {

    private lateinit var dbrefreg: DatabaseReference
    private lateinit var dbrefregurl: DatabaseReference
    private val eventsregistered = mutableListOf<EventRecylerclass>()
    private lateinit var recycler1: RecyclerView
    private lateinit var adapter: eventAdapter
    private lateinit var userName: String

    var url1: String? = null
    var url2: String? = null
    var url3: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val imageList = ArrayList<SlideModel>() // Create image list

        dbrefregurl = FirebaseDatabase.getInstance().getReference("urls")
        dbrefregurl.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if dataSnapshot exists
                if (dataSnapshot.exists()) {
                    // Assign URLs to variables
                    url1 = dataSnapshot.child("url1").getValue(String::class.java)
                    url2 = dataSnapshot.child("url2").getValue(String::class.java)
                    url3 = dataSnapshot.child("url3").getValue(String::class.java)

                    // Print the URLs or use them as needed
                    Log.d("FirebaseURL", "URL 1: $url1")
                    Log.d("FirebaseURL", "URL 2: $url2")
                    Log.d("FirebaseURL", "URL 3: $url3")

                    // Update imageList with the URLs
                    imageList.clear()
                    imageList.add(SlideModel(url1))
                    imageList.add(SlideModel(url2))
                    imageList.add(SlideModel(url3))

                    // Set image list to the slider
                    val imageSlider = view?.findViewById<ImageSlider>(R.id.image_slider)
                    imageSlider?.setImageList(imageList)
                } else {
                    Log.d("FirebaseURL", "No URLs found in the 'urls' node.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseURL", "Failed to read URLs", databaseError.toException())
            }
        })

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_registered, container, false)

//        val fetchedname = view.findViewById<TextView>(R.id.fetchedname)

        // Initialize RecyclerView and its layout manager
        recycler1 = view.findViewById(R.id.Recyler)
        recycler1.setHasFixedSize(true)
        recycler1.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        // Initialize adapter and set it to RecyclerView
        adapter = eventAdapter(requireContext(), eventsregistered)
        recycler1.adapter = adapter

        // Fetch ongoing and upcoming events based on user registration
        val sharedPreferences =
            requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userName = sharedPreferences.getString("username", "") ?: ""
        //fetchedname.text = userName
        fetchRegisteredEvents()

        return view
    }

    private fun fetchRegisteredEvents() {
        // Clear previous data
        eventsregistered.clear()

        // Fetch ongoing events
        dbrefreg = FirebaseDatabase.getInstance().getReference("ongoingevents")
        dbrefreg.addListenerForSingleValueEvent(eventListener)

        // Fetch upcoming events
        dbrefreg = FirebaseDatabase.getInstance().getReference("upcomingevents")
        dbrefreg.addListenerForSingleValueEvent(eventListener)
    }

    private val eventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (eventSnapshot in snapshot.children) {
                val event = eventSnapshot.getValue(EventRecylerclass::class.java)
                event?.let {
                    val registeredUsers = event.registeredusers ?: emptyList()

                    // Add event to list if userName is in registeredUsers
                    if (registeredUsers.contains(userName)) {
                        eventsregistered.add(event)
                    }
                }
            }

            // Update UI
            adapter.notifyDataSetChanged()
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseError", "Error fetching events: ${error.message}")
        }
    }
}

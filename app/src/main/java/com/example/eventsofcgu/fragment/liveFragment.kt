package com.example.eventsofcgu.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

class liveFragment : Fragment() {

    private lateinit var dbrefong: DatabaseReference
    private lateinit var dbrefongurl: DatabaseReference
    private val eventsongoing = mutableListOf<EventRecylerclass>()
    private lateinit var recycler2: RecyclerView
    private lateinit var adaptertwo: eventAdapter

    var url1: String? = null
    var url2: String? = null
    var url3: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_live, container, false)

        val imageSlider = view.findViewById<ImageSlider>(R.id.image_slider)
        val imageList = ArrayList<SlideModel>() // Create image list

        // Inflate the layout for this fragment
        recycler2 = view.findViewById(R.id.Recyler2)
        recycler2.setHasFixedSize(true)
        recycler2.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        adaptertwo = eventAdapter(requireContext(), eventsongoing)
        recycler2.adapter = adaptertwo

        dbrefongurl = FirebaseDatabase.getInstance().getReference("urls")
        dbrefongurl.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assign URLs to variables
                    url1 = dataSnapshot.child("url1").getValue(String::class.java)
                    url2 = dataSnapshot.child("url2").getValue(String::class.java)
                    url3 = dataSnapshot.child("url3").getValue(String::class.java)

                    // Print the URLs or use them as needed
                    Log.d("FirebaseURL", "URL 1: $url1")
                    Log.d("FirebaseURL", "URL 2: $url2")
                    Log.d("FirebaseURL", "URL 3: $url3")

                    // Clear the image list and add the new URLs
                    imageList.clear()
                    url1?.let { imageList.add(SlideModel(it)) }
                    url2?.let { imageList.add(SlideModel(it)) }
                    url3?.let { imageList.add(SlideModel(it)) }

                    // Set the image list to the slider
                    imageSlider.setImageList(imageList)
                } else {
                    Log.d("FirebaseURL", "No URLs found in the 'urls' node.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseURL", "Failed to read URLs", databaseError.toException())
            }
        })

        getuserdataong()
        return view
    }

    private fun getuserdataong() {
        dbrefong = FirebaseDatabase.getInstance().getReference("ongoingevents")
        dbrefong.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventsongoing.clear()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val event = userSnapshot.getValue(EventRecylerclass::class.java)
                        event?.let { eventsongoing.add(it) }
                    }
                    Log.d("HomeFragment", "Ongoing events: $eventsongoing")
                    adaptertwo.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }
}

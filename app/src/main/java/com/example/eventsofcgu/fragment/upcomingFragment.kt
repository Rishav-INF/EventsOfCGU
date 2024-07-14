package com.example.eventsofcgu.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class upcomingFragment : Fragment() {

    private lateinit var dbrefupc: DatabaseReference
    private lateinit var dbrefupcurl: DatabaseReference
    private val eventsupcoming = mutableListOf<EventRecylerclass>()
    private lateinit var recycler3: RecyclerView
    private lateinit var adapterthree: eventAdapter

    var url1: String? = null
    var url2: String? = null
    var url3: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val imageList = ArrayList<SlideModel>() // Create image list

        dbrefupcurl = FirebaseDatabase.getInstance().getReference("urls")
        dbrefupcurl.addValueEventListener(object : ValueEventListener {
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
        val view = inflater.inflate(R.layout.fragment_upcoming, container, false)

        recycler3 = view.findViewById(R.id.Recyler3)
        recycler3.setHasFixedSize(true)
        recycler3.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        adapterthree = eventAdapter(requireContext(), eventsupcoming)
        recycler3.adapter = adapterthree
        getuserdataupc()

        return view
    }

    private fun getuserdataupc() {
        dbrefupc = FirebaseDatabase.getInstance().getReference("upcomingevents")
        dbrefupc.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventsupcoming.clear()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val event = userSnapshot.getValue(EventRecylerclass::class.java)
                        event?.let { eventsupcoming.add(it) }
                    }
                    Log.d("HomeFragment", "Upcoming events: $eventsupcoming")
                    adapterthree.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }

}

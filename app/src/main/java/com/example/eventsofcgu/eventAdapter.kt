package com.example.eventsofcgu

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.eventsofcgu.eventAdapter.*
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue


class eventAdapter(private val context: Context, val eventdata : List<EventRecylerclass>) : RecyclerView.Adapter<eventViewHolder>() {
    class eventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val EventName=itemView.findViewById<TextView>(R.id.eventname)
        val ClubName=itemView.findViewById<TextView>(R.id.clubname)
        val RegLink=itemView.findViewById<TextView>(R.id.reglink)
        val eventImageView: ImageView = itemView.findViewById(R.id.Image)
        val moreinfo: ImageView = itemView.findViewById(R.id.moreinfo)
        val shareinfo: ImageView = itemView.findViewById(R.id.share)
        val Register: ImageView = itemView.findViewById(R.id.register)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): eventViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.event_card,parent,false)
    return eventViewHolder(view)
    }

    override fun getItemCount(): Int {
        return eventdata.size
    }

    override fun onBindViewHolder(holder: eventViewHolder, position: Int) {
    val event = eventdata[position]
    holder.EventName.text=eventdata[position].event
    holder.ClubName.text=eventdata[position].club
    holder.RegLink.text=eventdata[position].reglink
    holder.shareinfo.setOnClickListener{
       shareEventInfo(event)
    }
    Glide.with(holder.itemView.context)
            .load(eventdata[position].image)
            .into(holder.eventImageView)
    holder.moreinfo.setOnClickListener {
        showEventInfoDialog(event)
    }
    holder.Register.setOnClickListener {
        var evname : String = holder.EventName.text.toString()
        registrationDialog(event,evname)
    }
    }

    private fun shareEventInfo(event: EventRecylerclass) {
        val intent=Intent(Intent.ACTION_SEND)
        intent.type="text/plain"
        val shareText = event.eventdesc
        intent.putExtra(Intent.EXTRA_TEXT, shareText)
        val chooser=Intent.createChooser(intent,"Share using")
        context.startActivity(chooser)
    }

    private fun showEventInfoDialog(event: EventRecylerclass) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.eventinfo, null)
        val descriptionTextView: TextView = dialogView.findViewById(R.id.eventdesc)
        val desctitle:TextView =dialogView.findViewById(R.id.eventTitle)
        desctitle.text=event.event
        descriptionTextView.text = event.eventdesc
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
           //.setTitle("Event Details") // Customize the dialog title
         //   .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun registrationDialog(event: EventRecylerclass,EventName : String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.regcnfrm, null)
        val confirmPassword: TextInputEditText = dialogView.findViewById(R.id.pass)
        val eventTypeEditText: TextInputEditText = dialogView.findViewById(R.id.liveorupc)

        val text = "Hello, world!"
        val substring = "world"

        if (text.contains(substring)) {
            println("Substring found!")
        } else {
            println("Substring not found.")
        }

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Confirm Registration") // Customize the dialog title
            .setPositiveButton("Confirm") { dialog, which ->
                val enteredPassword = confirmPassword.text.toString()
                // Check if entered password matches user's password
                val sharedPreferences =
                    context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val storedPassword = sharedPreferences.getString("password", "")
                if (enteredPassword == storedPassword) {
                    val eventTypeText = eventTypeEditText.text.toString()
                    val eventType = eventTypeText.toIntOrNull()
                    if (eventType != null && ((eventType == 1 && EventName.contains("ongoing") )|| (eventType == 2)&& EventName.contains("upcoming"))) {
                        registerUserForEvent(eventTypeText,EventName)
                    } else {
                        Toast.makeText(context,"Invalid event type",Toast.LENGTH_SHORT).show()

                    }
                } else {
                    Toast.makeText(context, "Invalid Password", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun registerUserForEvent(eventTypeText: String, eventName: String) {
        val dbref: DatabaseReference
        var event: String
        if (eventTypeText == "1") {
            event = "ongoingevents"
        } else {
            event = "upcomingevents"
        }
        dbref = FirebaseDatabase.getInstance().getReference(event).child(eventName).child("registeredusers")

        val sharedpreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = sharedpreferences.getString("username", "")

        // Fetch the current list of registered users
        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUsers = snapshot.getValue<List<String>>() ?: emptyList()
                val updatedUsers = currentUsers.toMutableList()

                // Add the new user if not already in the list
                if (!updatedUsers.contains(username)) {
                    updatedUsers.add(username.toString())
                }

                // Update the registered users node with the updated list
                dbref.setValue(updatedUsers)
                    .addOnSuccessListener {
                        Toast.makeText(context,"Registered successfully",Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "User $username registered successfully for event $eventName")
                        // Handle success if needed
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to register user $username for event $eventName: ${e.message}")
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error fetching registered users for event $eventName: ${error.message}")
            }
        })
    }




}

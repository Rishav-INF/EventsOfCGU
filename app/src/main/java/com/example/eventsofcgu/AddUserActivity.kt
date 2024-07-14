package com.example.eventsofcgu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddUserActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Find views
        val submitbtn = findViewById<Button>(R.id.submit)
        val textname = findViewById<EditText>(R.id.name)
        val textpassword = findViewById<EditText>(R.id.password)
        val textsem = findViewById<EditText>(R.id.sem)

        submitbtn.setOnClickListener {
            val password = textpassword.text.toString().trim()
            val name = textname.text.toString().trim()
            val sem = textsem.text.toString().trim()  // Changed from Class to userClass

            if (name.isNotEmpty() && password.isNotEmpty() && sem.isNotEmpty()) {
                val userRef = database.child("users").child(name)
                val user = User(name, password, sem)

                userRef.setValue(user).addOnSuccessListener {
                    textname.text.clear()
                    textpassword.text.clear()
                    textsem.text.clear()
                    Toast.makeText(this, "User Added", Toast.LENGTH_SHORT).show()
                    val intent=Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                    Toast.makeText(this, "User Adding Failed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }

        }

    }

}


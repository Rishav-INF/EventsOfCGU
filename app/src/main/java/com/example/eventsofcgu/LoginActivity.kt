package com.example.eventsofcgu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val loginName = findViewById<EditText>(R.id.loginnametext)
        val loginPassword = findViewById<EditText>(R.id.loginpasswordtext)
        val loginBtn = findViewById<Button>(R.id.loginbtn)
        val signupBtn = findViewById<Button>(R.id.signupbtn)

        loginBtn.setOnClickListener {
            val name = loginName.text.toString().trim()
            val password = loginPassword.text.toString().trim()

            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            database = FirebaseDatabase.getInstance().getReference("users")
            Log.d("LoginActivity", "Checking user: $name")
            database.child(name).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val fetchedPassword = snapshot.child("password").getValue(String::class.java)
                    Log.d("LoginActivity", "Fetched password: $fetchedPassword")
                    if (fetchedPassword == password) {
                        Toast.makeText(this, "USER LOGIN SUCCESS", Toast.LENGTH_SHORT).show()
                        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("username", name)
                        editor.putString("password", password)
                        editor.apply()

                        val intent = Intent(this, EventNavigationActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Log.e("LoginActivity", "Failed to retrieve user data", it)
                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
            }
        }

        signupBtn.setOnClickListener {
            val intent = Intent(this, AddUserActivity::class.java)
            startActivity(intent)
        }
    }
}

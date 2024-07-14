package com.example.eventsofcgu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.eventsofcgu.fragment.registeredFragment
import com.example.eventsofcgu.fragment.upcomingFragment
import com.example.eventsofcgu.fragment.liveFragment
import com.example.eventsofcgu.fragment.noticeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class EventNavigationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigationxml)
        val nav_view=findViewById<BottomNavigationView>(R.id.bm)
        val registered=registeredFragment()
        val upcoming= upcomingFragment()
        val live=liveFragment()
        val profile= noticeFragment()
        makeCurrentFragment(registered)
//        val img=findViewById<ImageView>(R.id.imageView)
//        img.setOnClickListener {
//            finish()
//        }
        nav_view.setOnNavigationItemSelectedListener {
            when(it.itemId)
            {
                R.id.Registered->makeCurrentFragment(registered)
                R.id.Live->makeCurrentFragment(live)
                R.id.Upcoming->makeCurrentFragment(upcoming)
                R.id.notice->makeCurrentFragment(profile)
            }
            true
        }

    }
 private fun makeCurrentFragment(fragment: Fragment): FragmentTransaction =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment).commit()
        }

}
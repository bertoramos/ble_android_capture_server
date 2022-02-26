package com.ips.blecapturer.view.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ips.blecapturer.R
import com.ips.blecapturer.view.fragments.BleFragment
import com.ips.blecapturer.view.fragments.ServerFragment


class MainActivity : AppCompatActivity() {

    val server_fragment = ServerFragment()
    val scanner_fragment = BleFragment()
    val fm = supportFragmentManager

    var active: Fragment = scanner_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fm.beginTransaction().add(R.id.fragmentContainerView, server_fragment, "2").hide(server_fragment).commit()
        fm.beginTransaction().add(R.id.fragmentContainerView, scanner_fragment, "1").commit()

        val nav_buttons = findViewById<BottomNavigationView>(R.id.nav_view)
        nav_buttons.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_server ->
                {
                    fm.beginTransaction().hide(active).show(server_fragment).commit()
                    active = server_fragment
                    true
                }
                R.id.navigation_scanner ->
                {
                    fm.beginTransaction().hide(active).show(scanner_fragment).commit()
                    active = scanner_fragment
                    true
                }
                else -> {
                    false
                }
            }
        }

        nav_buttons.setOnItemReselectedListener {

        }
    }



}

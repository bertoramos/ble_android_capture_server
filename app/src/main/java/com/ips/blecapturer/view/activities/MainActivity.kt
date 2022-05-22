package com.ips.blecapturer.view.activities

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ips.blecapturer.R
import com.ips.blecapturer.view.fragments.BleFragment
import com.ips.blecapturer.view.fragments.ServerFragment


class MainActivity : AppCompatActivity() {

    private lateinit var server_fragment: ServerFragment
    private lateinit var scanner_fragment: BleFragment
    var fm = supportFragmentManager

    private lateinit var active: Fragment

    fun create_view() {
        server_fragment = ServerFragment()
        scanner_fragment = BleFragment()
        active = scanner_fragment

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentVersion = android.os.Build.VERSION.SDK_INT
        val minVersion = android.os.Build.VERSION_CODES.O
        if(currentVersion >= minVersion) {
            create_view()
        } else {
            runOnUiThread {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(title)
                builder.setMessage("The current API version is $currentVersion. The minimum version is $minVersion.")
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("Close app") { _, _ ->
                    finishAndRemoveTask()
                }

                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()

                // Set other dialog properties
                alertDialog.setCancelable(false)

                alertDialog.show()
            }
            Log.d("SDK_ERROR", "The current API version is $currentVersion. The minimum version is $minVersion.")

        }


    }



}

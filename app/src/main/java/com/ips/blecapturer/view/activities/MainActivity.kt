package com.ips.blecapturer.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import com.ips.blecapturer.R
import com.ips.blecapturer.model.database.DatabaseViewModel

class MainActivity : AppCompatActivity() {

    private val database_view_model: DatabaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val filterButton: Button = findViewById(R.id.whiteListButton)
        filterButton.setOnClickListener {
            val intent = Intent(this, WhiteListActivity::class.java).apply {

            }
            startActivity(intent)
        }
    }



}

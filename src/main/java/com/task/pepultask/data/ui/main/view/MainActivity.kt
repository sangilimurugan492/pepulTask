package com.task.pepultask.data.ui.main.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.task.pepultask.R
import com.task.pepultask.utils.Constants

class MainActivity : AppCompatActivity() {
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ivImage : ImageView = findViewById(R.id.ivImage)
        val btclose : Button = findViewById(R.id.bt_close)

        if (intent.extras != null) {
            val url = intent.extras?.get("ImageUrl")
            Glide.with(this).load(url).into(ivImage)
        }

        btclose.setOnClickListener {
            this.finish()
        }

    }
}
package com.android.sample.mpcupload

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.sample.mpcupload.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.toolbar.title = "New Diary"

        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)
        attachActions()
    }

    fun attachActions(){
        binding.ivCrossWhite.setOnClickListener { finish() }
    }

}
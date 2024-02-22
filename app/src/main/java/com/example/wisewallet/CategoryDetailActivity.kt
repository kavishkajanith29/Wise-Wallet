package com.example.wisewallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wisewallet.databinding.ActivityCategoryDetailBinding
import com.example.wisewallet.databinding.ActivityMainBinding

class CategoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryDetailBinding
    private lateinit var categoryName:String
    private lateinit var db:WalletDatabaseHelper
    private lateinit var walletAdapter:WalletAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = WalletDatabaseHelper(this)



        val extras = intent.extras
        if (extras != null) {
            categoryName = extras.getString("categoryName").toString()
        }
        binding.walletCategoryHeading.text = categoryName

        walletAdapter = WalletAdapter(db.getExpensesByCategory(categoryName),this@CategoryDetailActivity)
        binding.walletRecycleView.layoutManager = LinearLayoutManager(this)
        binding.walletRecycleView.adapter = walletAdapter


    }
}
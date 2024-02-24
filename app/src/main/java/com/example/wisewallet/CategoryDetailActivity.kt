package com.example.wisewallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wisewallet.databinding.ActivityCategoryDetailBinding
import com.example.wisewallet.databinding.ActivityMainBinding

class CategoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryDetailBinding
    private lateinit var db:WalletDatabaseHelper
    private lateinit var walletAdapter:WalletAdapter

    private lateinit var selectedDate: String
    private lateinit var categoryName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = WalletDatabaseHelper(this)

        val extras = intent.extras
        if (extras != null) {
            categoryName = extras.getString("categoryName").toString()
            selectedDate = extras.getString("date").toString()
        }
        getCategoryDetails()
    }
    override fun onRestart() {
        super.onRestart()
        getCategoryDetails()
        db.retrieveCategoryTotals(selectedDate)
    }
    fun getCategoryDetails(){
        binding.walletCategoryHeading.text = categoryName

        walletAdapter = if (selectedDate == "All"){
            WalletAdapter(db.getAllExpensesByCategory(categoryName),this@CategoryDetailActivity,selectedDate)
        }else{
            WalletAdapter(db.getExpensesByCategory(categoryName,selectedDate),this@CategoryDetailActivity,selectedDate)
        }

        binding.walletRecycleView.layoutManager = LinearLayoutManager(this)
        binding.walletRecycleView.adapter = walletAdapter
    }

}


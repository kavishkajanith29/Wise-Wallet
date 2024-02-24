package com.example.wisewallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wisewallet.databinding.ActivityWalletViewBinding

class WalletViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletViewBinding
    private lateinit var db:WalletDatabaseHelper
    private lateinit var walletAdapter: WalletAdapter

    private lateinit var Wallettype :String
    private lateinit var selectedDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val extras = intent.extras
        if (extras != null) {
            selectedDate = extras.getString("date").toString()
        }

        db = WalletDatabaseHelper(this)

        walletAdapter = if (selectedDate == "All"){
            WalletAdapter(db.getAllWallet(),this@WalletViewActivity,selectedDate)
        }else{
            WalletAdapter(db.getWallet(selectedDate),this@WalletViewActivity,selectedDate)
        }

        binding.walletRecycleView.layoutManager = LinearLayoutManager(this)
        binding.walletRecycleView.adapter = walletAdapter

        binding.btnExpense.setOnClickListener {
            Wallettype = "Expense"
            walletAdapter = if (selectedDate == "All"){
                WalletAdapter(db.getAllWalletByType(Wallettype),this@WalletViewActivity,selectedDate)
            }else{
                WalletAdapter(db.getWalletByType(Wallettype,selectedDate),this@WalletViewActivity,selectedDate)
            }

            binding.walletRecycleView.layoutManager = LinearLayoutManager(this)
            binding.walletRecycleView.adapter = walletAdapter
        }
        binding.btnIncome.setOnClickListener {
            Wallettype = "Income"
            walletAdapter = if (selectedDate == "All"){
                WalletAdapter(db.getAllWalletByType(Wallettype),this@WalletViewActivity,selectedDate)
            }else{
                WalletAdapter(db.getWalletByType(Wallettype,selectedDate),this@WalletViewActivity,selectedDate)
            }
            binding.walletRecycleView.layoutManager = LinearLayoutManager(this)
            binding.walletRecycleView.adapter = walletAdapter

        }


    }

    override fun onRestart() {
        super.onRestart()
        walletAdapter.refreshData(
            if (selectedDate =="All"){
                db.getAllWallet()
            }else{
                db.getWallet(selectedDate)
            })
    }
}
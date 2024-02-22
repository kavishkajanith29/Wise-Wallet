package com.example.wisewallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wisewallet.databinding.ActivityWalletViewBinding

class WalletViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletViewBinding
    private lateinit var db:WalletDatabaseHelper
    private lateinit var walletAdapter: WalletAdapter
    private lateinit var WalletYpe :String

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityWalletViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = WalletDatabaseHelper(this)

        walletAdapter = WalletAdapter(db.getWallet(),this@WalletViewActivity)

        binding.walletRecycleView.layoutManager = LinearLayoutManager(this)
        binding.walletRecycleView.adapter = walletAdapter

        binding.btnExpense.setOnClickListener {
            WalletYpe = "Expense"
            walletAdapter = WalletAdapter(db.getWalletByType(WalletYpe),this@WalletViewActivity)

            binding.walletRecycleView.layoutManager = LinearLayoutManager(this)
            binding.walletRecycleView.adapter = walletAdapter
        }
        binding.btnIncome.setOnClickListener {
            WalletYpe = "Income"
            walletAdapter = WalletAdapter(db.getWalletByType(WalletYpe),this@WalletViewActivity)

            binding.walletRecycleView.layoutManager = LinearLayoutManager(this)
            binding.walletRecycleView.adapter = walletAdapter

        }


    }

    override fun onRestart() {
        super.onRestart()
        walletAdapter.refreshData(db.getWallet())
    }
}
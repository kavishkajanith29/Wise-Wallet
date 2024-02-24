package com.example.wisewallet

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import com.example.wisewallet.databinding.ActivityUpdateWalletBinding
import java.util.Calendar

class UpdateWalletActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityUpdateWalletBinding
    private  lateinit var db: WalletDatabaseHelper
    private var walletId: Int = -1
    private lateinit var calendar: Calendar
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = WalletDatabaseHelper(this)

        calendar = Calendar.getInstance()

        binding.updateDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        walletId = intent.getIntExtra("wallet_id",-1)
        if(walletId == -1){
            finish()
            return
        }

        val wallet = db.getWalletByID(walletId)
        binding.updateTypeTextView.text = wallet.type
        binding.updateCategoryEditText.text = wallet.category
        binding.updateDescriptionEditText.setText(wallet.description)
        binding.updateAmountEditText.setText(wallet.amount.toString())
        binding.updateDateButton.text = wallet.date

        binding.updateSaveButton.setOnClickListener {
            val newType = binding.updateTypeTextView.text.toString()
            val newCategory = binding.updateCategoryEditText.text.toString()
            val newDescription = binding.updateDescriptionEditText.text.toString()
            val newAmount = binding.updateAmountEditText.text.toString()
            val newDate = binding.updateDateButton.text.toString()

            if (newCategory.isBlank() || newDescription.isBlank() || newAmount.isBlank() || newDate.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            } else {
                val builder = AlertDialog.Builder(this@UpdateWalletActivity)
                builder.setTitle("Confirmation")
                builder.setMessage("Are you sure you want to update this wallet item?")
                builder.setPositiveButton("Yes") { dialog, which ->
                    val updateWallet = Wallet(walletId, newType, newCategory, newDescription, newAmount.toDouble(), newDate)
                    db.updateWallet(updateWallet)
                    finish()
                    Toast.makeText(this,"Change Saved.", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }
        }

    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { view: DatePicker, selectedYear: Int, selectedMonth: Int, dayOfMonth: Int ->
                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.MONTH, selectedMonth)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", dayOfMonth)
                selectedDate = "$selectedYear-$formattedMonth-$formattedDay"
                binding.updateDateButton.text = selectedDate
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }
}
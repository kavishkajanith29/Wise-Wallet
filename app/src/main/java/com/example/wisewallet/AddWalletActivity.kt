package com.example.wisewallet

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import com.example.wisewallet.databinding.ActivityAddWalletBinding
import java.util.Calendar

class AddWalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddWalletBinding
    private lateinit var db:WalletDatabaseHelper
    private lateinit var calendar: Calendar
    private var selectedDate: String? = null
    private lateinit var type:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            type = extras.getString("type").toString()
        }

        db = WalletDatabaseHelper(this)

        calendar = Calendar.getInstance()

        binding.addDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        binding.saveButton.setOnClickListener {
            val type = type
            val category = binding.addCategoryEditText.text.toString()
            val description = binding.addDescriptionEditText.text.toString()
            val amount = binding.addAmountEditText.text.toString()
            val date = selectedDate


            val wallet = Wallet(0, type, category, description, amount.toDouble(),date.toString())

            db.insertWallet(wallet)
            finish()
            Toast.makeText(this,"Wallet Saved", Toast.LENGTH_SHORT).show()
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

                binding.addDateButton.text = selectedDate
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }
}
package com.example.wisewallet

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
    private lateinit var selectedCategory:String

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

        spinnerCategory()
        selectCategory()


        binding.addDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        binding.addCategoryEditText.visibility = View.GONE

        binding.btnNew.setOnClickListener {
            binding.addCategoryEditText.visibility = View.VISIBLE
            binding.categorySpinner.visibility = View.GONE

        }
        binding.btnExisting.setOnClickListener {
            binding.addCategoryEditText.visibility = View.GONE
            binding.categorySpinner.visibility = View.VISIBLE
            spinnerCategory()
        }

//        binding.saveButton.setOnClickListener {
//            val type = type
//            val category:String
//            if (binding.addCategoryEditText.visibility == View.VISIBLE) {
//                category = binding.addCategoryEditText.text.toString()
//            } else {
//                category = selectedCategory
//            }
//            val description = binding.addDescriptionEditText.text.toString()
//            val amount = binding.addAmountEditText.text.toString()
//            val date = selectedDate
//
//
//            val wallet = Wallet(0, type, category, description, amount.toDouble(),date.toString())
//
//            db.insertWallet(wallet)
//            finish()
//            Toast.makeText(this,"Wallet Saved", Toast.LENGTH_SHORT).show()
//        }
        binding.saveButton.setOnClickListener {
            val type = type
            val category: String
            val description = binding.addDescriptionEditText.text.toString()
            val amount = binding.addAmountEditText.text.toString()
            val date = selectedDate

            if (description.isBlank() || amount.isBlank() || date.isNullOrEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            } else {
                category = if (binding.addCategoryEditText.visibility == View.VISIBLE) {
                    binding.addCategoryEditText.text.toString()
                } else {
                    selectedCategory
                }

                val wallet = Wallet(0, type, category, description, amount.toDouble(), date.toString())

                db.insertWallet(wallet)
                finish()
                Toast.makeText(this, "Wallet Saved", Toast.LENGTH_SHORT).show()
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

                binding.addDateButton.text = selectedDate
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    private fun spinnerCategory(){
        val categories = db.getCategory()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        binding.categorySpinner.adapter = adapter

    }

    private fun selectCategory(){
        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
}
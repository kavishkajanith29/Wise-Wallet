package com.example.wisewallet

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.Spinner
import androidx.core.app.NotificationCompat
import com.example.wisewallet.databinding.ActivityMainBinding

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.DateFormatSymbols
import java.text.DecimalFormat
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var db:WalletDatabaseHelper

    private val CHANNEL_ID = "wisewallet_notification_channel"
    private val NOTIFICATION_ID = 123

    private lateinit var calendar: Calendar
    private lateinit var selectedDate: String
    private lateinit var formattedIncome:String
    private lateinit var formattedExpense:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = WalletDatabaseHelper(this)

        getCurrentMonth()
        balance(selectedDate)
        getPieChart(selectedDate)

        binding.btnDateSelect.setOnClickListener {
            showDialog()
        }


        binding.getBalance.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("date", selectedDate)
            val intent = Intent(this,WalletViewActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        binding.incomeButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "Income")
            bundle.putString("date", selectedDate)
            val intent = Intent(this,AddWalletActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        binding.expenseButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "Expense")
            bundle.putString("date", selectedDate)
            val intent = Intent(this,AddWalletActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }
    override fun onRestart() {
        super.onRestart()
        balance(selectedDate)
        getPieChart(selectedDate)
        db.retrieveCategoryTotals(selectedDate)
    }

    fun getCurrentMonth(){
        calendar = Calendar.getInstance()
        val Month = YearMonth.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
        selectedDate = Month.format(formatter)
    }
    private fun balance(setDate: String) {
        val totalIncome:Double
        val totalExpense:Double
        val totalBalance:Double
        val decimalFormat = DecimalFormat("0.00")
        if(setDate=="All"){
            totalIncome = db.getAllIncome()
            totalExpense = db.getAllExpense()
            totalBalance = totalIncome - totalExpense

            formattedExpense = decimalFormat.format(totalExpense).toString()
            formattedIncome = decimalFormat.format(totalIncome).toString()

            val formattedBalance = decimalFormat.format(totalBalance)
            binding.getBalance.text = "Rs. $formattedBalance"
            if (totalBalance < 5000) {
                sendNotification("Total balance is less than Rs.5000.00")
            }
        }else{
            totalIncome = db.getIncome(setDate)
            totalExpense = db.getExpense(setDate)
            totalBalance = totalIncome - totalExpense

            formattedExpense = decimalFormat.format(totalExpense).toString()
            formattedIncome = decimalFormat.format(totalIncome).toString()

            val formattedBalance = decimalFormat.format(totalBalance)
            binding.getBalance.text = "Rs. $formattedBalance"
            if (totalBalance < 5000) {
                sendNotification("Total balance is less than Rs.5000.00")
            }
        }
    }

    private fun getPieChart(setDate: String){
        val categoryTotals = if (setDate == "All"){
            db.retrieveAllCategoryTotals()
        }else{
            db.retrieveCategoryTotals(setDate)
        }

        val entries = ArrayList<PieEntry>()
        for ((category, total) in categoryTotals) {
            entries.add(PieEntry(total.toFloat(), category))
        }

        val pieChart: PieChart = findViewById(R.id.chart)
        val pieDataSet = PieDataSet(entries, "")

        if (entries.isEmpty()) {
            val greyColors = ArrayList<Int>()
            greyColors.add(Color.GRAY)
            pieDataSet.colors = greyColors
        } else {
            pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.asList()
        }

        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.setDrawValues(false)
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.animateY(1000)
        pieChart.invalidate()

        val centerText = "Rs.$formattedIncome\nRs.$formattedExpense"
        val spannableCenterText = SpannableString(centerText)

        spannableCenterText.setSpan(
            ForegroundColorSpan(Color.GREEN),
            0,
            "Rs.$formattedIncome".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableCenterText.setSpan(
            ForegroundColorSpan(Color.RED),
            "Rs.$formattedIncome\n".length,
            centerText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )



        pieChart.centerText = spannableCenterText
        pieChart.setCenterTextSize(30.0F)

        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    val category = (e as PieEntry).label
                    val bundle = Bundle()
                    bundle.putString("categoryName", category)
                    bundle.putString("date",setDate)
                    val intent = Intent(this@MainActivity, CategoryDetailActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }
            override fun onNothingSelected() {
            }
        })
    }

    private fun sendNotification(message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(CHANNEL_ID, "WiseWallet Notifications", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("WiseWallet Notification")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
    private fun showDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_options, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val dialog = dialogBuilder.show()

        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnDay = dialogView.findViewById<Button>(R.id.btnDay)
        val btnMonth = dialogView.findViewById<Button>(R.id.btnMonth)
        val btnYear = dialogView.findViewById<Button>(R.id.btnYear)
        val btnAll = dialogView.findViewById<Button>(R.id.btnAll)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnAll.setOnClickListener {
            dialog.dismiss()
            selectedDate = "All"
            binding.textView.text = selectedDate
            balance(selectedDate)
            getPieChart(selectedDate)
        }

        btnDay.setOnClickListener {
            dialog.dismiss()
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
                    binding.textView.text = selectedDate
                    balance(selectedDate)
                    getPieChart(selectedDate)
                },
                year,
                month,
                dayOfMonth
            )
            datePickerDialog.show()
        }

        btnMonth.setOnClickListener {
            dialog.dismiss()
            val monthPickerDialog = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_year_month_picker, null)
            val yearPicker = dialogView.findViewById<NumberPicker>(R.id.yearPicker)
            val monthSpinner = dialogView.findViewById<Spinner>(R.id.monthSpinner)

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)

            yearPicker.minValue = 1900 // Set your minimum year
            yearPicker.maxValue = 2100 // Set your maximum year
            yearPicker.value = year

            val monthAdapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                DateFormatSymbols().months
            )
            monthSpinner.adapter = monthAdapter
            monthSpinner.setSelection(month)

            monthPickerDialog.setTitle("Select Year and Month")
            monthPickerDialog.setView(dialogView)

            monthPickerDialog.setPositiveButton("Set") { dialog, _ ->
                val selectedYear = yearPicker.value
                val selectedMonth = monthSpinner.selectedItemPosition
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val monthName = DateFormatSymbols().months[selectedMonth]
                selectedDate = "$selectedYear-$formattedMonth"
                binding.textView.text = "$monthName $selectedYear"
                balance(selectedDate)
                getPieChart(selectedDate)
                dialog.dismiss()
            }

            monthPickerDialog.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            monthPickerDialog.show()
        }

        btnYear.setOnClickListener {
            dialog.dismiss()
            val yearPickerDialog = AlertDialog.Builder(this)
            val yearPicker = NumberPicker(this)

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)

            yearPicker.minValue = 1900
            yearPicker.maxValue = 2100
            yearPicker.value = year
            yearPicker.wrapSelectorWheel = false

            yearPickerDialog.setTitle("Select Year")
            yearPickerDialog.setView(yearPicker)

            yearPickerDialog.setPositiveButton("Set") { dialog, _ ->
                selectedDate = yearPicker.value.toString()
                binding.textView.text = selectedDate
                balance(selectedDate)
                getPieChart(selectedDate)
                dialog.dismiss()
            }

            yearPickerDialog.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            yearPickerDialog.show()
        }
    }
}
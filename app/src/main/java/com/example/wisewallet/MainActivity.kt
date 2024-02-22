package com.example.wisewallet

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var db:WalletDatabaseHelper
    private lateinit var walletAdapter:WalletAdapter

    private val CHANNEL_ID = "wisewallet_notification_channel"
    private val NOTIFICATION_ID = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = WalletDatabaseHelper(this)

        balance()
        getPieChart()


        binding.getBalance.setOnClickListener {
            val intent = Intent(this,WalletViewActivity::class.java)
            startActivity(intent)
        }

        binding.incomeButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "Income")
            val intent = Intent(this,AddWalletActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        binding.expenseButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "Expense")
            val intent = Intent(this,AddWalletActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }
    override fun onRestart() {
        super.onRestart()
        balance()
        getPieChart()
        db.retrieveCategoryTotals()
    }
    @SuppressLint("SetTextI18n")
    private fun balance(){
        val totalBalance = db.getBalance()
        val decimalFormat = DecimalFormat("0.00")
        val formattedBalance = decimalFormat.format(totalBalance)
        binding.getBalance.text = "Rs. $formattedBalance"
        if (totalBalance < 5000) {
            sendNotification("Total balance is less than Rs.5000.00")
        }
    }

    private fun getPieChart(){
        val categoryTotals = db.retrieveCategoryTotals()

        val entries = ArrayList<PieEntry>()
        for ((category, total) in categoryTotals) {
            entries.add(PieEntry(total.toFloat(), category))
        }

        val pieChart: PieChart = findViewById(R.id.chart)
        val pieDataSet = PieDataSet(entries, "")

        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.asList()
        pieDataSet.setDrawValues(false)
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.animateY(1000)
        pieChart.invalidate()

        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    val category = (e as PieEntry).label
                    val bundle = Bundle()
                    bundle.putString("categoryName", category)
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
}
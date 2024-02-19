package com.example.wisewallet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

class WalletAdapter(private var wallet: List<Wallet>,private val context: Context): RecyclerView.Adapter<WalletAdapter.WalletViewHolder>() {

    private val db:WalletDatabaseHelper = WalletDatabaseHelper(context)
    private lateinit var WalletCategory : String

    class WalletViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val typeTextView: TextView = itemView.findViewById(R.id.typeTextView)
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wallet_item,parent,false)
        return WalletViewHolder(view)
    }

    override fun getItemCount(): Int = wallet.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        val wallet = wallet[position]
        val decimalFormat = DecimalFormat("0.00")
        val balance = decimalFormat.format(wallet.amount)

        holder.typeTextView.text = wallet.type
        holder.categoryTextView.text = wallet.category
        holder.descriptionTextView.text = wallet.description
        holder.amountTextView.text = "Rs. $balance"
        holder.dateTextView.text = wallet.date

        if (wallet.type == "Expense") {
            holder.typeTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            holder.deleteButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.red))
            holder.updateButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.red))
        }

        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context,UpdateWalletActivity::class.java).apply{
                putExtra("wallet_id",wallet.id)
            }
            holder.itemView.context.startActivity(intent)
        }
        holder.deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure you want to delete this wallet item?")
            builder.setPositiveButton("Yes") { dialog, which ->
                db.deleteWallet(wallet.id)
                refreshData(db.getWallet())
                Toast.makeText(holder.itemView.context, "Wallet item Deleted.", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
    fun refreshData(newWallet: List<Wallet>){
        wallet = newWallet
        notifyDataSetChanged()
    }
}
package com.android.bitapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.bitapp.databinding.TradesItemBinding
import com.android.bitapp.models.Trade
import com.android.bitapp.utils.TradeViewHolder

class TradesAdapter(var dataList: ArrayList<Trade>) : RecyclerView.Adapter<TradeViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeViewHolder {
        val inflater = LayoutInflater.from(context)
        return TradeViewHolder(TradesItemBinding.inflate(inflater, parent, false))
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onBindViewHolder(holder: TradeViewHolder, position: Int) {
        holder.binding.amount.text = dataList[position].amount.toString()
        holder.binding.price.text = dataList[position].price.toString()
        holder.binding.time.text = dataList[position].milliSecondStamp.toString()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}


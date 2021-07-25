package com.android.bitapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.bitapp.R
import com.android.bitapp.databinding.AllPairsListItemBinding
import com.android.bitapp.models.AllPairItem
import com.android.bitapp.utils.AllPairsViewHolder
import com.android.bitapp.utils.formatTwoDecimal
import com.android.bitapp.utils.interfaces.ClickListenerRv

class AllPairsAdapter(
    var list: List<AllPairItem>?,
    var usedFromSwitcher: Boolean,
    private var clickListenerRv: ClickListenerRv
) :
    RecyclerView.Adapter<AllPairsViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllPairsViewHolder {
        val inflater = LayoutInflater.from(context)
        return AllPairsViewHolder(AllPairsListItemBinding.inflate(inflater))
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onBindViewHolder(holder: AllPairsViewHolder, position: Int) {

        holder.binding.pairNameTv.text = list?.get(position)?.name

        list?.get(position)?.dailyChangeRelative?.let { relativeChange ->
            holder.binding.pairDailyChangeTv.text =
                formatTwoDecimal(list?.get(position)?.dailyChange)
                    .plus(" (")
                    .plus(formatTwoDecimal(relativeChange)).plus("%)")
        } ?: kotlin.run {
            holder.binding.pairDailyChangeTv.text =
                formatTwoDecimal(list?.get(position)?.dailyChange)
        }

        list?.get(position)?.dailyChange?.let { lastPrice ->
            if (lastPrice >= 0) {
                holder.binding.pairDailyChangeTv.setTextColor(
                    context.resources.getColor(
                        R.color.green,
                        null
                    )
                )
            } else {
                holder.binding.pairDailyChangeTv.setTextColor(
                    context.resources.getColor(
                        R.color.red,
                        null
                    )
                )
            }
        }

        holder.binding.pairLastPriceTv.text = list?.get(position)?.lastPrice

        holder.itemView.setOnClickListener {
            clickListenerRv.onItemClick(list?.get(position))
        }
    }

    override fun getItemCount(): Int {
        list?.let { list ->
            return list.size
        }
        return 0
    }
}



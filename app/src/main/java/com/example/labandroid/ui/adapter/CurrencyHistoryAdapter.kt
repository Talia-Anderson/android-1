package com.example.labandroid.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.labandroid.databinding.ItemHistoryBinding
import com.example.labandroid.domain.model.CurrencyHistoryEntry

class CurrencyHistoryAdapter :
    ListAdapter<CurrencyHistoryEntry, CurrencyHistoryAdapter.HistoryViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CurrencyHistoryEntry) {
            binding.dateText.text = item.date
            binding.rateText.text = String.format("%.4f", item.rate)
        }
    }

    private object Diff : DiffUtil.ItemCallback<CurrencyHistoryEntry>() {
        override fun areItemsTheSame(oldItem: CurrencyHistoryEntry, newItem: CurrencyHistoryEntry): Boolean {
            return oldItem.currencyId == newItem.currencyId && oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: CurrencyHistoryEntry, newItem: CurrencyHistoryEntry): Boolean {
            return oldItem == newItem
        }
    }
}

package com.example.labandroid.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.labandroid.databinding.ItemCurrencyBinding
import com.example.labandroid.domain.model.CurrencyRate

class CurrencyAdapter(
    private val onItemClick: (CurrencyRate) -> Unit,
    private val onFavoriteClick: (CurrencyRate) -> Unit
) : ListAdapter<CurrencyRate, CurrencyAdapter.CurrencyViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val binding = ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CurrencyViewHolder(
        private val binding: ItemCurrencyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CurrencyRate) {
            binding.codeText.text = item.code
            binding.nameText.text = item.name
            binding.rateText.text = String.format("%.4f", item.rate)
            binding.favoriteButton.isChecked = item.isFavorite

            binding.root.setOnClickListener { onItemClick(item) }
            binding.favoriteButton.setOnClickListener { onFavoriteClick(item) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<CurrencyRate>() {
        override fun areItemsTheSame(oldItem: CurrencyRate, newItem: CurrencyRate): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CurrencyRate, newItem: CurrencyRate): Boolean = oldItem == newItem
    }
}

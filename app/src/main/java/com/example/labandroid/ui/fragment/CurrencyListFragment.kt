package com.example.labandroid.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.labandroid.R
import com.example.labandroid.databinding.FragmentCurrencyListBinding
import com.example.labandroid.ui.adapter.CurrencyAdapter
import com.example.labandroid.ui.viewmodel.CurrencyListViewModel
import com.example.labandroid.ui.viewmodel.SortMode
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CurrencyListFragment : Fragment(R.layout.fragment_currency_list) {
    private var _binding: FragmentCurrencyListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CurrencyListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCurrencyListBinding.bind(view)

        val adapter = CurrencyAdapter(
            onItemClick = { item ->
                Snackbar.make(binding.root, "Выбрана валюта ${item.code}", Snackbar.LENGTH_SHORT).show()
                val direction = CurrencyListFragmentDirections
                    .actionCurrencyListFragmentToCurrencyHistoryFragment(item.id, item.code)
                findNavController().navigate(direction)
            },
            onFavoriteClick = { viewModel.toggleFavorite(it) }
        )
        binding.currencyRecycler.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }
        binding.refreshButton.setOnClickListener { viewModel.refresh() }
        binding.searchEdit.doAfterTextChanged { viewModel.setFilter(it?.toString().orEmpty()) }

        // Настройка Spinner
        val sortItems = listOf("По коду", "По курсу", "По дате")
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            sortItems
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.sortSpinner.adapter = spinnerAdapter
        binding.sortSpinner.setSelection(0)

        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val mode = when (position) {
                    1 -> SortMode.RATE
                    2 -> SortMode.DATE
                    else -> SortMode.CODE
                }
                viewModel.setSort(mode)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Ничего не делаем
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.currencies.collect { adapter.submitList(it) } }
                launch {
                    viewModel.isRefreshing.collect { refreshing ->
                        binding.swipeRefresh.isRefreshing = refreshing
                    }
                }
                launch {
                    viewModel.message.collect { message ->
                        message ?: return@collect
                        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                        viewModel.consumeMessage()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
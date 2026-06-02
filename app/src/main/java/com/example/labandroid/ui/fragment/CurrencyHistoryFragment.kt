package com.example.labandroid.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.labandroid.R
import com.example.labandroid.databinding.FragmentCurrencyHistoryBinding
import com.example.labandroid.ui.adapter.CurrencyHistoryAdapter
import com.example.labandroid.ui.viewmodel.CurrencyHistoryViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CurrencyHistoryFragment : Fragment(R.layout.fragment_currency_history) {
    private var _binding: FragmentCurrencyHistoryBinding? = null
    private val binding get() = _binding!!
    private val args: CurrencyHistoryFragmentArgs by navArgs()
    private val viewModel: CurrencyHistoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCurrencyHistoryBinding.bind(view)
        val adapter = CurrencyHistoryAdapter()
        binding.historyRecycler.adapter = adapter
        binding.historyRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.titleText.text = getString(R.string.history_title_with_code, args.currencyCode)

        viewModel.load(args.currencyId)
        binding.refreshHistoryButton.setOnClickListener { viewModel.refresh() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.history.collect { adapter.submitList(it) } }
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

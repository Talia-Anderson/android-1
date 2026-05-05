package com.example.labandroid.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labandroid.model.Currency
import com.example.labandroid.ui.components.AddEditDialog
import com.example.labandroid.ui.components.CurrencyCard
import com.example.labandroid.ui.components.TopBar
import com.example.labandroid.ui.viewmodel.CurrencyViewModel

@Composable
fun CurrencyScreen(
    viewModel: CurrencyViewModel = viewModel()
) {
    val currencies by viewModel.currencies.collectAsState()
    val showAddDialog = remember { mutableStateOf(false) }
    val editingCurrency = remember { mutableStateOf<Currency?>(null) }

    Scaffold(
        topBar = {
            TopBar(
                title = "Курсы валют",
                onAddClick = { showAddDialog.value = true }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (currencies.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет валют. Нажмите + для добавления",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
                ) {
                    items(currencies) { currency ->
                        CurrencyCard(
                            currency = currency,
                            onEdit = { editingCurrency.value = currency },
                            onDelete = { viewModel.deleteCurrency(currency) },
                            onRefreshRate = { viewModel.updateRandomRate(currency) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog.value) {
        AddEditDialog(
            title = "Добавить валюту",
            onDismiss = { showAddDialog.value = false },
            onConfirm = { code, icon ->
                viewModel.addCurrency(code, icon)
                showAddDialog.value = false
            }
        )
    }

    editingCurrency.value?.let { currency ->
        AddEditDialog(
            title = "Редактировать валюту",
            initialCode = currency.code,
            onDismiss = { editingCurrency.value = null },
            onConfirm = { code, icon ->
                viewModel.updateCurrency(currency, code, icon)
                editingCurrency.value = null
            }
        )
    }
}
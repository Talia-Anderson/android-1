package com.example.labandroid.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun AddEditDialog(
    title: String,
    initialCode: String = "",
    onDismiss: () -> Unit,
    onConfirm: (code: String, icon: ImageVector) -> Unit
) {
    val code = remember { mutableStateOf(TextFieldValue(initialCode)) }
    val selectedIcon = remember { mutableStateOf<ImageVector?>(null) }

    val availableIcons = listOf(
        Icons.Filled.AttachMoney to "USD",
        Icons.Filled.Euro to "EUR",
        Icons.Filled.Paid to "GBP"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = code.value,
                    onValueChange = { code.value = it },
                    label = { Text("Код валюты (3 буквы)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Выберите иконку:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableIcons.forEach { (icon, name) ->
                        IconButton(
                            onClick = { selectedIcon.value = icon }
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = name,
                                modifier = Modifier.padding(4.dp),
                                tint = if (selectedIcon.value == icon)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                if (selectedIcon.value == null) {
                    Text(
                        text = "Пожалуйста, выберите иконку",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val codeText = code.value.text.trim().uppercase()
                    if (codeText.length == 3 && selectedIcon.value != null) {
                        onConfirm(codeText, selectedIcon.value!!)
                    }
                },
                enabled = code.value.text.trim().length == 3 && selectedIcon.value != null
            ) {
                Text("Подтвердить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
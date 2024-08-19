package com.example.mathmagic

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorApp()
        }
    }
}

@Composable
fun CalculatorApp() {
    var calculatorState by remember { mutableStateOf(CalculatorState()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = calculatorState.resultText,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color.White,
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("0", ".", "=", "+"),
            listOf("C", "DEL")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { text ->
                    CalculatorButton(text = text) {
                        try {
                            calculatorState = calculatorState.handleInput(text)
                        } catch (e: Exception) {
                            Log.e("CalculatorApp", "Error handling input: $text", e)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CalculatorButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(80.dp)
            .padding(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.LightGray,
            contentColor = Color.DarkGray
        )
    ) {
        Text(text = text, fontSize = 24.sp)
    }
}

data class CalculatorState(
    val resultText: String = "0",
    val currentInput: String = "",
    val operator: String? = null,
    val operand1: Double? = null
) {
    fun handleInput(input: String): CalculatorState {
        return when (input) {
            "C" -> CalculatorState()
            "DEL" -> copy(resultText = resultText.dropLast(1).ifEmpty { "0" })
            in listOf("+", "-", "*", "/") -> {
                val operand1 = resultText.toDoubleOrNull()
                if (operand1 != null) {
                    copy(operator = input, operand1 = operand1, currentInput = "")
                } else {
                    this
                }
            }
            "=" -> calculateResult()
            else -> {
                val newInput = if (currentInput == "0") input else currentInput + input
                copy(resultText = newInput, currentInput = newInput)
            }
        }
    }

    private fun calculateResult(): CalculatorState {
        val operand2 = currentInput.toDoubleOrNull() ?: return this
        val result = when (operator) {
            "+" -> operand1?.plus(operand2)
            "-" -> operand1?.minus(operand2)
            "*" -> operand1?.times(operand2)
            "/" -> operand1?.div(operand2)
            else -> null
        }
        val resultText = result?.let {
            if (it % 1 == 0.0) it.toInt().toString() else it.toString()
        } ?: "Error"
        return copy(resultText = resultText, currentInput = "", operator = null, operand1 = null)
    }
}
//this provides a preview of the app in the android studio
@Preview(showBackground = true)
@Composable
fun CalculatorAppPreview() {
    CalculatorApp()
}
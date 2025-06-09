package com.example.receiptlogger.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.receiptlogger.model.Check
import com.example.receiptlogger.model.CheckItem
import com.example.receiptlogger.ui.theme.ReceiptLoggerTheme

@Composable
fun CheckScreen(check: Check?, modifier: Modifier = Modifier) {
    if (check == null) {
        Text(text = "Something went wrong", color = Color.Red)
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(check.description, fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center)
        }
        HorizontalDivider(thickness = 2.dp)
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

        check.items.forEach {
            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    it.name, modifier = Modifier
                        .weight(1.0f)
                        .padding(end = 8.dp)
                )
                Text("${it.count.toInt()} x ${it.itemPrice}= ${it.totalPrice}")
            }

            Canvas(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            ) {
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    pathEffect = pathEffect
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("Total")
            Text("${check.totalPrice}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckScreenPreview() {

    val check = Check(
        "IMENSITATE S.R.L.\n" +
                "COD FISCAL: 1002600011694\n" +
                "mun. Chisinau str. Ion Creanga, 45/1\n" +
                "NUMARUL DE ÎNREGISTRARE: J402005328",
        listOf(
            CheckItem(
                name = "Branzica glazur.vanil/lap.cond. 26% 50g JLC",
                count = 3.0f,
                itemPrice = 7.75f,
                totalPrice = 23.25f,
            ),
            CheckItem(
                name = "Branzica glazur.vanil/lap.cond. 26% 50g JLC",
                count = 3.0f,
                itemPrice = 7.75f,
                totalPrice = 23.25f,
            ),
            CheckItem(
                name = "Branzica glazur.vanil/lap.cond. 26% 50g JLC",
                count = 3.0f,
                itemPrice = 7.75f,
                totalPrice = 23.25f,
            ),
            CheckItem(
                name = "Branzica glazur.vanil/lap.cond. 26% 50g JLC",
                count = 3.0f,
                itemPrice = 7.75f,
                totalPrice = 23.25f,
            ),
            CheckItem(
                name = "Branzica glazur.vanil/lap.cond. 26% 50g JLC",
                count = 3.0f,
                itemPrice = 7.75f,
                totalPrice = 23.25f,
            )
        ),
        219.53f
    )
    ReceiptLoggerTheme {
        CheckScreen(check = check)
    }
}

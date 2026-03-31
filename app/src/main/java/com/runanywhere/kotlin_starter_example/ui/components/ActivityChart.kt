package com.runanywhere.kotlin_starter_example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ActivityChart() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text("Activity")

            Row(
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val heights = listOf(0.4f, 0.8f, 0.5f, 0.3f, 0.5f, 0.9f, 0.2f)
                val labels = listOf("S", "M", "T", "W", "T", "F", "S")

                heights.forEachIndexed { index, h ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .fillMaxHeight(h)
                                .background(Color(0xFFD1C4E9), RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(labels[index])
                    }
                }
            }
        }
    }
}

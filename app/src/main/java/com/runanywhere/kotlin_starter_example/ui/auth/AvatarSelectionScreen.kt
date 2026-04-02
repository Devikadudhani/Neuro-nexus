package com.runanywhere.kotlin_starter_example.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.kotlin_starter_example.R

@Composable
fun AvatarSelectionScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1F8))
            .padding(24.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                "SKIP",
                color = Color(0xFF5E3A79),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate("loading")
                }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            "Choose an Avatar!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5E3A79),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                AvatarItem(R.drawable.logo, navController)
                AvatarItem(R.drawable.logo, navController)
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                AvatarItem(R.drawable.logo, navController)
                AvatarItem(R.drawable.logo, navController)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "OR",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5E3A79),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Upload Profile Picture\nfrom your gallery!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF5E3A79),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .clickable { navController.navigate("loading") }
                .border(2.dp, Color(0xFF5E3A79), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.AddPhotoAlternate,
                contentDescription = null,
                tint = Color(0xFF5E3A79),
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun AvatarItem(image: Int, navController: NavController) {
    Image(
        painter = painterResource(id = image),
        contentDescription = "avatar",
        modifier = Modifier
            .size(140.dp)
            .clip(CircleShape)
            .clickable {
                navController.navigate("loading")
            }
    )
}
package com.example.pixelbit.presentation.features.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pixelbit.domain.model.Category
import com.example.pixelbit.presentation.theme.CardDetailsBackgroundLight

@Composable
fun CategoryItem(
    category: Category,
    index: Int,
    modifier: Modifier = Modifier
) {
    val isTextOnLeft = index % 2 == 0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = CardDetailsBackgroundLight)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isTextOnLeft) {
                CategoryText(
                    category, Modifier
                        .weight(1f)
                        .padding(start = 24.dp)
                )
                CategoryImage(category, Modifier.weight(1f))
            } else {
                CategoryImage(category, Modifier.weight(1f))
                CategoryText(
                    category, Modifier
                        .weight(1f)
                        .padding(start = 24.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryText(category: Category, modifier: Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Text(
            text = category.title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${category.itemCount} Product",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun CategoryImage(category: Category, modifier: Modifier) {
    AsyncImage(
        model = category.imageUrl,
        contentDescription = category.title,
        modifier = modifier
            .fillMaxHeight()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop
    )
}

@Preview(showBackground = true)
@Composable
fun CategoryItemPreview() {
    CategoryItem(category = Category("1", "T-Shirts", 120, ""), index = 0)
}

@Preview(showBackground = true)
@Composable
fun CategoryItemPreviewRight() {
    CategoryItem(category = Category("1", "T-Shirts", 120, ""), index = 1)
}
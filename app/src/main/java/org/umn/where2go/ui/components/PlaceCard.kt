package org.umn.where2go.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.umn.where2go.data.Place
import org.umn.where2go.data.Vibe
import org.umn.where2go.data.distanceLabel
import org.umn.where2go.ui.theme.*

@Composable
fun PlaceCard(
    place: Place,
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
            ) {
                if (place.imageRes != null) {
                    Image(
                        painter = painterResource(place.imageRes),
                        contentDescription = place.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Peach,
                                        Lavender,
                                        Violet
                                    )
                                )
                            )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color(0xAA000000)
                                ),
                                startY = 80f
                            )
                        )
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(32.dp),
                    shape = CircleShape,
                    color = LavenderSoft
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = when (place.vibe) {
                                Vibe.QUIET -> "🌱"
                                Vibe.STUDY -> "📚"
                                Vibe.LIVELY -> "☕"
                            },
                            fontSize = 16.sp
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = place.name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )

                    Text(
                        text = "📍 ${place.distanceLabel()}",
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }

                if (isFavorite) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorit",
                        tint = Vermilion,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .size(21.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Lavender)
                    .padding(horizontal = 10.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tags = place.facilities.take(3)

                if (tags.isEmpty()) {
                    PlaceTag("Tempat nyaman")
                } else {
                    tags.forEach { tag ->
                        PlaceTag(tag)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceTag(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = LavenderSoft
    ) {
        Text(
            text = text,
            color = TitleInk,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 5.dp
            )
        )
    }
}

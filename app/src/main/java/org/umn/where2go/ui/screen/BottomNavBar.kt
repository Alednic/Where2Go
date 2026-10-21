package org.umn.where2go.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.umn.where2go.navigation.Routes
import org.umn.where2go.ui.theme.NavInactive
import org.umn.where2go.ui.theme.Violet

private data class NavTab(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val leftTabs = listOf(
    NavTab(Routes.MAP, "Map", Icons.Outlined.Map),
    NavTab(Routes.EXPLORE, "Explore", Icons.Outlined.Search)
)

private val rightTabs = listOf(
    NavTab(Routes.LEADERBOARD, "Ranking", Icons.Outlined.EmojiEvents),
    NavTab(Routes.PROFILE, "Profil", Icons.Outlined.Person)
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onCheckInClick: () -> Unit
) {
    Box(Modifier.fillMaxWidth()) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 16.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leftTabs.forEach { tab ->
                    NavItem(
                        tab = tab,
                        selected = tab.route == currentRoute,
                        modifier = Modifier.weight(1f)
                    ) {
                        onNavigate(tab.route)
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "Check In",
                        color = NavInactive,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                rightTabs.forEach { tab ->
                    NavItem(
                        tab = tab,
                        selected = tab.route == currentRoute,
                        modifier = Modifier.weight(1f)
                    ) {
                        onNavigate(tab.route)
                    }
                }
            }
        }

        CheckInPin(
            onClick = onCheckInClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-44).dp)
        )
    }
}

@Composable
private fun NavItem(
    tab: NavTab,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val color =
        if (selected) MaterialTheme.colorScheme.primary
        else NavInactive

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.label,
            tint = color,
            modifier = Modifier.size(28.dp)
        )

        Text(
            text = tab.label,
            color = color,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun CheckInPin(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(50.dp)
                .rotate(45f)
                .background(
                    Violet,
                    RoundedCornerShape(
                        topStart = 25.dp,
                        topEnd = 25.dp,
                        bottomStart = 25.dp,
                        bottomEnd = 4.dp
                    )
                )
        )

        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "Check In",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
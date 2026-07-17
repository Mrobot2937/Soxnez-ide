package com.mobileide.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobileide.app.model.OpenTab
import com.mobileide.app.ui.theme.TabActiveBackground
import com.mobileide.app.ui.theme.TabBarBackground
import com.mobileide.app.ui.theme.TabInactiveText
import com.mobileide.app.ui.theme.TextPrimary

@Composable
fun TopTabBar(
    tabs: List<OpenTab>,
    activeTab: OpenTab?,
    onTabSelected: (OpenTab) -> Unit,
    onTabClosed: (OpenTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TabBarBackground)
            .horizontalScroll(rememberScrollState())
    ) {
        tabs.forEach { tab ->
            val isActive = tab.key == activeTab?.key
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(if (isActive) TabActiveBackground else TabBarBackground)
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = tab.displayName,
                    color = if (isActive) TextPrimary else TabInactiveText,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (tab.isDirty) {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = "não salvo",
                        tint = TextPrimary,
                        modifier = Modifier.size(8.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "fechar",
                        tint = TabInactiveText,
                        modifier = Modifier
                            .size(14.dp)
                            .clickable { onTabClosed(tab) }
                    )
                }
            }
        }
    }
}

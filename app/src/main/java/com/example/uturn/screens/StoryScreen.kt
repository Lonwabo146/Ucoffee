package com.example.uturn.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uturn.R
import com.example.uturn.ui.theme.UturnCardBorder
import com.example.uturn.ui.theme.UturnGreen
import com.example.uturn.ui.theme.UturnTextDark
import com.example.uturn.ui.theme.UturnTextGrey

private data class Phase(val number: Int, val title: String, val description: String)

// Wording follows U-Turn's own site (homeless.org.za/about). Check it again before release.
private val phases = listOf(
    Phase(
        1, "Homeless Support Centre",
        "Basic needs like meals, clothing and washing facilities are met through vouchers, " +
                "which also keeps cash off the streets."
    ),
    Phase(
        2, "Rehabilitation",
        "When someone is ready, they receive therapy and rehabilitation, with shelter."
    ),
    Phase(
        3, "Work-Readiness",
        "Participants, known as Champions, work in U-Turn's social enterprises, build skills " +
                "and keep receiving therapy."
    ),
    Phase(
        4, "Independent Employment",
        "Champions move into open-labour jobs and join a peer-led support group."
    )
)


private val storyQuote: String? = "Roasting coffee gave me a routine and a reason to get up again."

private const val UTURN_SITE = "https://homeless.org.za"

@Composable
fun StoryScreen(onBrowse: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Green header (the background is applied before the status-bar padding, so it
        // reaches the top edge of the screen)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(UturnGreen)
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
                Image(
                    painter = painterResource(id = R.drawable.ucoffee_logo),
                    contentDescription = "U-Turn Coffee",
                    modifier = Modifier
                        .padding(12.dp)
                        .height(48.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text("Our Impact", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(4.dp))
            Text("Coffee with purpose", fontSize = 16.sp, color = Color.White)
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "U-Turn Homeless Ministries helps people leave the streets and rebuild " +
                        "their lives through a four-phase programme. Every cup of U-Turn Coffee " +
                        "helps fund that work.",
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = UturnTextDark
            )

            Spacer(Modifier.height(28.dp))
            Text(
                text = "The journey",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = UturnGreen
            )
            Spacer(Modifier.height(16.dp))
            phases.forEachIndexed { index, phase ->
                PhaseStep(phase = phase, isLast = index == phases.lastIndex)
            }

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    value = "70%",
                    label = "successful rehabilitation rate",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "80%",
                    label = "of graduates stay employed and sober",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Figures as reported by U-Turn",
                fontSize = 12.sp,
                color = UturnTextGrey
            )

            storyQuote?.let { quote ->
                Spacer(Modifier.height(24.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, UturnCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FormatQuote,
                            contentDescription = null,
                            tint = UturnGreen,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = quote,
                            fontSize = 15.sp,
                            fontStyle = FontStyle.Italic,
                            color = UturnTextDark
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            Button(
                onClick = onBrowse,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Browse coffee", fontSize = 16.sp)
            }
            TextButton(
                onClick = { uriHandler.openUri(UTURN_SITE) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Learn more about U-Turn", color = UturnGreen)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PhaseStep(phase: Phase, isLast: Boolean) {
    // IntrinsicSize.Min lets the connecting line stretch to the height of the text beside it
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(UturnGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${phase.number}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(UturnCardBorder)
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 20.dp)) {
            Text(
                text = phase.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = UturnTextDark
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = phase.description,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = UturnTextGrey
            )
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, UturnCardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = UturnGreen)
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = UturnTextGrey,
                textAlign = TextAlign.Center
            )
        }
    }
}
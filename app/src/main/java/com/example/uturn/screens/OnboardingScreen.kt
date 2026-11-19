package com.example.uturn.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uturn.data.SessionManager
import com.example.uturn.ui.theme.UturnGreen
import com.example.uturn.ui.theme.UturnTextGrey
import kotlinx.coroutines.launch

import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.uturn.R

private data class OnboardingPage(val imageRes: Int, val title: String, val description: String)

private val pages = listOf(
    OnboardingPage(
        imageRes = R.drawable.everycupfundsafreshstart,
        title = "Every cup funds a fresh start",
        description = "UCoffee roasts and sells coffee to fund our homeless ministry's stabilisation, rehabilitation and reintegration programme."
    ),
    OnboardingPage(
        imageRes = R.drawable.coffee2,
        title = "Trained hands, fresh roasts",
        description = "People from the programme are trained as baristas and roasters — every order supports their journey to employment."
    ),

    OnboardingPage(
        imageRes = R.drawable.ice,
        title = "Order in a few taps",
        description = "Choose your drink, pick a size, and pay securely with PayFast . Delivered fresh to your door."
    )
)

@Composable
fun OnboardingScreen(
    sessionManager: SessionManager,
    onFinished: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    fun finish() {
        scope.launch {
            sessionManager.setOnboardingDone()
            onFinished()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // Skip button — hidden on the last page (the button becomes "Get Started")
        Box(modifier = Modifier.fillMaxWidth()) {
            if (pagerState.currentPage < pages.size - 1) {
                TextButton(
                    onClick = { finish() },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text("Skip", color = UturnTextGrey)
                }
            }
        }


        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->

            val page = pages[pageIndex]

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = page.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = page.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = UturnGreen,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = page.description,
                    fontSize = 15.sp,
                    color = UturnTextGrey,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }



        // Page dots
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(pages.size) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(if (selected) 24.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (selected) UturnGreen else Color(0xFFD6D6D6))
                )
            }
        }

        val isLastPage = pagerState.currentPage == pages.size - 1
        Button(
            onClick = {
                if (isLastPage) {
                    finish()
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (isLastPage) "Get Started" else "Next")
        }
        Spacer(Modifier.height(16.dp))
    }

}

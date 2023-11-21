package com.mycart.ui.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.mycart.domain.model.Category
import com.mycart.ui.utils.FetchImageFromUrl
import com.mycart.ui.utils.getColorFromHex
import kotlinx.coroutines.launch



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DealsComposable(deals: List<Category>, onClick: (String, String) -> Unit) {

    HorizontalPagerWithIndicators(deals, onClick)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagerWithIndicators(deals: List<Category>, onClick: (String, String) -> Unit) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        deals.size
        // provide pageCount
    }
    val coroutineScope = rememberCoroutineScope()
    Column {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 10.dp
        ) { page ->
            Column() {
                DisplayDeal(deal = deals[page], page%2 == 0 ,onClick)

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp, bottom = 10.dp)
                ) {
                    HorizontalPagerIndicator(
                        pageCount = deals.size,
                        pagerState = pagerState,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clickable {
                                val currentPage = pagerState.currentPage
                                val totalPages = deals.size
                                val nextPage =
                                    if (currentPage < totalPages - 1) currentPage + 1 else 0
                                coroutineScope.launch { pagerState.animateScrollToPage(nextPage) }
                            }

                    )
                }
            }
            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }
                    .collect { currentPage ->
                        pagerState.animateScrollToPage(currentPage)
                    }
            }
        }
    }

}

@Composable
fun DisplayDeal(deal: Category, isWhite:Boolean,onClick: (String, String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(if (isWhite) getColorFromHex("#D8C9C5") else getColorFromHex("#ABABDF"))
            .border(2.dp, Color.Blue, RoundedCornerShape(10.dp))
            .clickable {
                onClick(deal.categoryName, deal.storeName)
            },
        contentAlignment = Alignment.Center,
    ) {

        FetchImageFromUrl(imageUrl = deal.categoryImage,modifier=Modifier.align(Alignment.CenterEnd), imageSize = 100.dp)

        Text(
            text = deal.categoryName,
            color = Color.Blue,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
        Text(
            text = deal.dealInfo,
            color = Color.Blue,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(8.dp)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
        OutlinedButton(onClick = {  onClick(deal.categoryName, deal.storeName)},modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(8.dp)
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .border(2.dp,Color.Blue, RoundedCornerShape(10.dp))) {
            Text(text = "Shop Now", color = Color.Black, fontSize = 20.sp)
            
        }
    }

}
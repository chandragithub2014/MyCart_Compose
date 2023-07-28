package com.mycart.ui.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import com.mycart.domain.model.Deal
import com.mycart.ui.category.viewmodel.CategoryViewModel
import com.mycart.ui.utils.FetchImageFromUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DealsComposable(categoryViewModel: CategoryViewModel = get()) {
    val dealList: List<Deal> = categoryViewModel.dealList.value

    LaunchedEffect(Unit) {
        categoryViewModel.fetchDeals()
    }
    HorizontalPagerWithIndicators(dealList)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagerWithIndicators(deals: List<Deal>) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
      Column{
        HorizontalPager(pageCount = deals.size, state = pagerState,
            contentPadding = PaddingValues(horizontal = 20.dp), pageSpacing = 10.dp) { page ->
             Column() {
                 DisplayDeal(deal = deals[page])

                 Box(
                     modifier = Modifier
                         .align(Alignment.CenterHorizontally)
                         .padding(top = 10.dp, bottom = 10.dp)
                 ) {
                     HorizontalPagerIndicator(
                         pageCount = deals.size,
                         pagerState = pagerState,
                         modifier = Modifier.align(Alignment.Center).clickable {
                             val currentPage = pagerState.currentPage
                             val totalPages = deals.size
                             val nextPage = if (currentPage < totalPages - 1) currentPage + 1 else 0
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
fun DisplayDeal(deal: Deal) {
    Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.Red),
            contentAlignment = Alignment.Center,
    ) {
        FetchImageFromUrl(imageUrl = deal.category.categoryImage)
        Text(
            text = deal.dealValue,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(Color.Black)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }

}
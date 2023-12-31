package com.mycart.ui.category


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mycart.domain.model.Category
import com.mycart.ui.category.viewmodel.CategoryViewModel
import org.koin.androidx.compose.get
import com.mycart.R
import com.mycart.domain.model.SeasonalCategory
import com.mycart.ui.utils.DisplayLabel
import com.mycart.ui.utils.DisplayOutLinedLabel
import com.mycart.ui.utils.FetchImageFromURLWithPlaceHolder


@Composable
fun Category() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My Cart") }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),

            ) {
                item {
                    DisplayLabel("Hot Deals", modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 10.dp))
                }
                item{
                    DealsComposable()
                }
                 item {
                     DisplayLabel("Shop By Category", modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 10.dp))
                  }
                  item {
                      CategoryScreen()
                  }
                  item {
                      SeasonalCategoryComposable()
                  }

            }
        }
    }

}

@Composable
fun CategoryScreen(categoryViewModel: CategoryViewModel = get()) {
    val categoryList: List<Category> = categoryViewModel.categoryList.value

    LaunchedEffect(Unit) {
        categoryViewModel.fetchCategories()
    }
    CategoryGrid(categoryList)

}

@Composable
fun SeasonalCategoryTitle(title: String) {
    DisplayOutLinedLabel(label = title)
}

@Composable
fun SeasonalCategoryComposable(categoryViewModel: CategoryViewModel = get()) {
    val categoryList: List<Category> = categoryViewModel.categoryList.value

    LaunchedEffect(Unit) {
        categoryViewModel.fetchSeasonalCategories()
    }
    SeasonalCategoryRow(categoryList)

}

@Composable
fun SeasonalCategoryRow(categories: List<Category>) {
    Column(){
        SeasonalCategoryTitle("Seasonal Specials")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(categories.size) { index ->
                SeasonalCategoryItem(category = categories[index])
            }
        }
    }

}


@Composable
fun CategoryGrid(categories: List<Category>) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.height(400.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(categories.size) { index ->
            CategoryItem(category = categories[index])
        }
    }

}

@Composable
fun SeasonalCategoryItem(category: Category) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .height(height = 100.dp)
            .width(width = 100.dp)
            .border(
                BorderStroke(1.dp, Color.LightGray)/*,
                shape = RoundedCornerShape(8.dp)*/
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //  CategoryImage(category.categoryImage)
            //  CategoryImageFromUrl(category.categoryImage)
            CategoryImageFromURLWithPlaceHolder(category.categoryImage)
            CategoryName(category.categoryName)

        }
    }
}

@Composable
fun CategoryItem(category: Category) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .border(
                BorderStroke(1.dp, Color.LightGray)/*,
                shape = RoundedCornerShape(8.dp)*/
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //  CategoryImage(category.categoryImage)
            //  CategoryImageFromUrl(category.categoryImage)
            CategoryImageFromURLWithPlaceHolder(category.categoryImage)
            CategoryName(category.categoryName)

        }
    }

}

@Composable
fun CategoryName(categoryName: String) {
    DisplayLabel(categoryName)

}

@Composable
fun CategoryImageFromURLWithPlaceHolder(imageUrl: String) {
    FetchImageFromURLWithPlaceHolder(imageUrl = imageUrl)
}


/*@Composable
fun Category() {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My Cart") }
            )
        }
    ) {
      Column()
            {
            CategoryScreen()
            SeasonalCategoryTitle("Seasonal Specials")
            SeasonalCategoryComposable()
        }
    }
}*/
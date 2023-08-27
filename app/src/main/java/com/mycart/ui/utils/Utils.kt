package com.mycart.ui.utils

import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.mycart.R

@Composable
fun FetchImageFromURLWithPlaceHolder(imageUrl : String){
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_baseline_shopping_cart_24),
        contentDescription = stringResource(R.string.app_name),
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(50.dp),
        colorFilter = ColorFilter.tint(Color.Blue)
    )
}

@Composable
fun FetchImageFromUrl(imageUrl: String) {
    val painter: Painter = rememberAsyncImagePainter(imageUrl)
    Image(
        painter = painter, contentDescription = null, modifier = Modifier.size(50.dp),
        colorFilter = ColorFilter.tint(Color.Blue)
    )
}

@Composable
fun FetchImageFromDrawable(imageName: String,modifier: Modifier=Modifier) {
    println("ImageName is $imageName")
    val context = LocalContext.current
    val resourceId =
        context.resources.getIdentifier(imageName.trim(), "drawable", context.packageName)
    val image = painterResource(resourceId)
    Image(painter = image, contentDescription = null, modifier = modifier.size(50.dp))
}

@Composable
fun FetchImageWithBorderFromDrawable(imageName: String,modifier: Modifier=Modifier,onClick:()->Unit) {
    val context = LocalContext.current
    val resourceId =
        context.resources.getIdentifier(imageName.trim(), "drawable", context.packageName)
    val image = painterResource(resourceId)
    Box(
        modifier = modifier
            .size(25.dp)
            .border(1.dp, Color.Blue)
            .clickable {
                onClick()
            }// Apply a border with 1dp width and black color
    ) {
        Image(painter = image, contentDescription = null, modifier = modifier.size(50.dp))
    }
}

@Composable
fun DisplayLabel(label:String,modifier: Modifier = Modifier){
    Text(
        text = label,
        modifier = modifier,
        fontWeight = FontWeight.Normal
    )
}

@Composable
fun DisplayOutLinedLabel(label:String){
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colors.onSurface, shape = MaterialTheme.shapes.small)
                .padding(horizontal = 16.dp, vertical = 10.dp)


        )
    }
}

@Composable
fun DisplayBorderedLabel(label:String,modifier: Modifier=Modifier){
    Text(
        text = label,
        modifier = modifier

    )
}

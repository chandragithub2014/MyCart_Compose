package com.mycart.ui.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.mycart.R
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Composable
fun FetchImageFromURLWithPlaceHolder(imageUrl : String,modifier: Modifier = Modifier){
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_baseline_shopping_cart_24),
        contentDescription = stringResource(R.string.app_name),
        contentScale = ContentScale.Crop,
        modifier = modifier.size(50.dp),
        colorFilter = ColorFilter.tint(Color.Blue)
    )
}

@Composable
fun FetchImageFromUrl(imageUrl: String,modifier: Modifier=Modifier,imageSize:Dp = 50.dp) {
    val painter: Painter = rememberAsyncImagePainter(imageUrl)
    Image(
        painter = painter, contentDescription = null, modifier = modifier.size(imageSize),
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
fun DisplayLabel(label:String,modifier: Modifier = Modifier,textColor: Color = Color.Unspecified,textFont:FontWeight = FontWeight.Normal){
    Text(
        text = label,
        modifier = modifier,
        color = textColor,
        fontWeight = textFont
    )
}

@Composable
fun DisplayOutLinedLabel(label:String,modifier: Modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)){
    Box(
        modifier = modifier,
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

fun getStrikethroughAnnotatedString(input: String): AnnotatedString {
    return AnnotatedString.Builder().apply {
        withStyle(
            style = SpanStyle(
                textDecoration = TextDecoration.LineThrough,
                color = Color.Gray
            )
        ) {
            append("(")
            append(input)
            append(")")
        }
    }.toAnnotatedString()
}

fun getCurrentDateTime(): String {
    val current = LocalDateTime.now()

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    return current.format(formatter)

}

@Composable
fun DisplayHeaderLabel(label:String,paddingHorizontal: Dp = 16.dp,paddingVertical:Dp = 10.dp,modifier: Modifier = Modifier.padding(horizontal = paddingHorizontal, vertical = paddingVertical).background(Color.Unspecified),backgroundColor:Color = Color.Unspecified,textColor:Color = MaterialTheme.colors.onSurface ){
    Box(
        modifier = modifier.background(backgroundColor),
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colors.onSurface, shape = MaterialTheme.shapes.small)
                .padding(horizontal = 16.dp, vertical = 10.dp)


        )
    }
}

@Composable
fun DisplayHeaderLabelWithImage(label:String,paddingHorizontal: Dp = 16.dp,paddingVertical:Dp = 10.dp,modifier: Modifier = Modifier.padding(horizontal = paddingHorizontal, vertical = paddingVertical).background(Color.Unspecified),backgroundColor:Color = Color.Unspecified,textColor:Color = MaterialTheme.colors.onSurface,imageIcon:ImageVector,onClick: (Boolean) -> Unit ){
    var isRotated by remember { mutableStateOf(false) }

    val rotationDegrees by animateFloatAsState(
        targetValue = if (isRotated) 180f else 0f,
        animationSpec = spring(), label = ""
    )

    LaunchedEffect(isRotated) {
        // Delay for a short period to allow the animation to finish before executing onClick
        delay(200)
        onClick(isRotated)
    }
    Box(
        modifier = modifier.background(backgroundColor),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically/*,
            modifier = Modifier.clickable { onClick() }*/
        ) {


            Text(
                text = label,
                color = textColor,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)


            )
                    Spacer(modifier = Modifier.weight(1f))

           /* Image(
                imageVector = imageIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        isRotated = !isRotated
                    }
                    .wrapContentSize(Alignment.Center)
                    .rotate(rotationDegrees)
            )*/

            Box(
                modifier = Modifier
                    .size(25.dp)
                    .border(1.dp, Color.Blue)
                    .clickable {
                        isRotated = !isRotated
                        onClick(isRotated)
                    }
            ){
                Image(
                    imageVector = imageIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .wrapContentSize(Alignment.Center)
                        .rotate(rotationDegrees)
                )
            }


        }
    }
}

fun generateKeywords(name: String): List<String> {
    val keywords = mutableListOf<String>()
    for (i in name.indices) {
        for (j in (i+1)..name.length) {
            val substring = name.slice(i until j)
            if (!substring.contains(' ') && !substring.contains('(') && !substring.contains(')')) {
                keywords.add(substring)
            }
        }
    }
    return keywords
}

fun getRandomColor(): Color {
    val randomIsWhite = Random.nextBoolean()
    return if (randomIsWhite) {
        Color.White
    } else {
        // You can choose any shade of grey here
        Color.Gray
    }
}

fun getColorFromHex(hex: String): Color {
    return Color(android.graphics.Color.parseColor(hex))
}

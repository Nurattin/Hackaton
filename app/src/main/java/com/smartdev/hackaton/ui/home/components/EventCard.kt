package com.smartdev.hackaton.ui.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.smartdev.hackaton.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EventCard(
    modifier: Modifier = Modifier,
    src: String?,
    title: String,
    category: String,
    price: String,
    date: String,
    onClick: ()->Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        onClick = onClick,
        elevation = 0.dp,
        border = BorderStroke(Dp.Hairline, Color.Black.copy(0.3f))
    ) {
        Column() {
            BannerImage(
                model = src
            )
            Title(
                modifier = Modifier
                    .padding(end = 12.dp, start = 12.dp, top = 16.dp),
                text = title
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Category(
                    modifier = Modifier.padding(bottom = 20.dp),
                    category = category
                )
//                Date(modifier = Modifier, date = date)
            }

        }
    }
}




@Composable
private fun BannerImage(
    modifier: Modifier = Modifier,
    model: String?
) {
    val width = LocalConfiguration.current.screenWidthDp
    val height = (width * 0.54)
    Box(
        modifier = modifier
            .width(width.dp)
            .height(height.dp)
            .clip(RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.BottomEnd
    ) {
        AsyncImage(
            model = model,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            placeholder = painterResource(R.drawable.place_holder),
            error = painterResource(R.drawable.place_holder)
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 18.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_favorite),
                modifier = modifier,
                contentDescription = null,
                tint = Color.White
            )
            Icon(
                painter = painterResource(R.drawable.ic_share),
                modifier = modifier,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun Category(
    modifier: Modifier = Modifier,
    category: String
) {
    Text(
        modifier = modifier,
        text = category,
        style = MaterialTheme.typography.body1,
        color = LocalContentColor.current.copy(alpha = 0.5f)
    )
}


@Composable
private fun Title(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Medium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

//@Composable
//private fun Date(
//    modifier: Modifier = Modifier,
//    date: String
//) {
//    Row(
//        modifier = modifier,
//        horizontalArrangement = Arrangement.spacedBy(4.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = date,
//            style = MaterialTheme.typography.body1,
//            fontWeight = FontWeight.W300,
//            modifier = Modifier
//        )
//        Icon(
//            modifier = Modifier,
//            painter = painterResource(R.drawable.ic_calendar),
//            contentDescription = null,
//            tint = SecondaryOrange
//        )
//    }
//}

//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//private fun PriceButton(
//    modifier: Modifier = Modifier,
//    price: String
//) {
//
//    Surface(
//        modifier = modifier,
//        onClick = {},
//        shape = MaterialTheme.shapes.small,
//        color = PrimaryCyan,
//    ) {
//        Text(
//            text = price,
//            style = MaterialTheme.typography.body1,
//            fontWeight = FontWeight.Normal,
//            color = Color.White,
//            modifier = modifier
//                .padding(vertical = 6.dp, horizontal = 26.dp),
//            textAlign = TextAlign.Center
//        )
//    }
//}







package com.yelpexplorer.features.business.presentation.businesslist

import android.widget.Toast
import androidx.compose.foundation.Box
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import com.yelpexplorer.features.business.R
import com.yelpexplorer.libraries.core.ui.YelpExplorerTheme
import com.yelpexplorer.libraries.core.utils.StarsProvider
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade
import java.util.Locale


private val businessUiModel = BusinessUiModel(
    id = "id",
    name = "Jun-i",
    photoUrl = "https://s3-media3.fl.yelpcdn.com/bphoto/p_dKe_-P6QhcK7hVRxEF7Q/o.jpg",
    rating = 4.0,
    reviewCount = 342,
    address = "156 Avenue Laurier O, Montréal, QC H2T 2N7",
    price = "$$",
    categories = "sushi"
)

private val businessList = listOf(
    businessUiModel, businessUiModel, businessUiModel, businessUiModel,
    businessUiModel, businessUiModel, businessUiModel, businessUiModel
)

@Composable
fun YelpExplorer(content: @Composable () -> Unit) {
    YelpExplorerTheme(darkTheme = false) { // TODO not sure why the topappbar is darkgrey when darktheme is false
        content()
    }
}

@Composable
fun BusinessListScreen(viewModel: BusinessListViewModel) {
//        viewModel.viewState.observe(this) { viewState ->
//            when (viewState) {
//                is BusinessListViewModel.ViewState.ShowBusinessList -> {
//                    val businessList = viewState.businessList
//                    for (business in businessList.businessList) {
//                        Business(business = business)
//                    }
//                }
//                is BusinessListViewModel.ViewState.ShowLoading -> { /* TODO */ }
//                is BusinessListViewModel.ViewState.ShowError -> { /* TODO */ }
//            }
//        }
//
//
//
//        val viewState: BusinessListViewModel.ViewState by viewModel.viewState.observeAsState(BusinessListViewModel.ViewState.ShowLoading())
//        if (viewState is BusinessListViewModel.ViewState.ShowBusinessList) {
//            viewState.businessList.businessList
//        }
//        when (viewState) {
//            is BusinessListViewModel.ViewState.ShowBusinessList -> {
//                val businessList = viewState.businessList //(viewState as BusinessListViewModel.ViewState.ShowBusinessList).businessList
//                for (business in businessList.businessList) {
//                    Business(business = business)
//                }
//            }
//            is BusinessListViewModel.ViewState.ShowLoading -> { /* TODO */ }
//            is BusinessListViewModel.ViewState.ShowError -> { /* TODO */ }
//        }

    val viewState by viewModel.viewState.observeAsState()
    val businessList = viewState?.businessListUiModel?.businessList.orEmpty()

    Scaffold(
        backgroundColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text(text = "YelpExplorer") },
//                    backgroundColor = MaterialTheme.colors.background,      //com.yelpexplorer.libraries.core.ui.darkRedYelp,
//                    contentColor = Color.White
            )
        },
        bodyContent = {
            if (viewState?.showLoading == true) {
                Text(text = "Loading", color = Color.White)
            } else {
                // TODO lazy column?
                ScrollableColumn {
                    businessList.forEachIndexed { index, businessUiModel ->
                        Business(
                            businessUiModel = businessUiModel,
                            index = index + 1
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun Business(businessUiModel: BusinessUiModel, index: Int) {
    val context = ContextAmbient.current
    Card(
        shape = RoundedCornerShape(2.dp),
        backgroundColor = Color.DarkGray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clickable(
                onClick = {
                    Toast.makeText(context, "${businessUiModel.name} Clicked!", Toast.LENGTH_LONG).show()
                },
                indication = RippleIndication()
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            CoilImageWithCrossfade(
                data = businessUiModel.photoUrl,
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop, // == centerCrop because of Alignment.Center
                modifier = Modifier.size(100.dp)
            )

            Box(modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxWidth()
            ) {
                Text(
                    text = "${index}. ${businessUiModel.name.capitalize(Locale.US)}",
                    color = Color.White,
                    fontSize = 15.sp, // TODO why overriding style ?
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
//                        fontFamily = GenericFontFamily("sans-serif-medium")
                )
                Row {
                    CoilImageWithCrossfade(
                        data = StarsProvider.getDrawableId(businessUiModel.rating),
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(width = 82.dp, height = 14.dp)
                    )
                    Text(
                        text = "${businessUiModel.reviewCount} reviews", // TODO getString with formatter
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Row {
                    Text( // TODO for some reason the space at the end of the first Text is not showing
                        text = if (businessUiModel.price.isNotBlank()) {
                            context.getString(R.string.business_price, businessUiModel.price)
                        } else {
                            ""
                        },
                        color = Color.White
                    )
                    Text(text = businessUiModel.categories, color = Color.White)
                }
                Text(text = businessUiModel.address, color = Color.White)
            }
        }
    }
}

//    @Preview("BusinessActivity preview")
//    @Composable
//    fun DefaultPreview() {
//        YelpExplorer {
//            BusinessListScreen(viewModel = viewModel)
//        }
//    }

@Preview("BusinessActivity preview")
@Composable
fun DefaultPreview() {
    ScrollableColumn {
        businessList.forEachIndexed { index, businessUiModel ->
            Business(
                businessUiModel = businessUiModel,
                index = index + 1
            )
        }
    }
}

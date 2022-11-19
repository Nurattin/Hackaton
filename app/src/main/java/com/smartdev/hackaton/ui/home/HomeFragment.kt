package com.smartdev.hackaton.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.smartdev.hackaton.R
import com.smartdev.hackaton.ui.home.components.EventCard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {
    @OptIn(
        ExperimentalMaterialApi::class,
        ExperimentalLifecycleComposeApi::class
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val viewModel: HomeViewModel by viewModels()

        return ComposeView(requireContext()).apply {
            setContent {

                val uiState = viewModel.tours.collectAsStateWithLifecycle()
                val chips = viewModel.chips.collectAsStateWithLifecycle()
                val verticalScrollState = rememberLazyListState()
                val showTopAppBarShadow = remember { derivedStateOf { verticalScrollState.firstVisibleItemScrollOffset.dp > 20.dp } }
                val animateShadow =
                    animateFloatAsState(targetValue = if (showTopAppBarShadow.value) 50f else 0f)

                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .graphicsLayer { shadowElevation = animateShadow.value }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                            ) {
                                Text(
                                    text = "Интересные Туры",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(vertical = 13.dp)
                                )
                            }

                            LazyRow(
                                modifier = Modifier
                                    .background(Color.White),
                                contentPadding = PaddingValues(start = 16.dp, top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                items(items = chips.value) { item ->
                                    Chip(
                                        onClick = { viewModel.changeCategorySelected(item) },
                                        colors = ChipDefaults.chipColors(
                                            contentColor = if (item.isSelected) Color.White else Color.Black,
                                            backgroundColor = if (item.isSelected) Color(0xFF76A595) else Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(Dp.Hairline, Color.Black.copy(0.1f))
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(
                                                vertical = 6.dp,
                                                horizontal = 12.dp
                                            ),
                                            text = item.name
                                        )
                                    }
                                }
                            }
                        }
                        when (val result = uiState.value) {
                            is TourUiState.Error -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "Error")
                                }
                            }
                            TourUiState.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center

                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            is TourUiState.Success -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                    state = verticalScrollState,
                                ) {
                                    items(items = result.tours?.data ?: emptyList()) { item ->
                                        EventCard(
                                            modifier = Modifier.padding(16.dp, vertical = 20.dp),
                                            title = "Самый вкусный",
                                            category = "Кафе, Море, Велобайки",
                                            price = "1200",
                                            date = "3",
                                            src = item.photos.first(),
                                            onClick = {
                                                findNavController().navigate(
                                                    R.id.action_homeFragment_to_mapFragment,
                                                    args = bundleOf("tourId" to item.id)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                backgroundColor = Color(0xFF76A595)
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                modifier = Modifier.padding(vertical = 4.dp),
                                text = "Создать маршрут"
                            )
                        }
                    }
                }
            }
        }
    }
}
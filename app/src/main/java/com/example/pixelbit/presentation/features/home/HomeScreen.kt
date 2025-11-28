package com.example.pixelbit.presentation.features.home

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pixelbit.R
import com.example.pixelbit.domain.model.Banner
import com.example.pixelbit.domain.model.User
import com.example.pixelbit.presentation.features.category.CategoryItem
import com.example.pixelbit.presentation.theme.Purple40
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {

    val user by viewModel.user.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val banners by viewModel.banners.collectAsStateWithLifecycle()
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { HomeTopBar(user) }, contentWindowInsets = WindowInsets()
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            HomeTabs(
                selectedIndex = selectedTabIndex,
                onTabSelected = { index -> selectedTabIndex = index })

            PullToRefreshBox(
                isRefreshing = isRefreshing, onRefresh = { viewModel.loadData(true) }) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Adaptive(320.dp),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        if (selectedTabIndex == 0) {
                            item(span = { GridItemSpan(maxLineSpan) }) { HomeBanner(banners) }

                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "New Arrivals 🔥",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "See All",
                                        color = Purple40,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            items(products.take(10)) { product ->
                                ProductItem(
                                    product = product,
                                    onFavoriteClick = { viewModel.toggleFavorite(it) })
                            }

                        } else {
                            itemsIndexed(
                                categories,
                                span = { _, _ -> GridItemSpan(maxLineSpan) }) { index, category ->
                                CategoryItem(category = category, index = index)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTabs(selectedIndex: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(100.dp)
                .clickable { onTabSelected(0) }) {
            Text(
                "Home",
                fontWeight = if (selectedIndex == 0) FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp,
                color = if (selectedIndex == 0) Color.DarkGray else Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (selectedIndex == 0) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(2.dp)
                        .background(Purple40)
                )
            } else {
                Spacer(modifier = Modifier.height(2.dp))
            }
        }

        Spacer(modifier = Modifier.width(32.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(100.dp)
                .clickable { onTabSelected(1) }) {
            Text(
                "Category",
                fontWeight = if (selectedIndex == 1) FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp,
                color = if (selectedIndex == 1) Color.DarkGray else Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (selectedIndex == 1) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(2.dp)
                        .background(Purple40)
                )
            } else {
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Composable
fun HomeTopBar(
    user: User?
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    // Todo replace with user image
                    .data("")
                    .crossfade(true)
                    .error(R.drawable.profile_image)
                    .build(),
                contentDescription = "",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(user?.name.orEmpty(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Let's go shopping", color = Color.Gray, fontSize = 12.sp)
            }
        }
        Row {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "Notify",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun HomeBanner(banners: List<Banner>) {
    if (banners.isNotEmpty()) {
        val pagerState = rememberPagerState { banners.size }
        LaunchedEffect(pagerState) {
            while (true) {
                delay(5000L) // Increased delay to 5 seconds
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(durationMillis = 1000) // Slower animation
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(16.dp))
        ) { page ->
            AsyncImage(
                model = banners[page].imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}
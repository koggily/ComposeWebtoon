package com.example.test

import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.test.model.SpecialWebtoonItem
import com.example.test.ui.theme.TestTheme
import com.google.accompanist.pager.*
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Home(viewModel: HomeTabViewModel) {
    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()
    Column {
        HomeTabBar(onTabSelected = {
            coroutineScope.launch {
                pagerState.scrollToPage(it)
            }
        }, pagerState)
        HomePager(pagerState, viewModel)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeTabBar(
    onTabSelected: (index: Int) -> Unit,
    pagerState: PagerState
) {
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            HomeTabIndicator(tabPositions, pagerState)
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 80.dp)
    ) {
        HomeTab(
            title = stringResource(R.string.home_recommend),
            onClick = { onTabSelected(0) },
            pagerState = pagerState,
            thisTabPage = 0
        )
        HomeTab(
            title = stringResource(R.string.home_special),
            onClick = { onTabSelected(1) },
            pagerState = pagerState,
            thisTabPage = 1
        )
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeTab(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    thisTabPage: Int
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .zIndex(2f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = if (pagerState.currentPage == thisTabPage) Color.Black else Color.White
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeTabIndicator(
    tabPositions: List<TabPosition>,
    pagerState: PagerState
) {
    val transition = updateTransition(
        pagerState,
        label = "Tab indicator"
    )
    val indicatorLeft by transition.animateDp(
        transitionSpec = {
            spring(stiffness = Spring.StiffnessMedium)
        },
        label = "Indicator left"
    ) { page ->
        tabPositions[page.currentPage].left
    }
    val indicatorRight by transition.animateDp(
        transitionSpec = {
            spring(stiffness = Spring.StiffnessMedium)
        },
        label = "Indicator right"
    ) { page ->
        tabPositions[page.currentPage].right
    }
    Box(
        Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.BottomStart)
            .offset(x = indicatorLeft)
            .width(indicatorRight - indicatorLeft)
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .zIndex(1f)
    )
}

@OptIn(ExperimentalPagerApi::class, ExperimentalSnapperApi::class)
@Composable
fun HomePager(pagerState: PagerState, viewModel: HomeTabViewModel) {
    HorizontalPager(
        count = 2, state = pagerState
    ) { page ->
        when (page) {
            0 -> RecommendTab()
            else -> SpecialTab(viewModel.specialWebtoonList, viewModel)
        }
    }

}

@Composable
fun RecommendTab() {
    Text(text = "추천탭")
}

@Composable
fun SpecialTab(list: List<SpecialWebtoonItem>, viewModel: HomeTabViewModel) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier
            .background(Color.Black),
        state = listState
    ) {
        items(
            items = list
        ) {
            SpecialItem(it, listState)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
    listState.OnBottomReached() {
        viewModel.loadMoreSpecialWebtoon()
    }
    listState.OnTopReached {
        viewModel.loadMoreSpecialWebtoonTop()
    }
}

@Composable
fun SpecialItem(item: SpecialWebtoonItem, listState: LazyListState) {
    val context = LocalContext.current
    val imgLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    val mPainter = rememberAsyncImagePainter(R.drawable.fight, imgLoader)


    Box(
        modifier = Modifier
            .height(600.dp)
            .clip(RoundedCornerShape(0.dp))
    ) {
        Image(
            painter = mPainter,
            contentDescription = "Webtoon",

            modifier = Modifier
                .zIndex(2.0f)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.Crop
        )
        Image(
            painterResource(id = R.drawable.back),
            contentDescription = "background",
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = with(LocalDensity.current) {
                    (listState.layoutInfo.visibleItemsInfo.firstOrNull()?.offset?.div(5))?.toDp()
                        ?: 0.dp
                }),
            contentScale = ContentScale.Crop
        )
        ChipButtons(
            modifier = Modifier
                .padding(bottom = 16.dp, start = 4.dp)
                .zIndex(3.0f)
                .align(Alignment.BottomCenter),
            data = chipString
        )
    }

}

val chipString = listOf(
    "꿀잼",
    "꿀맛",
    "개굴맨",
    "팡주팡머",
    "팡팡팡",
    "팡팡팡팡",
    "꿀잼",
    "꿀맛",
    "개굴맨",
    "팡주팡머",
    "팡팡팡",
    "팡팡팡팡",
    "꿀잼",
    "꿀맛",
    "개굴맨",
    "팡주팡머",
    "팡팡팡",
    "팡팡팡팡",
    "꿀잼",
    "꿀맛",
    "개굴맨",
    "팡주팡머",
    "팡팡팡",
    "팡팡팡팡"
)

@Composable
fun ChipButtons(modifier: Modifier, data: List<String>) {
    val infiniteTransition = rememberInfiniteTransition()
    val scrollPosition by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = data.size,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val chipState = rememberLazyListState(scrollPosition)
    val c by remember(scrollPosition) {
        derivedStateOf {
            when (scrollPosition) {
                0 -> {
                    data.size - 1
                }
                data.size - 1 -> {
                    0
                }
                else -> {
                    80
                }
            }
        }
    }
    LaunchedEffect(c) {
        println(chipState.layoutInfo.viewportEndOffset)
        if (c != 80) chipState.animateScrollToItem(c)
    }
    LazyRow(state = chipState, modifier = modifier) {
        items(data.size) {
            Text(
                text = data[it], modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                color = Color.White
            )
            Spacer(modifier = Modifier.width(2.dp))
        }
    }

}

@Composable
fun LazyListState.OnBottomReached(
    loadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem!!.index == layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect {
                if (it) loadMore()
            }
    }
}

@Composable
fun LazyListState.OnTopReached(
    loadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.firstOrNull()

            lastVisibleItem!!.index == 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect {
                if (it) loadMore()
            }
    }
}

@Composable
fun SpecialBackground() {

}


@Preview
@Composable
fun prevHome() {
    val homeTabViewModel = HomeTabViewModel()
    TestTheme {
        Home(homeTabViewModel)
    }
}

@Preview
@Composable
fun SpecialBackgroundPreview() {
    TestTheme {
        SpecialBackground()
    }
}

package com.coffeebean.ui.feature.home.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.coffeebean.ui.theme.coffeebeanPurple
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

/**
 * Configuration for the PromoCarousel component
 */
data class PromoCarouselConfig(
    val autoScrollEnabled: Boolean = true,
    val autoScrollDelayMs: Long = 3000,
    val animationDurationMs: Int = 600,
    val carouselHeight: Dp = 350.dp,
    val cardElevation: Dp = 6.dp,
    val cornerRadius: Dp = 16.dp,
    val horizontalPadding: Dp = 16.dp,
    val enablePageTransformation: Boolean = true
)

/**
 * Modern promotional carousel with auto-scroll, page indicators, and accessibility support.
 *
 * @param promoImages List of drawable resource IDs to display
 * @param modifier Modifier to be applied to the carousel
 * @param config Configuration for carousel behavior and appearance
 * @param onPromoClick Optional callback when a promo item is clicked
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PromoCarousel(
    promoImages: List<Int>,
    modifier: Modifier = Modifier,
    config: PromoCarouselConfig = PromoCarouselConfig(),
    onPromoClick: ((Int) -> Unit)? = null
) {
    if (promoImages.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { promoImages.size })
    var isUserInteracting by remember { mutableStateOf(false) }

    // Auto-scroll effect with proper lifecycle management
    LaunchedEffect(config.autoScrollEnabled, isUserInteracting) {
        if (config.autoScrollEnabled && !isUserInteracting) {
            while (true) {
                delay(config.autoScrollDelayMs)
                if (!pagerState.isScrollInProgress) {
                    val nextPage = (pagerState.currentPage + 1) % promoImages.size
                    pagerState.animateScrollToPage(
                        page = nextPage,
                        animationSpec = tween(config.animationDurationMs)
                    )
                }
            }
        }
    }

    // Auto-scroll effect with proper lifecycle management
    LaunchedEffect(pagerState) {
        if (config.autoScrollEnabled) {
            while (true) {
                delay(config.autoScrollDelayMs)
                val nextPage = (pagerState.currentPage + 1) % promoImages.size
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(config.animationDurationMs)
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Promotional carousel with ${promoImages.size} items" }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(config.carouselHeight),
            contentPadding = PaddingValues(horizontal = config.horizontalPadding),
            pageSpacing = 8.dp
        ) { page ->
            PromoCard(
                imageResId = promoImages[page],
                pageIndex = page,
                pagerState = pagerState,
                config = config,
                onClick = onPromoClick
            )
        }

        // Page indicators
        PageIndicators(
            pageCount = promoImages.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PromoCard(
    imageResId: Int,
    pageIndex: Int,
    pagerState: PagerState,
    config: PromoCarouselConfig,
    onClick: ((Int) -> Unit)?
) {
    Card(
        shape = RoundedCornerShape(config.cornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = config.cardElevation),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF532D6D)),
        onClick = { onClick?.invoke(pageIndex) },
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (config.enablePageTransformation) {
                    Modifier.carouselTransition(pagerState, pageIndex)
                } else {
                    Modifier
                }
            )
            .semantics { contentDescription = "Promotional image ${pageIndex + 1}" }
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null, // Decorative, description on Card
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Adds a smooth scale and alpha transition effect to carousel pages
 */
@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.carouselTransition(
    pagerState: PagerState,
    page: Int
): Modifier = graphicsLayer {
    val pageOffset = (
            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            ).absoluteValue

    // Scale pages: current page = 1.0, others slightly smaller
    val scale = lerp(
        start = 0.85f,
        stop = 1f,
        fraction = 1f - pageOffset.coerceIn(0f, 1f)
    )

    scaleX = scale
    scaleY = scale

    // Fade out pages slightly
    alpha = lerp(
        start = 0.5f,
        stop = 1f,
        fraction = 1f - pageOffset.coerceIn(0f, 1f)
    )
}

@Composable
private fun PageIndicators(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = coffeebeanPurple,
    inactiveColor: Color = Color.LightGray,
    indicatorSize: Dp = 8.dp,
    spacing: Dp = 4.dp
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.semantics {
            contentDescription = "Page ${currentPage + 1} of $pageCount"
        }
    ) {
        repeat(pageCount) { index ->
            val color = if (currentPage == index) activeColor else inactiveColor
            Box(
                modifier = Modifier
                    .padding(spacing)
                    .size(indicatorSize)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
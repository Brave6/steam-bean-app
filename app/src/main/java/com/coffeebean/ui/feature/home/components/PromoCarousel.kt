package com.coffeebean.ui.feature.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.coffeebean.domain.model.Promo
import com.coffeebean.ui.theme.coffeebeanPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
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
 * Modern promotional carousel with auto-scroll, page indicators, shimmer loading, and analytics support.
 *
 * Best practices implemented:
 * - Shimmer loading effect for images
 * - Proper error handling for image loading
 * - Accessibility support with semantic descriptions
 * - Performance optimization with distinctUntilChanged
 * - Memory-efficient image loading with Coil
 *
 * @param promos List of [Promo] objects to display.
 * @param modifier Modifier to be applied to the carousel.
 * @param config Configuration for carousel behavior and appearance.
 * @param onPromoClick Callback triggered when a promo item is clicked.
 * @param onPromoViewed Callback triggered when a promo item becomes visible.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PromoCarousel(
    promos: List<Promo>,
    modifier: Modifier = Modifier,
    config: PromoCarouselConfig = PromoCarouselConfig(),
    onPromoClick: (Promo) -> Unit,
    onPromoViewed: (Promo) -> Unit
) {
    if (promos.isEmpty()) {
        // Empty state - you can customize this
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(config.carouselHeight),
            contentAlignment = Alignment.Center
        ) {
            // Optional: Add empty state UI here
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { promos.size })
    val isUserInteracting by pagerState.interactionSource.collectIsDraggedAsState()

    // Auto-scroll effect - pauses when user is interacting
    LaunchedEffect(config.autoScrollEnabled, isUserInteracting) {
        if (config.autoScrollEnabled && !isUserInteracting) {
            while (true) {
                delay(config.autoScrollDelayMs)
                if (!pagerState.isScrollInProgress) {
                    val nextPage = (pagerState.currentPage + 1) % promos.size
                    pagerState.animateScrollToPage(
                        page = nextPage,
                        animationSpec = tween(config.animationDurationMs)
                    )
                }
            }
        }
    }

    // Analytics: Log promo view when the page changes
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                promos.getOrNull(page)?.let { onPromoViewed(it) }
            }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Promotional carousel with ${promos.size} items" }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(config.carouselHeight),
            contentPadding = PaddingValues(horizontal = config.horizontalPadding),
            pageSpacing = 8.dp
        ) { page ->
            val promo = promos[page]
            PromoCard(
                promo = promo,
                pageIndex = page,
                pagerState = pagerState,
                config = config,
                onClick = { onPromoClick(promo) }
            )
        }

        // Page indicators
        PageIndicators(
            pageCount = promos.size,
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
    promo: Promo,
    pageIndex: Int,
    pagerState: PagerState,
    config: PromoCarouselConfig,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(config.cornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = config.cardElevation),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8E8)),
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
            .then(
                if (config.enablePageTransformation) {
                    Modifier.carouselTransition(pagerState, pageIndex)
                } else {
                    Modifier
                }
            )
            .semantics {
                contentDescription = "Promotion: ${promo.title}. ${promo.description}"
            }
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(promo.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = {
                ShimmerEffect(modifier = Modifier.fillMaxSize())
            },
            error = {
                // Error state - you can customize this
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    // Optional: Add error icon or text here
                }
            }
        )
    }
}

/**
 * Shimmer loading effect for image placeholders
 */
@Composable
private fun ShimmerEffect(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        Color(0xFFE0E0E0),
        Color(0xFFF5F5F5),
        Color(0xFFE0E0E0)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 1000f, translateAnim - 1000f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier.background(brush)
    )
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

/**
 * Page indicators for the carousel
 */
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
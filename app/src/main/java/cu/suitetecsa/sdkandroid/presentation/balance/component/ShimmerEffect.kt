package cu.suitetecsa.sdkandroid.presentation.balance.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun BalanceSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ShimmerCircle(size = 48.dp)
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerBox(width = 120.dp, height = 16.dp)
        Spacer(modifier = Modifier.height(16.dp))
        ShimmerBox(width = 180.dp, height = 40.dp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ShimmerBox(width = 100.dp, height = 20.dp)
            ShimmerBox(width = 100.dp, height = 20.dp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        PlansSkeleton()
        Spacer(modifier = Modifier.height(16.dp))
        BonusesSkeleton()
    }
}

@Composable
fun PlansSkeleton() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(3) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ShimmerBox(width = 40.dp, height = 12.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    ShimmerBox(width = 60.dp, height = 16.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    ShimmerBox(width = 50.dp, height = 10.dp)
                }
            }
        }
    }
}

@Composable
fun BonusesSkeleton() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(2) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ShimmerBox(width = 40.dp, height = 12.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    ShimmerBox(width = 60.dp, height = 16.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    ShimmerBox(width = 50.dp, height = 10.dp)
                }
            }
        }
    }
}

@Composable
fun ShimmerBox(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
) {
    val alpha by shimmerAlpha()
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .alpha(alpha)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
    )
}

@Composable
fun ShimmerCircle(size: androidx.compose.ui.unit.Dp) {
    val alpha by shimmerAlpha()
    Box(
        modifier = Modifier
            .size(size)
            .alpha(alpha)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
    )
}

@Composable
private fun shimmerAlpha() = rememberInfiniteTransition(label = "shimmer").let { transition ->
    transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
}

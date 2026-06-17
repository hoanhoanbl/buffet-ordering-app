package com.example.appgoimon.ui.screen.user

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.appgoimon.data.remote.AuthUserDto
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.ui.util.generateQrBitmap
import com.example.appgoimon.viewmodel.UserOrderUiState
import kotlinx.coroutines.delay

private val LiveGreen = Color(0xFF1B7A4F)
private val LiveGreenSoft = Color(0xFFEAF7EE)

@Composable
fun WaitingPaymentScreen(
    user: AuthUserDto,
    uiState: UserOrderUiState,
    onRefresh: () -> Unit,
    onSimulatePayment: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val session = uiState.session
    val sessionId = session?.id ?: uiState.createSessionResult?.session_id

    // Auto-poll the SERVER for payment status every ~5s. Keyed on the session id so the loop is
    // recreated only when the session changes and is cancelled automatically when this screen leaves
    // the composition (the customer never advances on a local result — only on server-confirmed paid).
    LaunchedEffect(sessionId) {
        if (sessionId != null) {
            while (true) {
                delay(5000L)
                onRefresh()
            }
        }
    }

    val amount = formatUserCurrency(
        session?.total_amount
            ?: uiState.createSessionResult?.total_amount
            ?: uiState.totalPreview.toString()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF7E8), Color(0xFFFFE2AA))
                )
            )
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Thanh toán QR",
                    style = MaterialTheme.typography.headlineSmall,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
                Text(user.full_name ?: user.username, color = MutedBrown)
            }
            TextButton(onClick = onLogout) {
                Text("Đăng xuất", color = OrangeAccent, fontWeight = FontWeight.SemiBold)
            }
        }

        // Primary payment card: table + live status, the amount as the hero, then the framed QR.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = CircleShape, color = Color(0xFFFFF0D6)) {
                        Text(
                            text = uiState.table?.table_name ?: "Bàn ${uiState.tableCode}",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            color = AmberPrimaryDark,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    LiveWaitingPill()
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Số tiền cần thanh toán",
                        color = MutedBrown,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = amount,
                        color = OrangeAccent,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                // Real, scannable VietQR. The backend builds the EMVCo payload offline and we render
                // it here with ZXing. Cached on the payload so it isn't regenerated each recomposition.
                val qrPayload = uiState.vietqrPayload
                val qrBitmap = remember(qrPayload) { generateQrBitmap(qrPayload) }
                FramedQr {
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap,
                            contentDescription = "Mã VietQR chuyển khoản",
                            modifier = Modifier.size(208.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier.size(208.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = OrangeAccent, strokeWidth = 2.dp)
                        }
                    }
                }

                Text(
                    text = "Mở app ngân hàng và quét mã VietQR để chuyển khoản",
                    color = MutedBrown,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(color = Color(0xFFF0E6D6))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    uiState.bankAccountName?.takeIf { it.isNotBlank() }?.let {
                        DetailRow(label = "Chủ tài khoản", value = it)
                    }
                    uiState.bankAccountNo?.takeIf { it.isNotBlank() }?.let {
                        DetailRow(label = "Số tài khoản", value = it, copyable = true)
                    }
                    uiState.bankNameOrBin?.takeIf { it.isNotBlank() }?.let {
                        DetailRow(label = "Ngân hàng (BIN)", value = it)
                    }
                    DetailRow(
                        label = "Nội dung chuyển khoản",
                        value = uiState.paymentMemo,
                        copyable = true,
                        emphasize = true
                    )
                }
            }
        }

        if (uiState.errorMessage.isNotEmpty()) {
            Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
        }
        if (uiState.successMessage.isNotEmpty()) {
            Text(uiState.successMessage, color = AmberPrimaryDark)
        }

        OutlinedButton(
            onClick = onRefresh,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberPrimaryDark)
        ) {
            Text("Kiểm tra lại", fontWeight = FontWeight.SemiBold)
        }

        // DEV/DEMO control — stands in for the external gateway calling back. Boxed and de-emphasized
        // so it is obviously not part of the real customer flow.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFFFF3E6))
                .border(
                    width = 1.dp,
                    color = OrangeAccent.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Khu vực thử nghiệm (demo)",
                color = MutedBrown,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(
                onClick = onSimulatePayment,
                enabled = !uiState.isSimulatingPayment,
                colors = ButtonDefaults.textButtonColors(contentColor = OrangeAccent)
            ) {
                if (uiState.isSimulatingPayment) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = OrangeAccent,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Giả lập: khách đã thanh toán")
                }
            }
            Text(
                text = "Chỉ dùng để demo — thay bằng cổng thật khi deploy.",
                color = MutedBrown,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text("Quay lại", color = MutedBrown)
        }
    }
}

/** Live "waiting for payment" pill with a softly pulsing green dot, signalling the auto-poll loop. */
@Composable
private fun LiveWaitingPill() {
    val transition = rememberInfiniteTransition(label = "waiting")
    val dotAlpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "dotAlpha"
    )
    Surface(shape = CircleShape, color = LiveGreenSoft) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(dotAlpha)
                    .clip(CircleShape)
                    .background(LiveGreen)
            )
            Text(
                text = "Đang chờ",
                color = LiveGreen,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/** Wraps the QR in a white tile with four orange corner brackets — the familiar "scan here" frame. */
@Composable
private fun FramedQr(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.size(248.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sw = 5.dp.toPx()
            val len = 30.dp.toPx()
            val m = sw / 2f
            val w = size.width
            val h = size.height
            val c = OrangeAccent
            // Top-left
            drawLine(c, Offset(m, m), Offset(m + len, m), sw, StrokeCap.Round)
            drawLine(c, Offset(m, m), Offset(m, m + len), sw, StrokeCap.Round)
            // Top-right
            drawLine(c, Offset(w - m, m), Offset(w - m - len, m), sw, StrokeCap.Round)
            drawLine(c, Offset(w - m, m), Offset(w - m, m + len), sw, StrokeCap.Round)
            // Bottom-left
            drawLine(c, Offset(m, h - m), Offset(m + len, h - m), sw, StrokeCap.Round)
            drawLine(c, Offset(m, h - m), Offset(m, h - m - len), sw, StrokeCap.Round)
            // Bottom-right
            drawLine(c, Offset(w - m, h - m), Offset(w - m - len, h - m), sw, StrokeCap.Round)
            drawLine(c, Offset(w - m, h - m), Offset(w - m, h - m - len), sw, StrokeCap.Round)
        }
        Box(
            modifier = Modifier
                .size(216.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

/** Label/value row for transfer details, with an optional one-tap "copy to clipboard" affordance. */
@Composable
private fun DetailRow(
    label: String,
    value: String,
    copyable: Boolean = false,
    emphasize: Boolean = false
) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(1500L)
            copied = false
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = MutedBrown,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = value,
                color = if (emphasize) AmberPrimaryDark else InkBrown,
                fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (copyable) {
            Spacer(Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (copied) LiveGreenSoft else Color(0xFFFFF0D6),
                modifier = Modifier.clickable {
                    clipboard.setText(AnnotatedString(value))
                    copied = true
                }
            ) {
                Text(
                    text = if (copied) "Đã chép ✓" else "Sao chép",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    color = if (copied) LiveGreen else OrangeAccent,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

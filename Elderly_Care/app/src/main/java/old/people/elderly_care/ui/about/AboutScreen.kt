// File: app/src/main/java/old/people/elderly_care/ui/about/AboutScreen.kt
package old.people.elderly_care.ui.about

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition(label = "heartbeat")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartScale"
    )

    // ONE SINGLE SOLID COLOR FOR ALL THREE FEATURE CARDS
    val featureSolidColor = Color(0xFF6A5ACD)  // SlateBlue — rich, stable, consistent

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "About Elderly Care",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF667eea)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2)
                        )
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                // Pulsing Heart
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(heartScale)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .clip(CircleShape)
                        .border(3.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(70.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    "Elderly Care",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Empowering families to care with love",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(32.dp))
                // === RICH APP DESCRIPTION ===
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF667eea), fontSize = 20.sp)) {
                                    append("What is Elderly Care?\n\n")
                                }
                                withStyle(SpanStyle(color = Color.Black, fontSize = 16.sp)) {
                                    append("A compassionate mobile app designed to help ")
                                }
                                withStyle(SpanStyle(color = Color(0xFF667eea), fontWeight = FontWeight.Bold)) {
                                    append("families and caregivers")
                                }
                                withStyle(SpanStyle(color = Color.Black)) {
                                    append(" manage daily routines for ")
                                }
                                withStyle(SpanStyle(color = Color(0xFF764ba2), fontWeight = FontWeight.Bold)) {
                                    append("elderly loved ones aged 60+")
                                }
                                withStyle(SpanStyle(color = Color.Black)) {
                                    append(".\n\n")
                                    append("From medication reminders to meal planning, we ensure no detail is missed.")
                                }
                            },
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(20.dp))
                        // Age Group & Benefits
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            InfoChip(
                                icon = Icons.Default.Person,
                                label = "60+ Years",
                                color = Color(0xFF667eea)
                            )
                            InfoChip(
                                icon = Icons.Default.FamilyRestroom,
                                label = "Families",
                                color = Color(0xFF764ba2)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                // === ADVANTAGES ===
                Text(
                    "Why Choose Elderly Care?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        AdvantageItem(
                            icon = Icons.Default.CheckCircle,
                            title = "Peace of Mind",
                            desc = "Know your loved one's routine is on track — even when you're away."
                        )
                        Spacer(Modifier.height(16.dp))
                        AdvantageItem(
                            icon = Icons.Default.NotificationsActive,
                            title = "Smart Reminders",
                            desc = "Never miss medication, appointments, or meals."
                        )
                        Spacer(Modifier.height(16.dp))
                        AdvantageItem(
                            icon = Icons.Default.OfflineBolt,
                            title = "Works Offline",
                            desc = "Full access to schedules without internet."
                        )
                        Spacer(Modifier.height(16.dp))
                        AdvantageItem(
                            icon = Icons.Default.Shield,
                            title = "Privacy First",
                            desc = "Data stored securely on your device — no cloud, no sharing."
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                // Feature Cards — NOW ALL SAME SOLID COLOR
                FeatureCard(
                    icon = Icons.Default.HealthAndSafety,
                    title = "Medication & Health Tracking",
                    description = "Log pills, blood pressure, and doctor visits",
                    solidColor = featureSolidColor
                )
                FeatureCard(
                    icon = Icons.Default.Restaurant,
                    title = "Meal & Hydration Planner",
                    description = "Ensure balanced nutrition and water intake",
                    solidColor = featureSolidColor
                )
                FeatureCard(
                    icon = Icons.Default.EmojiPeople,
                    title = "Activity & Mood Tracker",
                    description = "Log walks, hobbies, and emotional well-being",
                    solidColor = featureSolidColor
                )
                Spacer(Modifier.height(24.dp))
                // Quote
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.FormatQuote,
                            null,
                            tint = Color(0xFF667eea),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Caring for our elders is perhaps the greatest responsibility we have.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "— John Hoeven",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667eea)
                        )
                    }
                }
                Spacer(Modifier.height(32.dp))
                Text(
                    "Version 1.0 • Built with Jetpack Compose",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
                Text(
                    "© 2025 Elderly Care Brisbane • Made with ❤️ by Akram",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
               
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun InfoChip(icon: ImageVector, label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            label,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun AdvantageItem(icon: ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF667eea).copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                null,
                tint = Color(0xFF667eea),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                desc,
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )
        }
    }
}

// UPDATED — now uses solid color instead of gradient list
@Composable
fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    solidColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(solidColor)           // ← SOLID COLOR ONLY
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

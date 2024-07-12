package com.example.forcedsocial.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PostCard(
    userImageUri: Uri? = null,
    userName: String,
    postText: String,
    postImageUri: Uri? = null,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // User info row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = userImageUri,
                    contentDescription = "User profile picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = rememberVectorPainter(image = Icons.Default.Person),
                    placeholder = rememberVectorPainter(image = Icons.Default.Person)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            val annotatedText = buildAnnotatedString {
                append(postText)
                val regex = "(https?://[\\w-]+(\\.[\\w-]+)+(/[#?]?.*)?)".toRegex()
                val matches = regex.findAll(postText)

                matches.forEach { match ->
                    addStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        ),
                        start = match.range.first,
                        end = match.range.last + 1
                    )
                    addStringAnnotation(
                        tag = "URL",
                        annotation = match.value,
                        start = match.range.first,
                        end = match.range.last + 1
                    )
                }
            }

            ClickableText(
                text = annotatedText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            ) { offset ->
                annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                        context.startActivity(intent)
                    }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post image
            if (postImageUri != null) {
                AsyncImage(
                    model = postImageUri,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Preview
@Composable
fun PostCardPreview() {
    PostCard(
        userImageUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/forced-social.appspot.com/o/images%2F7886b2c1-a278-4f47-9f90-089c6100f724.jpg?alt=media&token=6a291afe-0dcc-4a27-bcf2-f201af3ff39d"),
        userName = "John Doe",
        postText = "Just had an amazing day at the beach! The sunset was breathtaking. #BeachDay #Sunset",
        onClick = {}
    )
}
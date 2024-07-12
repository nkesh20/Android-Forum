package com.example.forcedsocial.components

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun CommentInput(
    userImageUrl: Uri? = null,
    userName: String,
    onCommentSubmit: (String, Uri?) -> Unit,
) {
    var commentText by remember { mutableStateOf("") }
    var commentImage by remember { mutableStateOf<Uri?>(null) }
    var isCommenting by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = userImageUrl,
                    contentDescription = "User profile picture",
                    error = rememberVectorPainter(Icons.Default.Person),
                    placeholder = rememberVectorPainter(Icons.Default.Person),
                    modifier = Modifier
                        .size(50.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = commentText,
                onValueChange = { commentText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Add a comment...") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank() && !isCommenting) {
                                isCommenting = true
                                onCommentSubmit(commentText, commentImage)
                                commentText = ""
                                commentImage = null
                                isCommenting = false
                            }
                        },
                        enabled = commentText.isNotBlank() && !isCommenting
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send comment",
                            tint = if (commentText.isNotBlank() && !isCommenting)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                }
            )

            RegularImageUpload(onUpload = {
                commentImage = it
            })
        }
    }
}

@Preview
@Composable
fun CommentInputPreview() {
    CommentInput(userName = "Nika", onCommentSubmit = { _: String, _: Uri? -> })
}
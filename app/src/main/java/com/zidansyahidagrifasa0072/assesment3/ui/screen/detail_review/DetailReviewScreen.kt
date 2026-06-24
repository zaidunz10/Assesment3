package com.zidansyahidagrifasa0072.assesment3.ui.screen.detail_review

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.zidansyahidagrifasa0072.assesment3.data.model.Review

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailReviewScreen(
    reviewId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEditReview: (String) -> Unit,
    viewModel: DetailReviewViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    var review by remember { mutableStateOf<Review?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    // Fetch data review secara langsung berdasarkan ID dari Firestore
    LaunchedEffect(reviewId) {
        FirebaseFirestore.getInstance().collection("reviews").document(reviewId)
            .get()
            .addOnSuccessListener { snapshot ->
                review = snapshot.toObject(Review::class.java)
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }
    if (showDeleteDialog) {

        AlertDialog(

            onDismissRequest = {
                showDeleteDialog = false
            },

            title = {
                Text("Hapus Review")
            },

            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus review ini?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        review?.let {

                            viewModel.deleteReview(it.id)

                        }

                        showDeleteDialog = false

                        onNavigateBack()
                    }
                ) {
                    Text("Hapus")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Wisata", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {

                    review?.let {

                        if (viewModel.isReviewOwner(it.userId)) {

                            IconButton(
                                onClick = {
                                    onNavigateToEditReview(it.id)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Review",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            IconButton(
                                onClick = {
                                    showDeleteDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus Review",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (review == null) {
                Text(text = "Data review tidak ditemukan.", modifier = Modifier.align(Alignment.Center))
            } else {
                val data = review!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Gambar Utama Tempat Wisata
                    if (data.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = data.imageUrl,
                            contentDescription = data.placeName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Nama Tempat Wisata
                            Text(
                                text = data.placeName,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )

                            // Badge Rating Bintang
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = data.rating.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Info Pembuat / Reviewer
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            AsyncImage(
                                model = data.userProfileUrl,
                                contentDescription = data.userName,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = data.userName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(text = "Reviewer", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Ulasan Lengkap:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Isi Deskripsi / Ulasan Utama
                        Text(
                            text = data.description,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
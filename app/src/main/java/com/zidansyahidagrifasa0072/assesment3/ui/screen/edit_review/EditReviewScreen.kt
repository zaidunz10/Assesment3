package com.zidansyahidagrifasa0072.assesment3.ui.screen.edit_review

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.zidansyahidagrifasa0072.assesment3.data.model.Review
import com.zidansyahidagrifasa0072.assesment3.data.network.AppNetworkState
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReviewScreen(
    reviewId: String,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: EditReviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var placeName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(3f) }
    var oldImageUrl by remember { mutableStateOf("") }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoadingData by remember { mutableStateOf(true) }

    val editState by viewModel.editState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> newImageUri = uri }

    // Ambil data awal dari Firestore
    LaunchedEffect(reviewId) {
        FirebaseFirestore.getInstance().collection("reviews").document(reviewId).get()
            .addOnSuccessListener { snapshot ->
                val review = snapshot.toObject(Review::class.java)
                review?.let {
                    placeName = it.placeName
                    description = it.description
                    rating = it.rating
                    oldImageUrl = it.imageUrl
                }
                isLoadingData = false
            }
            .addOnFailureListener { isLoadingData = false }
    }

    // Handler ketika berhasil/gagal Edit
    LaunchedEffect(editState) {
        if (editState is AppNetworkState.Success) {
            onNavigateToHome() // Ini yang melakukan redirect
            Toast.makeText(context, "Berhasil!", Toast.LENGTH_SHORT).show()
        }
    }

// Handler ketika berhasil/gagal Hapus
    LaunchedEffect(deleteState) {
        val currentState = deleteState
        if (currentState is AppNetworkState.Success) {
            Toast.makeText(context, "Review berhasil dihapus", Toast.LENGTH_SHORT).show()
            delay(3000)
            onNavigateToHome()
            viewModel.resetState() // Reset ditaruh PALING AKHIR
        } else if (currentState is AppNetworkState.Error) {
            Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Review", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    // Tombol hapus dinonaktifkan jika sedang loading
                    IconButton(
                        onClick = { viewModel.deleteReview(reviewId) },
                        enabled = deleteState !is AppNetworkState.Loading && editState !is AppNetworkState.Loading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus Review",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // 1. KONDISI AWAL: Ambil data dari Firestore
            if (isLoadingData) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // 2. KONDISI SUKSES EDIT
            else if (editState is AppNetworkState.Success) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Berhasil diperbarui! Mengalihkan...",
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 3. KONDISI SUKSES HAPUS
            else if (deleteState is AppNetworkState.Success) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Berhasil dihapus! Mengalihkan...", fontWeight = FontWeight.Medium)
                }
            }

            // 4. TAMPILAN FORM (Hanya muncul jika TIDAK sedang sukses)
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {
                    OutlinedTextField(
                        value = placeName,
                        onValueChange = { placeName = it },
                        label = { Text("Nama Tempat Wisata") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Rating Wisata:", fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Slider(
                            value = rating,
                            onValueChange = { rating = (it * 10).roundToInt() / 10f },
                            valueRange = 1f..5f,
                            steps = 3,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = rating.toString(), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Ulasan / Review") },
                        modifier = Modifier.fillMaxWidth().height(150.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Foto Dokumentasi:", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    val imageToShow = if (newImageUri != null) newImageUri else oldImageUrl
                    if (imageToShow != "") {
                        AsyncImage(
                            model = imageToShow,
                            contentDescription = "Foto Lokasi",
                            modifier = Modifier.fillMaxWidth().height(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ubah Foto")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Tombol Simpan Perubahan
                    Button(
                        onClick = {
                            viewModel.updateReview(
                                reviewId,
                                placeName,
                                description,
                                rating,
                                newImageUri,
                                oldImageUrl
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        enabled = editState !is AppNetworkState.Loading && deleteState !is AppNetworkState.Loading
                    ) {
                        if (editState is AppNetworkState.Loading || deleteState is AppNetworkState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Simpan Perubahan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
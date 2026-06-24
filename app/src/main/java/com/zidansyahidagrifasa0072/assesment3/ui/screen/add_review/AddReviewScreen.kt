package com.zidansyahidagrifasa0072.assesment3.ui.screen.add_review

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
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
import com.zidansyahidagrifasa0072.assesment3.data.network.AppNetworkState
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewScreen(
    onNavigateBack: () -> Unit,
onNavigateToHome: () -> Unit,
    viewModel: AddReviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var placeName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(3f) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val addReviewState by viewModel.addReviewState.collectAsState()
    val apiPlaceState by viewModel.apiPlaceState.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }


    LaunchedEffect(addReviewState) {
        when (val state = addReviewState) {

            is AppNetworkState.Success -> {

                Toast.makeText(
                    context,
                    "Review berhasil diposting",
                    Toast.LENGTH_SHORT
                ).show()

                delay(3000)

                onNavigateToHome()

                viewModel.resetState()
            }

            is AppNetworkState.Error -> {

                Toast.makeText(
                    context,
                    state.message,
                    Toast.LENGTH_LONG
                ).show()

                viewModel.resetState()
            }

            else -> {}
        }
    }

    // Mengisi deskripsi otomatis jika data dari OpenTripMap API berhasil ditarik
    LaunchedEffect(apiPlaceState) {
        if (apiPlaceState is AppNetworkState.Success) {
            val apiData = (apiPlaceState as AppNetworkState.Success).data
            val wikiText = apiData.wikipediaExtracts?.text
            if (!wikiText.isNullOrBlank() && description.isBlank()) {
                description = wikiText
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tulis Review", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onPrimary)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Input Nama Tempat Wisata
                OutlinedTextField(
                    value = placeName,
                    onValueChange = { placeName = it },
                    label = { Text("Nama Tempat Wisata") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tombol Verifikasi Lokasi via API OpenTripMap
                OutlinedButton(
                    onClick = { viewModel.searchPlaceInfo(placeName) },
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (apiPlaceState is AppNetworkState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Cek Info & Deskripsi API")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input Rating Berbasis Slider Bintang
                Text(text = "Rating Wisata:", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
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
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = rating.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input Deskripsi Review Wisata
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Tulis Ulasan / Review Kamu") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Preview Image & Tombol Ambil Gambar dari Galeri
                Text(text = "Foto Dokumentasi Tempat:", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Preview Gambar",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (imageUri == null) "Pilih Gambar dari Galeri" else "Ubah Gambar")
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Tombol Submit Form Kirim Data ke Firebase
                Button(
                    onClick = { viewModel.addReview(placeName, description, rating, imageUri) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = addReviewState !is AppNetworkState.Loading
                ) {
                    if (addReviewState is AppNetworkState.Loading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Post Review Wisata", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
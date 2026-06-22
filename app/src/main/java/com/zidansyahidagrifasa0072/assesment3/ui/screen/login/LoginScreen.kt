package com.zidansyahidagrifasa0072.assesment3.ui.screen.login

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.zidansyahidagrifasa0072.assesment3.data.network.AppNetworkState
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (loginState) {
            is AppNetworkState.Success -> {
                Toast.makeText(context, (loginState as AppNetworkState.Success<String>).data, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                onLoginSuccess()
            }
            is AppNetworkState.Error -> {
                Toast.makeText(context, (loginState as AppNetworkState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Selamat Datang di WisataKu",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Komunitas Review & Rating Tempat Wisata Terpercaya",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (loginState is AppNetworkState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            } else {
                Button(
                    onClick = {
                        // Ganti dengan WEB CLIENT ID dari Firebase Console -> Authentication -> Sign-In Method -> Google
                        val webClientId = "YOUR_FIREBASE_WEB_CLIENT_ID.apps.googleusercontent.com"

                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(webClientId)
                            .setAutoSelectEnabled(true)
                            .build()

                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        coroutineScope.launch {
                            try {
                                val result = credentialManager.getCredential(
                                    context = context,
                                    request = request
                                )
                                val credential = result.credential
                                val googleIdToken = com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.createFrom(credential.data).idToken
                                viewModel.loginWithGoogle(googleIdToken)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Batal atau Gagal Login: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(text = "Masuk dengan Google", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
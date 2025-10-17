package com.coffeebean.ui.feature.login

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.coffeebean.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {}
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Visibility states
    var passwordVisible by remember { mutableStateOf(false) }

    // Google Sign-In
    val googleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    )

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "Google Sign-In result code: ${result.resultCode}")

        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "Google Sign-In successful. Account: ${account?.email}")

                val idToken = account?.idToken
                if (idToken != null) {
                    Log.d(TAG, "ID Token retrieved, calling googleSignIn")
                    viewModel.googleSignIn(idToken)
                } else {
                    Log.e(TAG, "ID Token is null")
                    viewModel.setError("Failed to get authentication token")
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Google Sign-In failed with status code: ${e.statusCode}", e)
                viewModel.setError("Google Sign-In failed: ${e.message}")
            }
        } else {
            Log.d(TAG, "Google Sign-In cancelled or failed")
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(24.dp)
    ) {
        val (imageTitle, image, emailField, passwordField, button, errorText, divider, social) = createRefs()

        // Illustration
        Image(
            painter = painterResource(id = R.drawable.coffee_6_without_bg),
            contentDescription = "Login Illustration",
            modifier = Modifier
                .size(width = 303.dp, height = 366.dp)
                .constrainAs(image)
                {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.Fit
        )

        // Title Logo
        Image(
            painter = painterResource(id = R.drawable.logo_steam_bean_title),
            contentDescription = "Title Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(64.dp)
                .constrainAs(imageTitle) {
                    top.linkTo(image.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email address") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(emailField) {
                    top.linkTo(imageTitle.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(passwordField) {
                    top.linkTo(emailField.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Login Button
        Button(
            onClick = { viewModel.loginUser(email, password) },
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .constrainAs(button) {
                    top.linkTo(passwordField.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "Login",
                    fontSize = 20.sp
                )
            }
        }

        // Error Message
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage ?: "",
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(errorText) {
                    top.linkTo(button.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
        }

        // Divider with "or"
        Text(
            text = "──────────  or  ──────────",
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(divider) {
                top.linkTo(button.bottom, margin = if (uiState.errorMessage != null) 48.dp else 24.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        // Social Buttons Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.constrainAs(social) {
                top.linkTo(divider.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Icon(painterResource(R.drawable.apple_logo), contentDescription = "Apple")
            Icon(
                painter = painterResource(R.drawable.google_logoi),
                contentDescription = "Google",
                tint = Color.Unspecified,
                modifier = Modifier.clickable {
                    Log.d(TAG, "Google Sign-In button clicked")
                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                }            )
            Icon(painterResource(R.drawable.facebook_logo), contentDescription = "Facebook", tint = Color.Unspecified)
        }
    }

    // Handle successful login
    LaunchedEffect(uiState.isSuccess, uiState.googleSignInSuccess) {
        Log.d(TAG, "LaunchedEffect triggered - isSuccess: ${uiState.isSuccess}, googleSignInSuccess: ${uiState.googleSignInSuccess}")
        if (uiState.isSuccess || uiState.googleSignInSuccess) {
            Log.d(TAG, "Calling onLoginSuccess")
            onLoginSuccess()
            viewModel.onNavigationHandled()
        }
    }
}

@Composable
fun LoginScreenPreviewable(
    uiState: LoginUiState = LoginUiState(),
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onGoogleSignInClick: () -> Unit = {},
    errorMessage: String? = null
) {
    val dummyContext = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(24.dp)
    ) {
        val (imageTitle, image, emailField, passwordField, button, errorText, divider, social) = createRefs()

        // Illustration
        Image(
            painter = painterResource(id = R.drawable.coffee_6_without_bg),
            contentDescription = "Login Illustration",
            modifier = Modifier
                .size(width = 303.dp, height = 366.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.Fit
        )

        // Title Logo
        Image(
            painter = painterResource(id = R.drawable.logo_steam_bean_title),
            contentDescription = "Title Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(64.dp)
                .constrainAs(imageTitle) {
                    top.linkTo(image.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email address") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(emailField) {
                    top.linkTo(imageTitle.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(passwordField) {
                    top.linkTo(emailField.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Button(
            onClick = { onLoginClick(email, password) },
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .constrainAs(button) {
                    top.linkTo(passwordField.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Login", fontSize = 20.sp)
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(errorText) {
                    top.linkTo(button.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
        }

        Text(
            text = "──────────  or  ──────────",
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(divider) {
                top.linkTo(button.bottom, margin = if (errorMessage != null) 48.dp else 24.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.constrainAs(social) {
                top.linkTo(divider.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Icon(painterResource(R.drawable.apple_logo), contentDescription = "Apple")
            Icon(painterResource(R.drawable.google_logoi), contentDescription = "Google", tint = Color.Unspecified)
            Icon(painterResource(R.drawable.facebook_logo), contentDescription = "Facebook", tint = Color.Unspecified)
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreenPreviewable(
        uiState = LoginUiState(isLoading = false),
        errorMessage = null
    )
}



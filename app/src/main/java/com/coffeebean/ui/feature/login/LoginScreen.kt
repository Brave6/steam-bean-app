package com.coffeebean.ui.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {}
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Visibility states
    var passwordVisible by remember { mutableStateOf(false) }

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
            painter = painterResource(id = R.drawable.coffee_title),
            contentDescription = "Title Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(40.dp)
                .constrainAs(imageTitle) {
                    top.linkTo(image.bottom, margin = 16.dp)
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
            Icon(painterResource(R.drawable.google_logoi), contentDescription = "Google", tint = Color.Unspecified)
            Icon(painterResource(R.drawable.facebook_logo), contentDescription = "Facebook", tint = Color.Unspecified)
        }
    }

    // Navigate on success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}

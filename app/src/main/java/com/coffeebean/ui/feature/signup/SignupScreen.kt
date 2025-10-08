package com.coffeebean.ui.feature.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.coffeebean.R
import com.coffeebean.ui.theme.Recolleta
import com.coffeebean.ui.theme.coffeebeanPurple

@Composable
fun SignupScreen(
    onSignUpClick: () -> Unit = {}
) {
    val viewModel: SignupViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Visibility states
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Handle success and errors
    LaunchedEffect(uiState.isSuccess, uiState.errorMessage) {
        if (uiState.isSuccess) {
            onSignUpClick()
        }
        errorMessage = uiState.errorMessage
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(24.dp)
    ) {
        val (image, title, emailField, passwordField, confirmField, button, terms, loading, errorText) = createRefs()

        // Illustration
        Image(
            painter = painterResource(id = R.drawable.coffee_5_illus),
            contentDescription = "Signup Illustration",
            modifier = Modifier
                .size(width = 366.dp, height = 303.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Title
        Text(
            text = "Create Account",
            fontFamily = Recolleta,
            fontWeight = Bold,
            fontSize = 28.sp,
            color = coffeebeanPurple,
            modifier = Modifier.constrainAs(title) {
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
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(emailField) {
                    top.linkTo(title.bottom, margin = 24.dp)
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
                    top.linkTo(emailField.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Confirm Password Field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm password") },
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                val icon = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(icon, contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(confirmField) {
                    top.linkTo(passwordField.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Error Message
        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(errorText) {
                        top.linkTo(confirmField.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
        }

        // Sign Up Button
        Button(
            onClick = {
                when {
                    email.isBlank() || password.isBlank() || confirmPassword.isBlank() ->
                        errorMessage = "All fields are required"
                    password != confirmPassword ->
                        errorMessage = "Passwords do not match"
                    else -> {
                        errorMessage = null
                        viewModel.register(email, password)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .constrainAs(button) {
                    top.linkTo(confirmField.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            enabled = !uiState.isLoading
        ) {
            Text("Sign up")
        }

        // Loading Indicator
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .constrainAs(loading) {
                        top.linkTo(button.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
        }

        // Terms & Conditions
        Text(
            text = "By creating an account you agree to our Terms and Conditions",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.constrainAs(terms) {
                top.linkTo(button.bottom, margin = if (uiState.isLoading) 56.dp else 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Sc() {
    SignupScreen()
}
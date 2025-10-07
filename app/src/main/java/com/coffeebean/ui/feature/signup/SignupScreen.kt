package com.coffeebean.ui.feature.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.coffeebean.R
import com.coffeebean.ui.feature.splash.SplashContent
import com.coffeebean.ui.theme.headlineCustom

@Composable
fun SignupScreen(
    onSignUpClick: () -> Unit = {}
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(24.dp)
    ) {
        val (image, title, email, password, confirmPassword, button, terms) = createRefs()

        // Illustration
        Image(
            painter = painterResource(id = R.drawable.coffee_2_illus),
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
            style = MaterialTheme.typography.headlineCustom,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(image.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        // Email Field
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Email address") },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(email) {
                    top.linkTo(title.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Password Field
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = { Icon(Icons.Default.Visibility, contentDescription = "Toggle") },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(password) {
                    top.linkTo(email.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Confirm Password Field
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Confirm password") },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = { Icon(Icons.Default.Visibility, contentDescription = "Toggle") },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(confirmPassword) {
                    top.linkTo(password.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Sign Up Button
        Button(
            onClick = onSignUpClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .constrainAs(button) {
                    top.linkTo(confirmPassword.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Text("Sign up")
        }

        // Terms & Conditions
        Text(
            text = "By creating an account or signing you agree to our Terms and Conditions",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.constrainAs(terms) {
                top.linkTo(button.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun sc() {
    SignupScreen()
}
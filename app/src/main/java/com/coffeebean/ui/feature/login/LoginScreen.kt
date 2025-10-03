package com.coffeebean.ui.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.coffeebean.R
import com.coffeebean.ui.feature.signup.SignupScreen
import com.coffeebean.ui.theme.headlineCustom
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {}
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(24.dp)
    ) {
        val (imageTitle, image, tagline, logo, email, password, button, forgot, divider, social) = createRefs()

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
                }
        )

        // Title Logo (brand image instead of text)
        Image(
            painter = painterResource(id = R.drawable.coffee_title), // replace with your logo drawable
            contentDescription = "Title Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(40.dp) // adjust size as needed
                .constrainAs(imageTitle) {
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
                    top.linkTo(imageTitle.bottom, margin = 24.dp)
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
                    top.linkTo(email.bottom, margin = 20.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Login Button
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .constrainAs(button) {
                    top.linkTo(password.bottom, margin = 20.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.titleMedium.copy( // base M3 style
                fontSize = 20.sp, // custom size
                )
            )
        }

        // Forgot Password
        Text(
            text = "Forgot Password?",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            modifier = Modifier.constrainAs(forgot) {
                top.linkTo(button.bottom, margin = 20.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        // Divider with "or"
        Text(
            text = "──────────  or  ──────────",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(divider) {
                top.linkTo(forgot.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        // Social Buttons Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .constrainAs(social) {
                    top.linkTo(divider.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Icon(painterResource(R.drawable.apple_logo),
                contentDescription = "Apple")
            Icon(
                painter = painterResource(id = R.drawable.google_logoi),
                contentDescription = "Google",
                tint = Color.Unspecified
            )
            Icon(
                painter = painterResource(id = R.drawable.facebook_logo),
                contentDescription = "Facebook",
                tint = Color.Unspecified
            )
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun sc() {
    LoginScreen()
}
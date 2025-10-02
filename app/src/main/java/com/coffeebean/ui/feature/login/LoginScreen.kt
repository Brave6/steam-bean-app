package com.coffeebean.ui.feature.login

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.coffeebean.R
import com.coffeebean.ui.theme.headlineCustom
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {}
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        val (image, tagline, logo, email, password, button, forgot, divider, social) = createRefs()

        // Illustration
        Image(
            painter = painterResource(id = R.drawable.coffee_6_illus), // replace with asset
            contentDescription = "Login Illustration",
            modifier = Modifier
                .size(180.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Tagline
        Text(
            text = "Fuel your day with a perfect brew.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(tagline) {
                top.linkTo(image.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        // Brand Logo Text
        Text(
            text = "THE COFFEE BEAN & TEA LEAF",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(logo) {
                top.linkTo(tagline.bottom, margin = 24.dp)
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
                    top.linkTo(logo.bottom, margin = 24.dp)
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

        // Login Button
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .constrainAs(button) {
                    top.linkTo(password.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Text("Login")
        }

        // Forgot Password
        Text(
            text = "Forgot Password?",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.constrainAs(forgot) {
                top.linkTo(button.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        // Divider with "or"
        Text(
            text = "──────────  or  ──────────",
            style = MaterialTheme.typography.bodySmall,
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
            Icon(painterResource(R.drawable.apple_logo), contentDescription = "Apple")
            Icon(painterResource(R.drawable.google), contentDescription = "Google")
            Icon(painterResource(R.drawable.facebook), contentDescription = "Facebook")
        }
    }
}

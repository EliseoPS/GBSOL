package com.example.gymbuddyapp.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alparslanguney.example.nfc.R
import com.alparslanguney.example.nfc.datasource.services.AuthService
import com.alparslanguney.example.nfc.domain.dtos.Auth
import com.alparslanguney.example.nfc.domain.use_cases.SharedPref
import com.alparslanguney.example.nfc.util.Lock
import com.alparslanguney.example.nfc.util.Screens
import com.alparslanguney.example.nfc.util.Visibility
import com.alparslanguney.example.nfc.util.Visibility_off
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@Composable
fun LoginScreen(innerPadding: PaddingValues, navController: NavController){
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var isPasswordVisible by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val sharedPref = SharedPref(LocalContext.current)
    // Creación de FocusRequester para los dos campos
    val passwordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "GymBuddy",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Image(
            painter = painterResource(R.drawable.loginimagegymbuddy),
            contentDescription = "Login",
            modifier = Modifier.size(200.dp).padding(vertical = 15.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            placeholder = { Text("Correo electronico") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray
            ),
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "correo") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    passwordFocusRequester.requestFocus()
                }
            )
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            placeholder = { Text("Contraseña") },
            visualTransformation = if (!isPasswordVisible) PasswordVisualTransformation() else
                VisualTransformation.None,
            modifier = Modifier.fillMaxWidth().focusRequester(passwordFocusRequester),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Cierra el teclado virtual
                    focusManager.clearFocus()
                }
            ),
            leadingIcon = {
                Icon(imageVector = Lock, contentDescription = "pass")
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        isPasswordVisible = !isPasswordVisible
                    },
                    content = {
                        val icon = if(!isPasswordVisible) Visibility else Visibility_off
                        Icon(imageVector = icon,
                            contentDescription = "Ver", )
                    }
                )

            }
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.onSurface
            ),
            onClick = {
                scope.launch(Dispatchers.IO) {
                    try {
                        // Configurar cliente OkHttp que confía en todos los certificados (no recomendado en producción)
                        val trustAllCerts = arrayOf<javax.net.ssl.TrustManager>(
                            object : X509TrustManager {
                                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                            }
                        )

                        val sslContext = SSLContext.getInstance("TLS").apply {
                            init(null, trustAllCerts, SecureRandom())
                        }

                        val okHttpClient = OkHttpClient.Builder()
                            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                            .hostnameVerifier { _, _ -> true }
                            .build()

                        // Configurar Retrofit con el cliente personalizado
                        val authService = Retrofit.Builder()
                            .baseUrl("https://157.230.187.109/")
                            .client(okHttpClient) // Integra el cliente aquí
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(AuthService::class.java)

                        // Lógica de autenticación
                        val auth = Auth(email = email, password = password)
                        val response = authService.login(auth)

                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            Log.i("LoginScreen", "Response: $responseBody")
                            if (responseBody?.isLogged == true) {
                                withContext(Dispatchers.Main) {
                                    sharedPref.saveUserSharedPref(
                                        userId = response.body()?.userId ?: 0,
                                        isLogged = true
                                    )
                                    navController.navigate(Screens.ExerciseSelectScreen.route){
                                        popUpTo(Screens.ExerciseSelectScreen.route) { inclusive=true }
                                    }
                                }
                            } else {
                                Log.e("LoginScreen", "Login failed: ${responseBody?.message}")
                            }
                        } else {
                            Log.e("LoginScreen", "HTTP Error: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        Log.e("LoginScreen", "Exception: ${e.message}")
                    }
                }
            },
        ){
            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "¿No tienes una cuenta? Crea una",
            color = Color.Gray,
            modifier = Modifier.clickable {
                Log.i("Login","navegar")
                navController.navigate(Screens.ExerciseSelectScreen.route)
            }
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun LoginScreenPreview(){

    LoginScreen(innerPadding = PaddingValues(0.dp), navController = rememberNavController())

}
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alparslanguney.example.nfc.R
import com.alparslanguney.example.nfc.domain.use_cases.SharedPref
import com.alparslanguney.example.nfc.util.Logout
import com.alparslanguney.example.nfc.util.Screens


@Composable
fun ExerciseSelectScreen(innerPadding: PaddingValues, navController: NavController){
    val sharedPref = SharedPref(LocalContext.current)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = "Hola: ",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = {
                sharedPref.removeUserSharedPref()
                navController.navigate(Screens.LoginScreen.route){
                    popUpTo(Screens.LoginScreen.route) { inclusive = true }
                }
            }){
                Icon(
                    imageVector = Logout,
                    contentDescription = "salir",
                    modifier = Modifier.size(30.dp)
                )
            }

        }
        Text(
            text = "Seleccione una opción:",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 35.sp
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(10.dp)
                .clickable {
                    navController.navigate(Screens.NFCEntryScreen.route)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painter = painterResource(R.drawable.nfcroutine),
                contentDescription = "rutinas automaticas",
                modifier = Modifier.size(200.dp)
            )
            Text(text = "Rutinas Automaticas")
        }



        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(10.dp)
                .clickable {
                    navController.navigate(Screens.ManualEntryScreen.route)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painter = painterResource(R.drawable.manualroutine),
                contentDescription = "Rutina manual",
                modifier = Modifier.size(200.dp)
            )
            Text(text = "Rutinas Manuales")
        }


    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ExerciseSelectScreenPreview(){
    ExerciseSelectScreen(innerPadding = PaddingValues(0.dp), navController = rememberNavController())
}
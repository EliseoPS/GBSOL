    package com.example.gymbuddyapp.Screens

    import android.os.Build
    import android.widget.Toast
    import androidx.annotation.RequiresApi
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.text.KeyboardActions
    import androidx.compose.foundation.text.KeyboardOptions
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.MoreVert
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.focus.FocusRequester
    import androidx.compose.ui.focus.focusRequester
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.platform.LocalFocusManager
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.text.input.ImeAction
    import androidx.compose.ui.text.input.KeyboardType
    import androidx.compose.ui.text.input.PasswordVisualTransformation
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavController
    import androidx.navigation.compose.rememberNavController
    import com.alparslanguney.example.nfc.R
    import com.alparslanguney.example.nfc.util.ChevronDown
    import com.alparslanguney.example.nfc.util.Screens
    import java.time.LocalDate
    import java.time.format.DateTimeFormatter


    @Composable
    fun ManualEntryScreen(innerPadding: PaddingValues, navController: NavController,currentDate: String){
        var reps by remember {
            mutableStateOf("")
        }
        var ejercicio by remember {
            mutableStateOf("")
        }
        var peso by remember {
            mutableStateOf("")
        }
        val fecha = currentDate

        val repsFocusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var expanded by remember { mutableStateOf(false) }
            var selectedOption by remember { mutableStateOf("") }
            val options = listOf("Opción 1", "Opción 2", "Opción 3")

            Text(
                text = "Ejercicio Manual",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.height(26.dp)
            )


            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Selecciona el nombre del ejercicio",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = currentDate,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedOption,
                        onValueChange = {},
                        placeholder = { Text("Ejercicio") },
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                // Cierra el teclado virtual
                            }
                        ),
                        readOnly = true
                    )
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = ChevronDown,
                            contentDescription = "More",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedOption = option
                                    expanded = false // Cierra el menú después de seleccionar
                                }
                            )
                        }
                    }
                }


                //            ExposedDropdownMenuBox(
                //                expanded = expanded,
                //                onExpandedChange = { expanded = !expanded }
                //            ) {
                //                TextField(
                //                    value = selectedOption,
                //                    onValueChange = { selectedOption = it },
                //                    readOnly = true, // Hace que solo se seleccione de la lista
                //                    label = { Text("Selecciona una opción") },
                //                    trailingIcon = {
                //                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                //                    },
                //                    colors = TextFieldDefaults.colors() // Corrección aquí
                //                )
                //                ExposedDropdownMenu(
                //                    expanded = expanded,
                //                    onDismissRequest = { expanded = false }
                //                ) {
                //                    options.forEach { option ->
                //                        DropdownMenuItem(
                //                            text = { Text(option) },
                //                            onClick = {
                //                                selectedOption = option
                //                                expanded = false // Cierra el menú después de seleccionar
                //                            }
                //                        )
                //                    }
                //                }
                //            }
            }


            Spacer(
                modifier = Modifier.height(26.dp)
            )
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Ingresa el peso",
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = peso,
                    onValueChange = {
                        // Filtra los caracteres permitidos (solo números y un único punto decimal)
                        if (it.all { char -> char.isDigit() || char == '.' }) {
                            // Limitar a 6 caracteres en total
                            if (it.length <= 6) {
                                // Limitar a 2 decimales
                                if (it.count { char -> char == '.' } <= 1) {
                                    // Solo aceptar hasta 2 decimales
                                    val parts = it.split(".")
                                    if (parts.size == 1 || (parts.size == 2 && parts[1].length <= 2)) {
                                        peso = it
                                    }
                                }
                            }
                        }
                    },
                    placeholder = { Text("Peso (kg)") },
                    modifier = Modifier.fillMaxWidth().focusRequester(repsFocusRequester),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Cierra el teclado virtual
                            repsFocusRequester.requestFocus()
                        }
                    )
                )
            }


            Spacer(
                modifier = Modifier.height(26.dp)
            )
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Ingresa las repeticiones",
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = {
                        // Filtra los caracteres permitidos (solo números y un único punto decimal)
                        if (it.all { char -> char.isDigit() || char == '.' }) {
                            // Limitar a 6 caracteres en total
                            if (it.length <= 6) {
                                // Limitar a 2 decimales
                                if (it.count { char -> char == '.' } <= 1) {
                                    // Solo aceptar hasta 2 decimales
                                    val parts = it.split(".")
                                    if (parts.size == 1 || (parts.size == 2 && parts[1].length <= 2)) {
                                        reps = it
                                    }
                                }
                            }
                        }
                    },
                    placeholder = { Text("Repeticiones") },
                    modifier = Modifier.fillMaxWidth().focusRequester(repsFocusRequester),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Cierra el teclado virtual
                            focusManager.clearFocus()
                        }
                    )
                )
            }

            Spacer(
                modifier = Modifier.height(26.dp)
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
                    navController.navigate(Screens.HomeScreen.route)

                }
            ) {
                Text(
                    text = "Registrar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

        }
    }




    @Preview(
        showBackground = true,
        showSystemUi = true
    )
    @Composable
    fun ManualEntryScreenPreview(){
        ManualEntryScreen(innerPadding = PaddingValues(0.dp), navController = rememberNavController(), currentDate = "10-10-2003")
    }
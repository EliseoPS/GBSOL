package com.alparslanguney.example.nfc

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alparslanguney.example.nfc.domain.use_cases.SharedPref
import com.alparslanguney.example.nfc.models.BottomNavigationItem
import com.alparslanguney.example.nfc.ui.theme.GymBuddyAppTheme
import com.alparslanguney.example.nfc.util.INTENT_ACTION_NFC_READ
import com.alparslanguney.example.nfc.util.NfcBroadcastReceiver
import com.alparslanguney.example.nfc.util.Screens
import com.alparslanguney.example.nfc.util.getParcelableCompatibility
import com.example.gymbuddyapp.Screens.*
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    val currentDate: String
        get() {
            val current = LocalDate.now() // Obtén la fecha actual
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Define el formato deseado
            return current.format(formatter) // Devuelve la fecha como String
        }

    private var nfcAdapter: NfcAdapter? = null

    fun enableNfcForegroundDispatch() {
        nfcAdapter?.let { adapter ->
            if (adapter.isEnabled) {
                val nfcIntentFilter = arrayOf(
                    IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
                )

                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_MUTABLE
                    )
                } else {
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
                adapter.enableForegroundDispatch(
                    this, pendingIntent, nfcIntentFilter, null
                )
            }
        }
    }

    fun disableNfcForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }
    override fun onPause() {
        super.onPause()
        disableNfcForegroundDispatch()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val action = intent?.action
        Log.d("NFC", "Intent action: $action")

        if (action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }

            val ndefMessage = readNdefMessage(tag)
            if (ndefMessage != null) {
                Toast.makeText(this, "NFC NDEF Message: $ndefMessage", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "NFC tag detected, but no NDEF data", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            val navController = rememberNavController()
            val sharedPref = SharedPref(LocalContext.current)
            val isLogged = sharedPref.getIsLoggedSharedPref()
            var selectedItem by rememberSaveable {
                mutableIntStateOf(0)
            }
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentScreen = currentBackStackEntry?.destination?.route
            GymBuddyAppTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {

                    }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentScreen != Screens.LoginScreen.route) {
                            AnimatedNavigationBar(
                                selectedIndex = 0,
                                modifier = Modifier
                                    .height(90.dp),
                                barColor = Color(0xFFFF931F),
                                cornerRadius = shapeCornerRadius(cornerRadius = 34.dp)
                            ) {
                                BottomNavigationItem.items.forEachIndexed { index, bottomNavigationItem ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable {
                                                selectedItem = index
                                                navController.navigate(bottomNavigationItem.route)
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = bottomNavigationItem.icon,
                                            contentDescription = bottomNavigationItem.title,
                                            tint = if (selectedItem == index) Color.White else Color.White.copy(alpha = 0.5f),
                                            modifier = Modifier.size(26.dp)
                                        )
                                        Text(
                                            text = bottomNavigationItem.title,
                                            color = if (selectedItem == index) Color.White else Color.White.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }

                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = if (isLogged) Screens.ExerciseSelectScreen.route else Screens.LoginScreen.route
                    ) {
                        composable(route = Screens.LoginScreen.route) {
                            LoginScreen(innerPadding = innerPadding, navController = navController)
                        }
                        composable(route = Screens.StatsScreen.route) {
                            StatsScreen(innerPadding = innerPadding)
                        }
                        composable(route = Screens.DashboardScreen.route) {
                            DashboardScreen(innerPadding = innerPadding)
                        }
                        composable(route = Screens.ExerciseSelectScreen.route) {
                            ExerciseSelectScreen(innerPadding = innerPadding, navController = navController)
                        }
                        composable(route = Screens.ManualEntryScreen.route) {
                            ManualEntryScreen(innerPadding = innerPadding, navController = navController,currentDate = currentDate)
                        }
                        composable(route = Screens.NFCEntryScreen.route) {
                            NFCEntryScreen(innerPadding = innerPadding, navController = navController)
                        }
                    }
                }
            }
        }

    }

    @OptIn(ExperimentalStdlibApi::class)
    @Composable
    fun Greeting(modifier: Modifier = Modifier) {

        var nfcCardId by remember {
            mutableStateOf("")
        }

        Text(text = "Read Card : $nfcCardId", modifier = modifier)

        NfcBroadcastReceiver { tag ->
            nfcCardId = tag.id.toHexString()
        }
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun GreetingPreview() {
        GymBuddyAppTheme {
            Greeting()
        }
    }

    fun readNdefMessage(tag: Tag?): String? {
        val ndef = Ndef.get(tag)
        var data :String? = ""
        try{
            if (ndef != null) {
                ndef.connect()
                val ndefMessage = ndef.ndefMessage
                val records = ndefMessage?.records
                val recordData = records?.mapNotNull { record ->
                    record.payload?.let { String(it) }
                }?.joinToString()
                ndef.close()
                data = recordData

            } else {
                Log.d("NFC", "No NDEF records found.")
                data = null
            }
            return data
        }
        catch (e:Exception){
            Log.i("MainError",e.toString())
            return ""
        }
    }
}
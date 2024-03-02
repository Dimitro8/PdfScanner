package com.saventiy.pdfscanner

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.saventiy.pdfscanner.ui.theme.PdfScannerTheme

class MainActivity : ComponentActivity() {

    private val pdfUri = mutableStateOf("")
    private val options = GmsDocumentScannerOptions.Builder().setGalleryImportAllowed(false).setPageLimit(1)
        .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
        .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL).build()

    private val scanner = GmsDocumentScanning.getClient(options)
    private val scannerLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val result = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                result?.pdf?.let { pdf ->
                    pdfUri.value = pdf.uri.path.toString()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PdfScannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Button(
                        onClick = { scan(this@MainActivity) },
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        Text(text = "scan")
                    }
                }
            }
        }
    }

    private fun scan(activity: MainActivity) {
        scanner.getStartScanIntent(activity).addOnSuccessListener { intentSender ->
            scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
        }.addOnFailureListener {
            Toast.makeText(this, "Some problem with scanning", Toast.LENGTH_SHORT).show()
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PdfScannerTheme {
        Greeting("Android")
    }
}
package com.example.metrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import io.scanbot.sdk.persistence.Page
import io.scanbot.sdk.ui.view.camera.DocumentScannerActivity
import io.scanbot.sdk.ui.view.camera.configuration.DocumentScannerConfiguration

class ScanFragment : Fragment() {

    private lateinit var documentScannerResult: ActivityResultLauncher<DocumentScannerConfiguration>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myButton = view.findViewById<Button>(R.id.myButton)

        documentScannerResult = registerForActivityResult(DocumentScannerActivity.ResultContract()) { result ->
            if (result.resultOk) {
                val snappedPages: List<Page>? = result.result
                // here you can add your logic to show or process the snapped pages
            }
        }

        myButton.setOnClickListener {
            val cameraConfiguration = DocumentScannerConfiguration()
            documentScannerResult.launch(cameraConfiguration)
        }
    }
}
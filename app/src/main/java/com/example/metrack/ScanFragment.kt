package com.example.metrack

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.example.metrack.databinding.FragmentScanBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.scanbot.pdf.model.PageDirection
import io.scanbot.pdf.model.PageSize
import io.scanbot.pdf.model.PdfAttributes
import io.scanbot.pdf.model.PdfConfig
import io.scanbot.sdk.ScanbotSDK
import io.scanbot.sdk.persistence.Page
import io.scanbot.sdk.process.PDFRenderer
import io.scanbot.sdk.ui.view.camera.DocumentScannerActivity
import io.scanbot.sdk.ui.view.camera.configuration.DocumentScannerConfiguration
import java.io.File


class ScanFragment : Fragment() {


    private lateinit var pdfRenderer: PDFRenderer
    private lateinit var binding: FragmentScanBinding
    private var pdfFileUri: Uri? = null
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference

    private lateinit var documentScannerResult: ActivityResultLauncher<DocumentScannerConfiguration>
    private lateinit var launcherPhoto: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScanBinding.inflate(inflater, container, false)
        init()
        initClickListeners()
        pdfRenderer = ScanbotSDK(requireContext()).createPdfRenderer()
        return binding.root
    }

    private fun init() {
        binding = FragmentScanBinding.inflate(layoutInflater)


        val userId = FirebaseAuth.getInstance().currentUser?.uid
        storageReference = FirebaseStorage.getInstance().reference.child("pdfs/$userId")
        databaseReference = FirebaseDatabase.getInstance("https://metrack-app-d3ffd-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("pdfs/$userId")

        documentScannerResult = registerForActivityResult(DocumentScannerActivity.ResultContract()) { result ->
            if (result.resultOk) {
                val snappedPages: List<Page>? = result.result
                if (snappedPages != null) {
                    generatePDF(snappedPages)
                }
            }
        }
        launcherPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            pdfFileUri = uri
            val fileName = uri?.let { DocumentFile.fromSingleUri(requireContext(), it)?.name }
            binding.fileName.text = fileName.toString()
        }
        // Set the "None" RadioButton as checked by default
        binding.radioButtonNone.isChecked = true

    }

    private fun generatePDF(pages: List<Page>) {
        val pdfConfig = PdfConfig(
            pdfAttributes = PdfAttributes(
                author = "Your author",
                creator = "Your creator",
                title = "Your title",
                subject = "Your subject",
                keywords = "Your keywords"
            ),
            pageSize = PageSize.CUSTOM,
            pageDirection = PageDirection.AUTO
        )
        val pdfFile = pdfRenderer.renderDocumentFromPages(pages, pdfConfig)
        if (pdfFile != null) {
            uploadScannedPdfToFirebase(pdfFile)
        }
    }

    private fun uploadScannedPdfToFirebase(pdfFile: File) {
    val pdfUri = Uri.fromFile(pdfFile)
    val fileName = pdfFile.name
    val storageRef = storageReference.child("${System.currentTimeMillis()}/$fileName")

    storageRef.putFile(pdfUri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                Log.i("ScanFragment", downloadUri.toString())
                val pdfFile = PdfFile(fileName, downloadUri.toString(), "Scanned Document")
                databaseReference.push().key?.let { pushKey ->
                    databaseReference.child(pushKey).setValue(pdfFile)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Scanned PDF uploaded and stored successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(requireContext(), "Failed to store scanned PDF: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
        .addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "Failed to upload scanned PDF: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
}

    private fun initClickListeners() {
        binding.selectPdfButton.setOnClickListener {
            launcher.launch("application/pdf")
        }
        binding.uploadBtn.setOnClickListener {
            if (pdfFileUri != null) {
                uploadPdfFileToFirebase()
            } else {
                Toast.makeText(requireContext(), "Please select pdf first", Toast.LENGTH_SHORT).show()
            }
        }

        binding.scanButton.setOnClickListener {
            val config = DocumentScannerConfiguration()
            documentScannerResult.launch(config)
        }

    }



    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pdfFileUri = uri
        val fileName = uri?.let { DocumentFile.fromSingleUri(requireContext(), it)?.name }
        binding.fileName.text = fileName.toString()
    }

    private fun uploadPdfFileToFirebase() {

        val fileName = binding.fileName.text.toString()
        val mStorageRef = storageReference.child("${System.currentTimeMillis()}/$fileName")

        // Get the selected category from the RadioGroup
        val selectedCategoryId = binding.categoryRadioGroup.checkedRadioButtonId
        val selectedCategoryButton = binding.root.findViewById<RadioButton>(selectedCategoryId)
        val selectedCategory = selectedCategoryButton.text.toString()

        pdfFileUri?.let { uri ->
            mStorageRef.putFile(uri).addOnSuccessListener {

                mStorageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Log.i("ScanFragment", downloadUri.toString())
                    val pdfFile = PdfFile(fileName, downloadUri.toString(), selectedCategory)
                    databaseReference.push().key?.let { pushKey ->
                        databaseReference.child(pushKey).setValue(pdfFile)
                            .addOnSuccessListener {

                                pdfFileUri = null
                                binding.fileName.text =
                                    resources.getString(R.string.no_pdf_file_selected_yet)
                                Toast.makeText(requireContext(), "Uploaded Successfully", Toast.LENGTH_SHORT)
                                    .show()

                                if (binding.progressBar.isShown)
                                    binding.progressBar.visibility = View.GONE

                            }.addOnFailureListener { err ->
                                Log.e("ScanFragment", "Error occured upload file", err)
                                Toast.makeText(requireContext(), err.message.toString(), Toast.LENGTH_SHORT)
                                    .show()

                                if (binding.progressBar.isShown)
                                    binding.progressBar.visibility = View.GONE
                            }
                    }
                }.addOnFailureListener {err ->
                    Log.e("ScanFragment", "Error to store file into storage", err)
                }
            }
                .addOnProgressListener { uploadTask ->

                val uploadingPercent = uploadTask.bytesTransferred * 100 / uploadTask.totalByteCount
                binding.progressBar.progress = uploadingPercent.toInt()
                if (!binding.progressBar.isShown)
                    binding.progressBar.visibility = View.VISIBLE

            }.addOnFailureListener { err ->
                if (binding.progressBar.isShown)
                    binding.progressBar.visibility = View.GONE
                Log.e("Scanfragment", "Error to put file into storage", err)
                Toast.makeText(requireContext(), err.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}
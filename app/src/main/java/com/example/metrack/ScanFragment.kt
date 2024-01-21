package com.example.metrack

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.example.metrack.databinding.FragmentScanBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.scanbot.sdk.persistence.Page
import io.scanbot.sdk.ui.view.camera.DocumentScannerActivity
import io.scanbot.sdk.ui.view.camera.configuration.DocumentScannerConfiguration

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private lateinit var documentScannerResult: ActivityResultLauncher<DocumentScannerConfiguration>
    private lateinit var launcher: ActivityResultLauncher<String>
    private var pdfFileUri: Uri? = null
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        initClickListeners()
    }

    private fun init() {
        storageReference = FirebaseStorage.getInstance().reference.child("pdfs")
        databaseReference = FirebaseDatabase.getInstance().reference.child("pdfs")

        documentScannerResult = registerForActivityResult(DocumentScannerActivity.ResultContract()) { result ->
            if (result.resultOk) {
                val snappedPages: List<Page>? = result.result
                // here you can add your logic to show or process the snapped pages
            }
        }

        launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            pdfFileUri = uri
            val fileName = uri?.let { DocumentFile.fromSingleUri(requireContext(), it)?.name }
            binding.fileName.text = fileName.toString()
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
                Toast.makeText(context, "Please select pdf first", Toast.LENGTH_SHORT).show()
            }
        }
        binding.myButton.setOnClickListener {
            val cameraConfiguration = DocumentScannerConfiguration()
            documentScannerResult.launch(cameraConfiguration)
        }
    }

    private fun uploadPdfFileToFirebase() {
        val fileName = binding.fileName.text.toString()
        val mStorageRef = storageReference.child("${System.currentTimeMillis()}/$fileName")

        pdfFileUri?.let { uri ->
            mStorageRef.putFile(uri).addOnSuccessListener {
                mStorageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val pdfFile = PdfFile(fileName, downloadUri.toString())
                    databaseReference.push().key?.let { pushKey ->
                        databaseReference.child(pushKey).setValue(pdfFile)
                            .addOnSuccessListener {
                                pdfFileUri = null
                                binding.fileName.text = getString(R.string.no_pdf_file_selected_yet)
                                Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                                if (binding.progressBar.isShown)
                                    binding.progressBar.visibility = View.GONE
                            }.addOnFailureListener { err ->
                                Toast.makeText(context, err.message.toString(), Toast.LENGTH_SHORT).show()
                                if (binding.progressBar.isShown)
                                    binding.progressBar.visibility = View.GONE
                            }
                    }
                }
            }.addOnProgressListener { uploadTask ->
                val uploadingPercent = uploadTask.bytesTransferred * 100 / uploadTask.totalByteCount
                binding.progressBar.progress = uploadingPercent.toInt()
                if (!binding.progressBar.isShown)
                    binding.progressBar.visibility = View.VISIBLE
            }.addOnFailureListener { err ->
                if (binding.progressBar.isShown)
                    binding.progressBar.visibility = View.GONE
                Toast.makeText(context, err.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
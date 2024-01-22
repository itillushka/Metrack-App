package com.example.metrack

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.example.metrack.databinding.FragmentScanBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ScanFragment : Fragment() {


    private lateinit var binding: FragmentScanBinding
    private var pdfFileUri: Uri? = null
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScanBinding.inflate(inflater, container, false)
        init()
        initClickListeners()
        return binding.root
    }

    private fun init() {
        binding = FragmentScanBinding.inflate(layoutInflater)


        storageReference = FirebaseStorage.getInstance().reference.child("pdfs")
        databaseReference = FirebaseDatabase.getInstance("https://metrack-app-d3ffd-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("pdfs")

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
        binding.showAllBtn.setOnClickListener {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.ScanFrame, LibraryFragment())
        transaction.addToBackStack(null)
        transaction.commit()
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

        pdfFileUri?.let { uri ->
            mStorageRef.putFile(uri).addOnSuccessListener {

                mStorageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Log.i("ScanFragment", downloadUri.toString())
                    val pdfFile = PdfFile(fileName, downloadUri.toString())
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
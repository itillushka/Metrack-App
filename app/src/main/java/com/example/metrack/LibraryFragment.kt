package com.example.metrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.metrack.databinding.FragmentLibraryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LibraryFragment : Fragment(), LibraryAdapter.PdfClickListener {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: LibraryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseReference = FirebaseDatabase.getInstance("https://metrack-app-d3ffd-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("pdfs")
        initRecyclerView()
        getAllPdfs()

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return false
            }
        })
    }

    private fun getAllPdfs() {

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val tempList = mutableListOf<PdfFile>()
                snapshot.children.forEach {
                    val pdfFile = it.getValue(PdfFile::class.java)
                    if (pdfFile != null) {
                        tempList.add(pdfFile)
                    }
                }
                if (tempList.isEmpty())
                    Toast.makeText(requireContext(), "No Data Found", Toast.LENGTH_SHORT)
                        .show()
                adapter.submitPdfList(tempList)
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT)
                    .show()
                binding.progressBar.visibility = View.GONE
            }

        })
    }

    private fun initRecyclerView() {
        binding.pdfsRecyclerView.setHasFixedSize(true)
        binding.pdfsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        adapter = LibraryAdapter(this)
        binding.pdfsRecyclerView.adapter = adapter
    }

    override fun onPdfClicked(pdfFile: PdfFile) {
        val intent = Intent(requireContext(), PdfViewerActivity::class.java)
        intent.putExtra("fileName", pdfFile.fileName)
        intent.putExtra("downloadUrl", pdfFile.downloadUrl)
        startActivity(intent)
    }
}
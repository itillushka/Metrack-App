package com.example.metrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PrescriptionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mList: MutableList<DataModelPrescriptions>
    private lateinit var adapter: ItemPrescriptionsAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_prescriptions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.main_recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance("https://metrack-app-d3ffd-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("pdfs/$userId")

        mList = mutableListOf()

        getAllPdfs()
    }

    private fun getAllPdfs() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val importantList = mutableListOf<String>()
                val activeList = mutableListOf<String>()
                val pendingList = mutableListOf<String>()
                val expiredList = mutableListOf<String>()

                snapshot.children.forEach {
                    val pdfFile = it.getValue(PdfFile::class.java)
                    if (pdfFile != null) {
                        when (pdfFile.category) {
                            "Important Prescriptions" -> importantList.add(pdfFile.fileName)
                            "Active Prescriptions" -> activeList.add(pdfFile.fileName)
                            "Pending Prescriptions" -> pendingList.add(pdfFile.fileName)
                            "Expired Prescriptions" -> expiredList.add(pdfFile.fileName)
                        }
                    }
                }

                mList.add(DataModelPrescriptions(importantList, "Important", "@drawable/important_icon"))
                mList.add(DataModelPrescriptions(activeList, "Active Prescriptions","@drawable/active"))
                mList.add(DataModelPrescriptions(pendingList, "Pending Prescriptions","@drawable/pending"))
                mList.add(DataModelPrescriptions(expiredList, "Expired Prescriptions","@drawable/rejected"))

                adapter = ItemPrescriptionsAdapter(mList)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error...
            }
        })
    }
}
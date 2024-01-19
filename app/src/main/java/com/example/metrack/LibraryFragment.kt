package com.example.metrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class LibraryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var mList = ArrayList<LibraryData>()
    private lateinit var adapter: LibraryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.searchView)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        addDataToList()
        adapter = LibraryAdapter(mList)
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })

        return view
    }

    private fun filterList(query: String?) {

        if (query != null) {
            val filteredList = ArrayList<LibraryData>()
            for (i in mList) {
                if (i.title.lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(requireContext(), "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                adapter.setFilteredList(filteredList)
            }
        }
    }

    private fun addDataToList() {
        mList.add(LibraryData("Liver Document", R.drawable.logo))
        mList.add(LibraryData("Heart Document", R.drawable.logo))
        mList.add(LibraryData("Veins Document", R.drawable.logo))
        mList.add(LibraryData("Pancreas Document", R.drawable.logo))
        mList.add(LibraryData("Brain Document", R.drawable.logo))
        mList.add(LibraryData("Kidney Document", R.drawable.logo))
        mList.add(LibraryData("Muscle Document", R.drawable.logo))
    }
}
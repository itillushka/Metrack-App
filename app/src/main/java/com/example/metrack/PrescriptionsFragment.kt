package com.example.metrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PrescriptionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mList: MutableList<DataModelPrescriptions>
    private lateinit var adapter: ItemPrescriptionsAdapter

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

        mList = mutableListOf()

        //list1
        val nestedList1 = listOf("Jams and Honey", "Pickles and Chutneys", "Readymade Meals", "Chyawanprash and Health Foods", "Pasta and Soups", "Sauces and Ketchup", "Namkeen and Snacks", "Honey and Spreads")

        val nestedList2 = listOf("Book", "Pen", "Office Chair", "Pencil", "Eraser", "NoteBook", "Map", "Office Table")

        val nestedList3 = listOf("Decorates", "Tea Table", "Wall Paint", "Furniture", "Bedsits", "Certain", "Namkeen and Snacks", "Honey and Spreads")

        val nestedList4 = listOf("Pasta", "Spices", "Salt", "Chyawanprash", "Maggie", "Sauces and Ketchup", "Snacks", "Kurkure")


        mList.add(DataModelPrescriptions(nestedList1, "Important", "@drawable/important_icon"))
        mList.add(DataModelPrescriptions(nestedList2, "Active Prescriptions","@drawable/active"))
        mList.add(DataModelPrescriptions(nestedList3, "Pending Prescriptions","@drawable/pending"))
        mList.add(DataModelPrescriptions(nestedList4, "Expired Prescriptions","@drawable/rejected"))


        adapter = ItemPrescriptionsAdapter(mList)
        recyclerView.adapter = adapter
    }
}


package com.example.metrack

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.metrack.databinding.EachItemBinding

class LibraryAdapter(private val listener: PdfClickListener) :
    ListAdapter<PdfFile, LibraryAdapter.PdfFilesViewHolder>(PdfDiffCallback()) {

    private var pdfListFull: List<PdfFile> = listOf()

    inner class PdfFilesViewHolder(private val binding: EachItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onPdfClicked(getItem(adapterPosition))
            }
        }

        fun bind(data: PdfFile) {
            binding.fileName.text = data.fileName
        }
    }

    class PdfDiffCallback : DiffUtil.ItemCallback<PdfFile>() {
        override fun areItemsTheSame(oldItem: PdfFile, newItem: PdfFile) =
            oldItem.downloadUrl == newItem.downloadUrl

        override fun areContentsTheSame(oldItem: PdfFile, newItem: PdfFile) = oldItem == newItem

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfFilesViewHolder {
        val binding = EachItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PdfFilesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PdfFilesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun submitPdfList(list: List<PdfFile>) {
        pdfListFull = ArrayList(list)
        super.submitList(list)
}

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            pdfListFull
        } else {
            pdfListFull.filter {
                it.fileName.contains(query, ignoreCase = true)
            }
        }
        submitList(filteredList)
    }

    interface PdfClickListener {
        fun onPdfClicked(pdfFile: PdfFile)
    }
}
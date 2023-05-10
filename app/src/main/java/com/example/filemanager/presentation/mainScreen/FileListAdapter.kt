package com.example.filemanager.presentation.mainScreen

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager.R
import com.example.filemanager.databinding.FileItemBinding
import com.example.filemanager.domain.model.FileModel
import com.example.filemanager.domain.util.Extension
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileListAdapter(
    private val onDirectoryClick: (path: String) -> Unit,
    private val onFileClick: (file: File) -> Unit,
    private val onShareButtonClick: (file: File) -> Unit,
) : RecyclerView.Adapter<FileListAdapter.FileListViewHolder>() {
    inner class FileListViewHolder(val binding: FileItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FileItemBinding.inflate(inflater, parent, false)

        return FileListViewHolder(binding)

    }

    private val listOfFile: MutableList<FileModel> = mutableListOf()
    override fun onBindViewHolder(holder: FileListViewHolder, position: Int) {
        val fileModel = listOfFile[position]
        val file = fileModel.file
        val fileSize = fileModel.fileSize
        holder.binding.name.text = file.name
        holder.binding.date.text = getDateTime(file.lastModified())
        val fileSizeInBytes = file.length()
        if (!file.isDirectory) {
            holder.binding.shareButton.visibility = View.VISIBLE
            holder.binding.shareButton.setOnClickListener {
                onShareButtonClick(file)
            }
            holder.binding.size.text = getFileSize(fileSizeInBytes)
            holder.binding.root.setOnClickListener {
                onFileClick(file)
            }
            when (file.extension) {
                Extension.Txt.name -> {
                    holder.binding.fileIcon.setImageResource(Extension.Txt.iconId)
                }
                Extension.Png.name -> {
                    holder.binding.fileIcon.setImageResource(Extension.Png.iconId)
                }
                Extension.Jpeg.name -> {
                    holder.binding.fileIcon.setImageResource(Extension.Jpeg.iconId)
                }
                Extension.Pdf.name -> {
                    holder.binding.fileIcon.setImageResource(Extension.Pdf.iconId)
                }
                Extension.Mp3.name -> {
                    holder.binding.fileIcon.setImageResource(Extension.Mp3.iconId)
                }
                Extension.Mp4.name -> {
                    holder.binding.fileIcon.setImageResource(Extension.Mp3.iconId)
                }
            }
        } else {
            holder.binding.shareButton.visibility = View.GONE
            holder.binding.size.text = getFileSize(fileSize!!)
            holder.binding.fileIcon.setImageResource(R.drawable.folder_icon)
            holder.binding.root.setOnClickListener {
                Log.d("SOME", file.path.toString())
                onDirectoryClick(file.name)
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfFile.size
    }

    private fun getDateTime(time: Long): String? {
        return try {
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(time)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    private fun getFileSize(fileSizeInBytes: Long): String {
        val fileSizeInKB = fileSizeInBytes / 1024
        val fileSizeInMB = fileSizeInKB / 1024
        val fileSizeInGb = fileSizeInMB / 1024
        return if (fileSizeInGb >= 1.toLong()) {
            return "$fileSizeInMB gb"
        } else if (fileSizeInMB >= 1.toLong()) {
            return "$fileSizeInMB mb"
        } else if (fileSizeInKB >= 1.toLong()) {
            "$fileSizeInKB kb"
        } else {
            "$fileSizeInBytes bytes"
        }
    }

    fun submitList(newData: List<FileModel>) {
        listOfFile.clear()
        listOfFile.addAll(newData)
        notifyDataSetChanged()
    }
}
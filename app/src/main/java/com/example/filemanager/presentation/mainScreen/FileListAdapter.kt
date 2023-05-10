package com.example.filemanager.presentation.mainScreen

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
import kotlin.math.log10
import kotlin.math.pow

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
            holder.binding.size.text = getFormattedFileSize(fileSizeInBytes)
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
                Extension.Epub.name -> {
                    holder.binding.fileIcon.setImageResource(Extension.Epub.iconId)
                }
                Extension.Mp4.name -> {
                    holder.binding.fileIcon.setImageResource(Extension.Mp4.iconId)
                }
                Extension.Zip.name -> {
                    holder.binding.fileIcon.setImageResource(Extension.Zip.iconId)
                }
            }
        } else {
            holder.binding.shareButton.visibility = View.GONE
            holder.binding.size.text = getFormattedFileSize(fileSize!!)
            holder.binding.fileIcon.setImageResource(R.drawable.folder_icon)
            holder.binding.root.setOnClickListener {
                onDirectoryClick(file.name)
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfFile.size
    }

    private fun getDateTime(time: Long): String? {
        return try {
            val sdf = SimpleDateFormat("dd.MM.yyyy")
            val netDate = Date(time)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }


    private fun getFormattedFileSize(size: Long): String {
        if (size <= 0) return "0 B"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()

        return "%.1f %s".format(size / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
    }

    fun submitList(newData: List<FileModel>) {
        listOfFile.clear()
        listOfFile.addAll(newData)
        notifyDataSetChanged()
    }
}
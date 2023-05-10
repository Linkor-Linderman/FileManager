package com.example.filemanager.presentation.mainScreen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanager.R
import com.example.filemanager.databinding.FragmentMainScreenBinding
import com.example.filemanager.domain.util.FileOrder
import com.example.filemanager.domain.util.OrderType
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainScreenFragment : Fragment() {
    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainScreenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sortButton.setOnClickListener {
            if (binding.sortSection.visibility == View.VISIBLE) {
                binding.sortSection.visibility = View.GONE
            } else {
                binding.sortSection.visibility = View.VISIBLE
            }
        }
        val adapter = FileListAdapter(
            onDirectoryClick = {
                viewModel.onDirectoryClick(it)
            },
            onFileClick = { openFile(it) },
            onShareButtonClick = { shareFile(it) }
        )
        val recyclerViewOfFileScreen = binding.fileList
        recyclerViewOfFileScreen.adapter = adapter
        recyclerViewOfFileScreen.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewModel.files.observe(viewLifecycleOwner) { files ->
            if (files.isEmpty() && viewModel.isLoading.value == false) {
                binding.noFilesText.visibility = View.VISIBLE
            } else {
                binding.noFilesText.visibility = View.GONE
            }
            adapter.submitList(files)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.fileList.visibility = View.INVISIBLE
                binding.noFilesText.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.fileList.visibility = View.VISIBLE
            }
        }
        viewModel.directoryName.observe(viewLifecycleOwner) { directoryName ->
            if (directoryName.isEmpty()) {
                binding.directoryName.text = getString(R.string.your_files)
                binding.arrowBackButton.visibility = View.GONE
            } else {
                binding.arrowBackButton.visibility = View.VISIBLE
                binding.directoryName.text = directoryName
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
        binding.arrowBackButton.setOnClickListener {
            viewModel.onArrowBackButtonClicked()
        }
        binding.navigateToLastModifiedButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainScreenFragment_to_lastModifiedScreenFragment)
        }
        binding.sortTypesGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.nameRadioButton -> {
                    viewModel.onChangeFileOrder(FileOrder.Name(viewModel.orderType.value!!))
                }
                R.id.sizeRadioButton -> {
                    viewModel.onChangeFileOrder(FileOrder.Size(viewModel.orderType.value!!))
                }
                R.id.dateRadioButton -> {
                    viewModel.onChangeFileOrder(FileOrder.DateOfCreation(viewModel.orderType.value!!))
                }
                R.id.extensionRadioButton -> {
                    viewModel.onChangeFileOrder(FileOrder.FileExtension(viewModel.orderType.value!!))
                }
            }
        }
        binding.orderTypeGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.ascendingRadioButton -> {
                    viewModel.onChangeOrderType(OrderType.Ascending)
                }
                R.id.descendingRadioButton -> {
                    viewModel.onChangeOrderType(OrderType.Descending)
                }
            }
        }
    }

    private fun openFile(file: File) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
            val uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".provider",
                file
            )
            intent.setDataAndType(uri, mimeType)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            viewModel.onError()
        }
    }

    private fun shareFile(file: File) {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
        val fileUri =
            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().applicationContext.packageName}.provider",
                file
            )
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = mimeType
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)

        requireContext().startActivity(Intent.createChooser(shareIntent, "Share file using"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
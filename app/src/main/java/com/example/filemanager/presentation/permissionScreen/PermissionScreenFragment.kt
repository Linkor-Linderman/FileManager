package com.example.filemanager.presentation.permissionScreen

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.filemanager.R
import com.example.filemanager.common.PermissionUtils
import com.example.filemanager.common.PermissionUtils.Companion.hasPermissions
import com.example.filemanager.databinding.FragmentPermissionScreenBinding


class PermissionScreenFragment : Fragment() {
    private var _binding: FragmentPermissionScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PermissionScreenViewModel by viewModels()
    private lateinit var permissionUtils: PermissionUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionUtils = PermissionUtils(this) {
            if (hasPermissions(requireContext()))
                viewModel.allowPermission()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (hasPermissions(requireContext())) {
            viewModel.allowPermission()
        }
        binding.allowBtn.setOnClickListener {
            if (hasPermissions(requireContext())) {
                viewModel.allowPermission()
            } else {
                permissionUtils.requestPermissions(PERMISSION_STORAGE)
            }
        }

        viewModel.isPermissionsGranted.observe(viewLifecycleOwner) { allGranted ->
            if (allGranted) {
                findNavController().navigate(R.id.action_permissionScreenFragment_to_mainScreenFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.allowPermission()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val PERMISSION_STORAGE = 101
    }
}
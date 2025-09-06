package com.daarba.flashalert.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.daarba.flashalert.databinding.BottomSheetFlashTypeBinding

class FlashTypeBottomSheet(
    private val context: Context,
    onFlashTypeSelected1: Int,
    private val onFlashTypeSelected: (String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFlashTypeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomSheetFlashTypeBinding.inflate(inflater, container, false)

        // Handle "Continuous" flash selection
        binding.llContinuousFlash.setOnClickListener {
            onFlashTypeSelected("Continuous")
            dismiss()
        }

        // Handle "Rhythm" flash selection
        binding.llRhythmFlash.setOnClickListener {
            onFlashTypeSelected("Rhythm")
            dismiss()
        }

        return binding.root
    }
}

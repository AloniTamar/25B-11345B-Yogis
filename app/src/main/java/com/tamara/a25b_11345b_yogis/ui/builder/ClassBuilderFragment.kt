package com.tamara.a25b_11345b_yogis.ui.builder

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderBinding
import com.tamara.a25b_11345b_yogis.ui.main.MainLoggedInFragment
import com.tamara.a25b_11345b_yogis.utils.navigateBackToMain
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly

class ClassBuilderFragment : Fragment() {

    private var _binding: ClassBuilderBinding? = null
    private val binding get() = _binding!!

    private fun checkForm() {
        val nameFilled = binding.etFocus.text.toString().isNotBlank()
        val durationFilled = binding.etDuration.text.toString().isNotBlank()
        val levelFilled = binding.acLevel.text.toString().isNotBlank()

        val enabled = nameFilled && durationFilled && levelFilled
        binding.btnStart.isEnabled = enabled

        val colorRes = if (enabled) R.color.black else R.color.disabled_button
        val tint = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), colorRes)
        )
        binding.btnStart.backgroundTintList = tint
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ClassBuilderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCbBack.setOnClickListener {
            navigateSmoothly(MainLoggedInFragment())
        }
        binding.tvBackMenu.setOnClickListener {
            navigateBackToMain()
        }

        val levels = resources.getStringArray(R.array.class_levels)
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_menu,
            levels
        )
        binding.acLevel.setAdapter(adapter)
        binding.acLevel.setOnItemClickListener { _, _, _, _ ->
            checkForm()
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                checkForm()
            }
        }
        binding.etFocus.addTextChangedListener(watcher)
        binding.etDuration.addTextChangedListener(watcher)

        binding.btnStart.isEnabled = false
        binding.btnStart.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), R.color.disabled_button)
        )

        binding.btnStart.setOnClickListener {
            navigateSmoothly(ClassBuilderActionsFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

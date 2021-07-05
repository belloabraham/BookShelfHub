package com.bookshelfhub.bookshelfhub.ui.more

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.bookshelfhub.bookshelfhub.databinding.FragmentProfileBinding
import com.bookshelfhub.bookshelfhub.view.toast.Toast

class ProfileFragment : Fragment() {

    private lateinit var layout: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentProfileBinding.inflate(inflater, container, false)

        layout.dobEditTxt.setOnFocusChangeListener { v, hasFocus ->
            MaterialDialog(requireContext()
            ).show {
                datePicker{ dialog, date ->
                    Toast(requireActivity()).showToast(date.toString())
                }
            }
        }

        layout.dobEditTxt.doOnTextChanged { text, start, before, count ->
            MaterialDialog(requireContext()).show {
                datePicker { dialog, date ->
                    Toast(requireActivity()).showToast(date.toString())
                }
            }
        }

        return layout.root
    }


}
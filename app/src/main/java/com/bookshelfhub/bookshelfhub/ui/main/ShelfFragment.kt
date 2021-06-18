package com.bookshelfhub.bookshelfhub.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.bookshelfhub.bookshelfhub.MainActivityViewModel
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.databinding.FragmentShelfBinding
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.view.search.internal.SearchLayout
import com.google.android.material.progressindicator.LinearProgressIndicator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class ShelfFragment : Fragment() {
    @Inject
    lateinit var userAuth: UserAuth
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var layout: FragmentShelfBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentShelfBinding.inflate(inflater, container, false)



        layout.materialSearchView.apply {
            setOnNavigationClickListener(object : SearchLayout.OnNavigationClickListener {
                override fun onNavigationClick(hasFocus: Boolean) {
                    if (hasFocus()) {
                        layout.materialSearchView.clearFocus()
                    } else {
                        layout.materialSearchView.requestFocus()
                    }
                }
            })
            setOnFocusChangeListener(object : SearchLayout.OnFocusChangeListener {
                override fun onFocusChange(hasFocus: Boolean) {
                    layout.materialSearchView.navigationIconSupport = if (hasFocus) {
                        SearchLayout.NavigationIconSupport.ARROW
                    } else {
                        SearchLayout.NavigationIconSupport.SEARCH
                    }
                }
            })
        }


        requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            if (layout.materialSearchView.hasFocus()){
                layout.materialSearchView.clearFocus()
            }else{
                requireActivity().finish()
            }
        }

        layout.gotoStoreBtn.setOnClickListener {
           // mainActivityViewModel.setSelectedIndex(1)

            val view = View.inflate(requireContext(), R.layout.continue_reading, null)
            view.findViewById<TextView>(R.id.percentageText).text = "60%"
            view.findViewById<LinearProgressIndicator>(R.id.progressIndicator).progress = 60
            view.findViewById<TextView>(R.id.bookName).text = "Things Fall Apart"


            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                cornerRadius(16f)
                customView(null, view, true,  noVerticalPadding = true, horizontalPadding = true)
                negativeButton(R.string.continue_reading)
                positiveButton(R.string.dismiss)
                lifecycleOwner(viewLifecycleOwner)
               // debugMode(BuildConfig.DEBUG)
            }

        }



        return layout.root
    }

}
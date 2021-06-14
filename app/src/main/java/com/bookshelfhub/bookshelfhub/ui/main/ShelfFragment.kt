package com.bookshelfhub.bookshelfhub.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bookshelfhub.bookshelfhub.databinding.FragmentShelfBinding
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import org.threeten.bp.LocalDateTime
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class ShelfFragment : Fragment() {
    @Inject
    lateinit var userAuth: UserAuth


    private lateinit var layout: FragmentShelfBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentShelfBinding.inflate(inflater, container, false)

        layout.button.setOnClickListener {
           // Toast.makeText(requireActivity(), userAuth.getProfilePicUri().toString(),Toast.LENGTH_LONG).show()
            val localDateTime = LocalDateTime.now()
            Toast.makeText(requireActivity(), localDateTime.toString(),Toast.LENGTH_LONG).show()
        }

        return layout.root
    }

}
package com.bookshelfhub.bookshelfhub.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bookshelfhub.bookshelfhub.databinding.FragmentOnboardingBinding

class OnBoardingFragment:Fragment() {

    private lateinit var layout: FragmentOnboardingBinding;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentOnboardingBinding.inflate(inflater, container, false);

        val actionLogin = OnBoardingFragmentDirections.actionOnBoardingFragmentToLoginFragment()
        val actionSignUp = OnBoardingFragmentDirections.actionOnBoardingFragmentToSignUpFragment()

        return layout.root
    }

}
package com.bookshelfhub.bookshelfhub.ui.welcome

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.adapters.viewpager.onboarding.SliderAdapter
import com.bookshelfhub.bookshelfhub.adapters.viewpager.onboarding.SliderItem
import com.bookshelfhub.bookshelfhub.databinding.FragmentLoginBinding
import com.bookshelfhub.bookshelfhub.databinding.FragmentOnboardingBinding
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController.ClickListener
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class OnBoardingFragment:Fragment() {

    private var binding: FragmentOnboardingBinding?=null
    lateinit var sliderAdapter:SliderAdapter
    @Inject
    lateinit var userAuth: IUserAuth

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {

        if (userAuth.getIsUserAuthenticated()){
            val actionUserInfo = OnBoardingFragmentDirections.actionOnBoardingFragmentDirectionToUserInfoFragment(false)
            findNavController().navigate(actionUserInfo)
        }

        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        val layout = binding!!

        layout.terms.movementMethod = LinkMovementMethod.getInstance()

        sliderAdapter = SliderAdapter()

        //Setting up Slider view for Onboarding
        layout.sliderView.setSliderAdapter(sliderAdapter)
        layout.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        layout.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        layout.sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT)
        layout.sliderView.setAutoCycle(true)
        layout.sliderView.startAutoCycle()
        layout.sliderView.setOnIndicatorClickListener(ClickListener { position ->
            layout.sliderView.currentPagePosition = position
        })
        addSliderItems(sliderAdapter)


        //Navigate to Login Fragment
        layout.btnLogin.setOnClickListener {
            val login = getString(R.string.login)
            val actionLogin = OnBoardingFragmentDirections.actionOnBoardingFragmentToLoginFragment(login)
            findNavController().navigate(actionLogin)
        }

        //Navigate to Login Fragment
        layout.btnSignup.setOnClickListener {
          val signUp = getString(R.string.sign_up)
            val actionLogin = OnBoardingFragmentDirections.actionOnBoardingFragmentToLoginFragment(signUp)
           findNavController().navigate(actionLogin)
        }

        return layout.root
    }

    private fun addSliderItems(sliderAdapter: SliderAdapter){
            val titles = resources.getStringArray(R.array.titles)
            val subTitles = resources.getStringArray(R.array.subTitles)
            val description = resources.getStringArray(R.array.description)
            val resourceIds:IntArray = intArrayOf(R.drawable.shelf,R.drawable.upload, R.drawable.group )
            val length:Int = titles.size-1;

            for (i in 0..length ){
                val sliderItem = SliderItem(titles[i], subTitles[i], description[i], resourceIds[i]);
                sliderAdapter.addItem(sliderItem)
            }
    }

    override fun onDestroy() {
        binding=null
        super.onDestroy()
    }


}
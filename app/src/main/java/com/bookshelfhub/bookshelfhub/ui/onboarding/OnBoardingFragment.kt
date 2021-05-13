package com.bookshelfhub.bookshelfhub.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.adapters.slider.SliderAdapter
import com.bookshelfhub.bookshelfhub.adapters.slider.SliderItem
import com.bookshelfhub.bookshelfhub.databinding.FragmentOnboardingBinding
import com.bookshelfhub.bookshelfhub.wrapper.imageloader.ImageLoader
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

    private lateinit var layout: FragmentOnboardingBinding;

    @Inject
    lateinit var sliderAdapter:SliderAdapter

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {

        layout = FragmentOnboardingBinding.inflate(inflater, container, false);

        layout.sliderView.setSliderAdapter(sliderAdapter)

        layout.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        layout.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        layout.sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT)
       // layout.sliderView.setIndicatorSelectedColor(Color.WHITE);
       // layout.sliderView.setIndicatorUnselectedColor(Color.GRAY);

        layout.sliderView.setOnIndicatorClickListener(ClickListener { position ->
            layout.sliderView.setCurrentPagePosition(
                position
            )
        })

        addSliderItems(sliderAdapter)

        layout.btnLogin.setOnClickListener {
            val actionLogin = OnBoardingFragmentDirections.actionOnBoardingFragmentToLoginFragment()
            findNavController().navigate(actionLogin)
        }

        layout.btnSignup.setOnClickListener {
            val actionSignUp = OnBoardingFragmentDirections.actionOnBoardingFragmentToSignUpFragment()
            findNavController().navigate(actionSignUp)
        }

        return layout.root
    }

    private fun addSliderItems(sliderAdapter: SliderAdapter){
        val titles = resources.getStringArray(R.array.titles)
        val subTitles = resources.getStringArray(R.array.subTitles)
        val description = resources.getStringArray(R.array.description)
        val resourceIds:IntArray = intArrayOf(R.drawable.logo,R.drawable.logo, R.drawable.logo )
        val length:Int = titles.size-1;

        for (i in 0..length ){
            val sliderItem = SliderItem(titles[i], subTitles[i], description[i], resourceIds[i]);
            sliderAdapter.addItem(sliderItem)
        }

    }

}
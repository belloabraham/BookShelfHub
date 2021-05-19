package com.bookshelfhub.bookshelfhub.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.databinding.FragmentLoginBinding
import com.skydoves.balloon.*

class LoginFragment:Fragment() {

    private lateinit var layout: FragmentLoginBinding;
    private val args:LoginFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentLoginBinding.inflate(inflater, container, false);

        //Populate UI controls with data
        layout.title.setText(args.loginSignup)
        val description= String.format(getString(R.string.login_signup_desicription), args.loginSignup)
        val loginSignupText= String.format(getString(R.string.with_phone), args.loginSignup)
        val googleBtnText= String.format(getString(R.string.google_btn_text), args.loginSignup)
        layout.description.setText(description)
        layout.btnPhoneLogin.setText(loginSignupText)
        layout.btnGoogleLogin.setText(googleBtnText)
        layout.ccp.registerCarrierNumberEditText(layout.phoneNumEditText)

        //
       layout.phoneNumEditText.doOnTextChanged { text, start, before, count ->
           layout.errorAlert.visibility = View.GONE
       }

        layout.btnPhoneLogin.setOnClickListener {

            if (layout.ccp.isValidFullNumber){
                layout.errorAlert.visibility = View.GONE
            }else{

                layout.errorAlert.visibility = View.VISIBLE


                val balloon = createBalloon(this@LoginFragment.requireContext()) {
                    setArrowSize(15)
                    setWidth(BalloonSizeSpec.WRAP)
                    setHeight(BalloonSizeSpec.WRAP)
                    setArrowPosition(0.89f)
                    setArrowOrientation(ArrowOrientation.TOP)
                    setCornerRadius(0f)
                    setMarginRight(8)
                    setAlpha(0.9f)
                    setPadding(8)
                    setText("Enter a valid phone number")
                    setTextColorResource(R.color.white)
                    setBackgroundDrawable(ContextCompat.getDrawable(this@LoginFragment.requireContext(), R.drawable.error_background))
                    setTextSize(14f)
                    setBackgroundColorResource(R.color.black)
                    setOnBalloonClickListener(OnBalloonClickListener {
                    })
                    setBalloonAnimation(BalloonAnimation.ELASTIC)
                    setOnBalloonDismissListener {
                        this@LoginFragment.layout.textInputLayout.requestFocus()
                        val imm: InputMethodManager =
                            (this@LoginFragment.requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        imm.showSoftInput(this@LoginFragment.layout.phoneNumEditText, InputMethodManager.SHOW_IMPLICIT)
                    }
                    setLifecycleOwner(lifecycleOwner)
                }
                balloon.showAlignBottom(layout.errorAlert)


            }
        }


        return layout.root
    }

}
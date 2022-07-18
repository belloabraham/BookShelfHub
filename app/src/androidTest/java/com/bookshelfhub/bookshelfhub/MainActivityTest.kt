package com.bookshelfhub.bookshelfhub

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest{

    @Test
    fun didMainActivityLaunch(){
        //Launch MainActivity
        val mainActivityScenario = ActivityScenario.launch(MainActivity::class.java)
        // Check if a view in main activity is visible
        onView(withId(R.id.container)).check(matches(isDisplayed()))
        // Check if a view in main activity is visible
        onView(withId(R.id.container)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }


}
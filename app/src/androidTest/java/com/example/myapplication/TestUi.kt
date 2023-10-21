package com.example.myapplication

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TestUI {
    @get:Rule
    var activityScenarioRule: ActivityTestRule<MainActivity> =
        ActivityTestRule(MainActivity::class.java)

    @Test
    fun a() {
        onView(withId(R.id.recycleview)).perform(CustomRecycleviewAction(10))
        Thread.sleep(3000)
        onView(withId(R.id.button)).perform(CustomViewAction("qiopc"))
        onView(withId(R.id.button)).perform(CustomAnimationViewAction(true))
        Thread.sleep(1000)
        onView(withId(R.id.button)).perform(CustomViewAction("con cặc"))
        Thread.sleep(1000)
        onView(withId(R.id.button)).perform(CustomAnimationViewAction(false))
        Thread.sleep(1000)


    }
}

class CustomViewAction(val text: String) : ViewAction {
    override fun getDescription(): String {
        return text
    }

    override fun getConstraints(): Matcher<View> {
        return allOf(isDisplayed(), isAssignableFrom(TextView::class.java))
    }

    override fun perform(uiController: UiController?, view: View?) {
        val view = (view as TextView)
        view.text = text
    }
}

class CustomAnimationViewAction(val isScaleUp: Boolean) : ViewAction {
    override fun getDescription(): String {
        return "text"
    }

    override fun getConstraints(): Matcher<View> {
        return allOf(isDisplayed(), isAssignableFrom(TextView::class.java))
    }

    override fun perform(uiController: UiController?, view: View?) {
        val view = (view as TextView)
        if (isScaleUp)
            view.animate()
                .x(400f)
                .y(1000f)
                .scaleX(4f)
                .scaleY(4f)
                .duration = 1000
        else
            view.animate()
                .x(0f)
                .y(0f)
                .scaleX(1f)
                .scaleY(1f)
                .duration = 1000
    }
}

class CustomRecycleviewAction(val position: Int) : ViewAction {
    override fun getDescription(): String {
        return "text"
    }

    override fun getConstraints(): Matcher<View> {
        return Matchers.allOf(
            isAssignableFrom(
                RecyclerView::class.java
            ), isDisplayed()
        )
    }

    override fun perform(uiController: UiController?, view: View?) {
        (view as RecyclerView).smoothScrollToPosition(position)
    }

}
package ru.kharlov.lab1

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.io.File

@RunWith(AndroidJUnit4::class)
class LabScenarioTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private fun screenshot(name: String) {
        closeSoftKeyboard()
        Thread.sleep(2400)
        val directory = File(context.getExternalFilesDir(null), "screenshots").apply { mkdirs() }
        assertTrue(UiDevice.getInstance(instrumentation).takeScreenshot(File(directory, "$name.png")))
    }
    private fun type(id: Int, value: String) { onView(withId(id)).perform(scrollTo(), replaceText(value)); closeSoftKeyboard() }
    private fun tap(id: Int) { onView(withId(id)).perform(scrollTo(), click()) }
    private fun check(id: Int, text: String) { onView(withId(id)).check(matches(withText(text))) }
    @Test fun helloWorld() {
        ActivityScenario.launch(MainActivity::class.java).use {
            check(R.id.greeting, "Hello World!")
            screenshot("01-hello-world")
        }
    }
}

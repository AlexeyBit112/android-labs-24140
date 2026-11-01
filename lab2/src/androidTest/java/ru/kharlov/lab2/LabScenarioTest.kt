package ru.kharlov.lab2

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
    @Test fun formAndNavigation() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            screenshot("01-form")
            tap(R.id.submit); check(R.id.result, "Введите имя"); screenshot("02-empty-name")
            type(R.id.name, "Алексей")
            tap(R.id.submit); check(R.id.result, "Введите возраст от 0 до 150"); screenshot("03-invalid-age")
            type(R.id.age, "151"); tap(R.id.submit); check(R.id.result, "Введите возраст от 0 до 150")
            type(R.id.age, "9999999999999999"); tap(R.id.submit); check(R.id.result, "Введите возраст от 0 до 150")
            // Нечисловой ввод программно проверяет toIntOrNull независимо от цифровой клавиатуры.
            scenario.onActivity { activity ->
                activity.findViewById<android.widget.EditText>(R.id.age).apply { inputType = android.text.InputType.TYPE_CLASS_TEXT; setText("abc") }
            }
            tap(R.id.submit); check(R.id.result, "Введите возраст от 0 до 150")
            type(R.id.age, "20"); tap(R.id.submit); check(R.id.result, "Примите правила"); screenshot("04-consent-required")
            tap(R.id.agree); tap(R.id.submit)
            check(R.id.result, "Имя: Алексей, Возраст: 20, Правила: Да"); screenshot("05-success")
            tap(R.id.navigation)
            tap(R.id.go)
            type(R.id.message, "Привет, Android!"); screenshot("06-navigation-input"); tap(R.id.go)
            check(R.id.received, "Получено: Привет, Android!"); screenshot("07-detail")
            tap(R.id.back); check(R.id.status, "Возврат из второго экрана выполнен"); screenshot("08-return")
        }
    }
}

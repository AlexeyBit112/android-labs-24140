package ru.kharlov.lab5

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.hamcrest.Matchers.allOf
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class LabScenarioTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    private fun waitFor(check: () -> Unit) {
        val end = System.currentTimeMillis() + 10_000
        while (true) {
            try { check(); return }
            catch (e: AssertionError) {
                if (System.currentTimeMillis() >= end) throw e
            }
            catch (e: androidx.test.espresso.NoMatchingViewException) {
                if (System.currentTimeMillis() >= end) throw e
            }
            catch (e: androidx.test.espresso.AmbiguousViewMatcherException) {
                if (System.currentTimeMillis() >= end) throw e
            }
            Thread.sleep(100)
        }
    }
    private fun status(text: String) = waitFor {
        onView(withId(R.id.status)).check(matches(withText(text)))
    }
    private fun count(value: Int) = waitFor {
        onView(withId(R.id.count)).check(matches(withText("Всего: $value")))
    }
    private fun fill(title: String, content: String) {
        onView(withId(R.id.title)).perform(replaceText(title))
        onView(withId(R.id.content)).perform(replaceText(content))
        closeSoftKeyboard()
    }
    private fun select(title: String) = waitFor {
        onView(allOf(withId(R.id.noteTitle), withText(title))).perform(click())
    }
    private fun screenshot(name: String) {
        closeSoftKeyboard()
        Thread.sleep(2400)
        val directory = File(context.getExternalFilesDir(null), "screenshots").apply { mkdirs() }
        assertTrue(UiDevice.getInstance(instrumentation).takeScreenshot(File(directory, "$name.png")))
    }

    @Test fun roomNotesFlow() {
        context.deleteDatabase("room_notes.db")
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            count(0); screenshot("01-empty")
            onView(withId(R.id.add)).perform(click())
            status("Введите заголовок"); count(0); screenshot("02-title-required")
            fill("   ", "Пустой заголовок не должен сохраняться")
            onView(withId(R.id.add)).perform(click()); status("Введите заголовок"); count(0)
            onView(withId(R.id.update)).perform(click()); status("Сначала выберите заметку")
            onView(withId(R.id.delete)).perform(click()); status("Сначала выберите заметку")
            screenshot("03-selection-required")

            fill("Заметка Room", "Список обновляется автоматически.")
            onView(withId(R.id.add)).perform(click()); status("Заметка добавлена"); count(1)
            waitFor { onView(withId(R.id.noteTitle)).check(matches(withText("Заметка Room"))) }
            screenshot("04-added")
            select("Заметка Room")
            onView(withId(R.id.title)).check(matches(withText("Заметка Room")))
            onView(withId(R.id.content)).check(matches(withText("Список обновляется автоматически.")))
            screenshot("05-selected")

            fill("Заметка Room", "Несохранённый текст")
            val device = UiDevice.getInstance(instrumentation)
            try {
                device.setOrientationLeft()
                waitFor { onView(withId(R.id.content)).check(matches(withText("Несохранённый текст"))) }
                screenshot("11-landscape")
            } finally { device.setOrientationNatural() }
            waitFor { onView(withId(R.id.content)).check(matches(withText("Несохранённый текст"))) }
            fill("", "Текст без заголовка")
            onView(withId(R.id.update)).perform(click()); status("Введите заголовок"); count(1)
            screenshot("06-title-required-update")
            fill("Заметка Room", "Текст изменён через DAO.")
            onView(withId(R.id.update)).perform(click()); status("Заметка обновлена"); count(1)
            waitFor { onView(allOf(withId(R.id.noteContent), withText("Текст изменён через DAO."), isDisplayed())).check(matches(isDisplayed())) }
            screenshot("07-updated")

            fill("Удаляемая заметка", "Вторая запись")
            onView(withId(R.id.add)).perform(click()); status("Заметка добавлена"); count(2)
            screenshot("08-two-notes")
            select("Удаляемая заметка")
            onView(withId(R.id.delete)).perform(click()); status("Заметка удалена"); count(1)
            waitFor { onView(withText("Удаляемая заметка")).check(doesNotExist()) }
            screenshot("09-deleted")
            scenario.recreate()
            count(1)
            waitFor { onView(allOf(withId(R.id.noteContent), withText("Текст изменён через DAO."), isDisplayed())).check(matches(isDisplayed())) }
            screenshot("10-after-recreate")
        }
    }
}

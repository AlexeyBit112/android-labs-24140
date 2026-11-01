package ru.kharlov.lab4

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
    @Test fun sqliteCrud() {
        context.deleteDatabase("notes.db")
        NotesDbHelper(context).use { db ->
            val id = db.insertNote("O'Reilly", "Текст с 'кавычками' и Unicode")
            assertTrue(id > 0)
            assertEquals("O'Reilly", db.getAllNotes().single().title)
            assertEquals(1, db.updateNote(id, "Изменено", "Новый текст"))
            assertEquals("Новый текст", db.getAllNotes().single().content)
            assertEquals(0, db.updateNote(-1, "Нет", "Нет"))
            assertEquals(0, db.deleteNote(-1))
            assertEquals(1, db.deleteNote(id))
            assertTrue(db.getAllNotes().isEmpty())
        }
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            Thread.sleep(700); screenshot("01-empty-database")
            onView(withId(R.id.add)).perform(click()); check(R.id.status, "Введите заголовок"); screenshot("02-title-required")
            onView(withId(R.id.update)).perform(click()); check(R.id.status, "Сначала выберите заметку")
            onView(withId(R.id.delete)).perform(click()); check(R.id.status, "Сначала выберите заметку")
            fun fill(heading: String, text: String) {
                onView(withId(R.id.title)).perform(replaceText(heading))
                onView(withId(R.id.content)).perform(replaceText(text)); closeSoftKeyboard()
            }
            fill("Первая заметка", "Данные хранятся в SQLite.")
            onView(withId(R.id.add)).perform(click()); Thread.sleep(700); screenshot("03-inserted")
            onView(withText("Первая заметка")).perform(click()); screenshot("04-selected")
            UiDevice.getInstance(instrumentation).setOrientationLeft(); Thread.sleep(800)
            onView(withId(R.id.title)).check(matches(withText("Первая заметка"))); screenshot("08-landscape")
            UiDevice.getInstance(instrumentation).setOrientationNatural(); Thread.sleep(800)
            fill("Первая заметка", "Текст изменён через updateNote.")
            onView(withId(R.id.update)).perform(click()); Thread.sleep(700)
            onView(withText("Текст изменён через updateNote.")).check(matches(isDisplayed())); screenshot("05-updated")
            scenario.recreate(); Thread.sleep(700)
            onView(withText("Текст изменён через updateNote.")).check(matches(isDisplayed())); screenshot("06-after-recreate")
            fill("Вторая заметка", "Эту запись удалим.")
            onView(withId(R.id.add)).perform(click()); Thread.sleep(700)
            onView(withText("Вторая заметка")).perform(click())
            onView(withId(R.id.delete)).perform(click()); Thread.sleep(700); screenshot("07-deleted")
            onView(withText("Вторая заметка")).check(androidx.test.espresso.assertion.ViewAssertions.doesNotExist())
            onView(withText("Первая заметка")).check(matches(isDisplayed()))
        }
        NotesDbHelper(context).use { db -> assertEquals("Первая заметка", db.getAllNotes().single().title) }
    }
}

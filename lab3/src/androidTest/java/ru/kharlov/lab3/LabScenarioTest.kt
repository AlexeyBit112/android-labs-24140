package ru.kharlov.lab3

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
    @Test fun storageAndList() {
        context.getSharedPreferences("NotesPrefs", Context.MODE_PRIVATE).edit().clear().commit()
        val file = File(context.filesDir, "my_note.txt")
        file.deleteRecursively()
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            tap(R.id.load); check(R.id.result, "Нет сохранённой заметки (SP)"); screenshot("01-no-preferences")
            tap(R.id.loadFile); Thread.sleep(400); check(R.id.result, "Файл не найден"); screenshot("02-no-file")
            tap(R.id.save); check(R.id.result, "Введите текст заметки"); screenshot("03-empty-input")
            type(R.id.note, "Проверить сохранение заметки после перезапуска.")
            tap(R.id.save); screenshot("04-preferences-saved")
            scenario.recreate()
            tap(R.id.load); check(R.id.result, "Проверить сохранение заметки после перезапуска."); screenshot("05-preferences-loaded")
            tap(R.id.saveFile); Thread.sleep(400); check(R.id.result, "Сохранено в файл my_note.txt"); screenshot("06-file-saved")
            tap(R.id.clear); check(R.id.result, "Данные (SP) очищены"); screenshot("07-cleared")
            tap(R.id.load); check(R.id.result, "Нет сохранённой заметки (SP)")
            tap(R.id.loadFile); Thread.sleep(400); check(R.id.result, "Проверить сохранение заметки после перезапуска."); screenshot("08-file-loaded")
            assertEquals("Проверить сохранение заметки после перезапуска.", file.readText())
            // Каталог вместо файла создаёт настоящую ошибку записи/чтения.
            file.delete(); assertTrue(file.mkdir())
            type(R.id.note, "Текст для проверки ошибки")
            tap(R.id.saveFile); Thread.sleep(400); check(R.id.result, "Не удалось записать файл"); screenshot("09-write-error")
            tap(R.id.loadFile); Thread.sleep(400); check(R.id.result, "Не удалось прочитать файл"); screenshot("10-read-error")
            assertTrue(file.delete())
            tap(R.id.saveFile); Thread.sleep(400)
            tap(R.id.list)
            onView(withId(R.id.add)).perform(click())
            screenshot("11-empty-list")
            fun add(value: String) {
                onView(withId(R.id.newNote)).perform(replaceText(value)); closeSoftKeyboard()
                onView(withId(R.id.add)).perform(click())
            }
            add("Разобраться с Intent"); add("Повторить SharedPreferences"); add("Проверить RecyclerView")
            screenshot("12-three-notes")
            onView(withText("Разобраться с Intent")).check(matches(isDisplayed()))
            onView(org.hamcrest.Matchers.allOf(withId(R.id.delete), hasSibling(withText("Повторить SharedPreferences")))).perform(click())
            screenshot("13-note-deleted")
            onView(withText("Повторить SharedPreferences")).check(androidx.test.espresso.assertion.ViewAssertions.doesNotExist())
            UiDevice.getInstance(instrumentation).setOrientationLeft(); Thread.sleep(800)
            onView(withText("Разобраться с Intent")).check(matches(isDisplayed()))
            UiDevice.getInstance(instrumentation).setOrientationNatural()
        }
    }
}

// Иллюстрация для отчёта, не входит в сборку. Нужны импорты androidx.room.* и KSP.
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val timestamp: Long
)
@Dao
interface NotesDao {
    @Insert suspend fun insert(note: Note): Long
    @Query("SELECT * FROM notes ORDER BY timestamp DESC, id DESC")
    suspend fun getAllNotes(): List<Note>
    @Update suspend fun update(note: Note): Int
    @Delete suspend fun delete(note: Note): Int
}
@Database(entities = [Note::class], version = 1)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
}

package org.evidyaloka.core.student.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.evidyaloka.core.student.database.dao.CourseContentDAO
import org.evidyaloka.core.student.database.entity.BookmarkEntity
import org.evidyaloka.core.student.database.entity.CourseContentEntity
import org.evidyaloka.core.student.database.entity.StudentEntity

@Database(entities = [StudentEntity::class, CourseContentEntity::class,BookmarkEntity::class], version = 5)
abstract class CourseDB : RoomDatabase() {
    abstract fun courseContentDAO(): CourseContentDAO
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE `BookmarkEntity` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `studentId` INTEGER, `subtopicId` INTEGER, `timetableId` INTEGER, `topicId` INTEGER, `offeringId` INTEGER, `sessionId` INTEGER, `topicName` TEXT, `subTopicName` TEXT) ")
    }
}
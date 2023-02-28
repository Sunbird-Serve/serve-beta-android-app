package org.evidyaloka.core.student.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.evidyaloka.core.student.database.entity.BookmarkEntity
import org.evidyaloka.core.student.database.entity.CourseContentEntity
import org.evidyaloka.core.student.database.entity.StudentEntity

@Dao
interface CourseContentDAO {

    //Student related functions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStudent(vararg student: StudentEntity)

    @Query("SELECT * FROM StudentEntity")
    fun getAllStudents(): LiveData<List<StudentEntity>>

    @Query("SELECT * FROM StudentEntity where id = :studentId")
    fun getStudentById(studentId: Int): LiveData<StudentEntity>

    //Content related functions
    @Insert
    fun insertCourseContent(vararg content: CourseContentEntity)

    @Delete
    fun deleteCourseContent(content: CourseContentEntity)

    @Query("SELECT * FROM CourseContentEntity where contentId = :contentId")
    fun getDownloadDetailsByContentId(contentId: Long): CourseContentEntity?

    @Query("SELECT * FROM CourseContentEntity where id = :id")
    fun getDownloadDetailsById(id: Long): CourseContentEntity?

    @Query("SELECT * FROM CourseContentEntity where downloadStatus != 16 AND type!='BOOKMARK'") // except failed downloads
    fun getAll(): LiveData<List<CourseContentEntity>>

    @Query("SELECT * FROM CourseContentEntity where studentId = :studentId AND downloadStatus != 16 AND type!='BOOKMARK'") // except failed downloads
    fun getContentByStudentId(studentId: Int): LiveData<List<CourseContentEntity>>

    @Query("UPDATE CourseContentEntity set downloadStatus = :downloadStatus,reason= :reason where id = :id")
    fun updateStatus(id: Long, downloadStatus: Int?, reason: String?)

    @Query("UPDATE CourseContentEntity set localUrl = :url where id = :id")
    fun updateUrl(id: Long, url: String?)

    @Query("UPDATE CourseContentEntity set progress = :progressDuration, viewStatus = 0 where contentId = :contentId")
    fun updateProgress(contentId: Long, progressDuration: Long)

    @Query("DELETE FROM CourseContentEntity where contentId = :contentId")
    fun deleteByContentId(contentId: Long): Int?


    //    Bookmark
    @Insert
    fun insertBookmark(vararg bookmark: BookmarkEntity)

    @Delete
    fun deleteBookmark(vararg bookmark: BookmarkEntity)

    @Query("SELECT * FROM BookmarkEntity WHERE subtopicId=:subTopicId")
    fun getBookmarkBySubtopicId(subTopicId: Int): BookmarkEntity

    @Query("SELECT * FROM BookmarkEntity where studentId = :studentId") // except failed downloads
    fun getBookmarkByStudentId(studentId: Int): LiveData<List<BookmarkEntity>>

}
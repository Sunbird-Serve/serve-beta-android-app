package org.evidyaloka.common.util

import org.evidyaloka.common.R

/**
 * @author Madhankumar
 * created on 16-03-2021
 *
 */
object CourseTimetableViewUtils {
    private val TAG = "CourseTimetableViewUtils"

    enum class CourseUISettings {
        MATHS {
            override fun background(): Int {
                return R.drawable.bg_course_timetable_maths
            }

            override fun color(): Int {
                return R.color.subject_maths
            }

            override fun complete(): Int {
                return R.drawable.ic_attended_maths
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_maths
            }

            override fun progressDrawable(): Int {
                return R.drawable.ic_subject_maths_progress_bar
            }
        },
        SCIENCE {
            override fun background(): Int {
                return R.drawable.bg_course_timetable_science
            }


            override fun color(): Int {
                return R.color.subject_science
            }

            override fun complete(): Int {
                return R.drawable.ic_attended_science
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_science
            }

            override fun progressDrawable(): Int {
                return R.drawable.ic_subject_science_progress_bar
            }
        },
        ENGLISH {
            override fun background(): Int {
                return R.drawable.bg_course_timetable_english
            }

            override fun color(): Int {
                return R.color.subject_english
            }

            override fun complete(): Int {
                return R.drawable.ic_attended_english
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_english
            }

            override fun progressDrawable(): Int {
                return R.drawable.ic_subject_english_progress_bar
            }
        },
        SOCIAL_SCIENCE {
            override fun background(): Int {
                return R.drawable.bg_course_timetable_sst
            }

            override fun color(): Int {
                return R.color.subject_social_studies
            }

            override fun complete(): Int {
                return R.drawable.ic_attended_sst
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_social_studies
            }

            override fun progressDrawable(): Int {
                return R.drawable.ic_subject_social_studies_progress_bar
            }
        },
        HINDI {
            override fun background(): Int {
                return R.drawable.bg_course_timetable_hindi
            }

            override fun color(): Int {
                return R.color.subject_hindi
            }

            override fun complete(): Int {
                return R.drawable.ic_attended_hindi
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_hindi
            }

            override fun progressDrawable(): Int {
                return R.drawable.ic_subject_hindi_progress_bar
            }
        },
        KANNADA {
            override fun background(): Int {
                return R.drawable.bg_course_timetable_kannada
            }

            override fun color(): Int {
                return R.color.subject_kannada
            }

            override fun complete(): Int {
                return R.drawable.ic_attended_kannada
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_kannada
            }

            override fun progressDrawable(): Int {
                return R.drawable.ic_subject_kannada_progress_bar
            }
        },
        TELUGU {
            override fun background(): Int {
                return R.drawable.bg_course_timetable_telugu
            }

            override fun color(): Int {
                return R.color.subject_telugu
            }

            override fun complete(): Int {
                return R.drawable.ic_attended_telugu
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_telugu
            }

            override fun progressDrawable(): Int {
                return R.drawable.ic_subject_telugu_progress_bar
            }
        },
        TAMIL {
            override fun background(): Int {
                return R.drawable.bg_course_timetable_tamil
            }

            override fun color(): Int {
                return R.color.subject_tamil
            }

            override fun complete(): Int {
                return R.drawable.ic_attended_tamil
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_tamil
            }

            override fun progressDrawable(): Int {
                return R.drawable.ic_subject_tamil_progress_bar
            }
        },
        MALAYALAM {
            override fun background(): Int {
                return R.drawable.bg_course_timetable_malayalam
            }

            override fun color(): Int {
                return R.color.subject_malayalam
            }

            override fun complete(): Int {
                return R.drawable.ic_attended_malayalam
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_malyalam
            }

            override fun progressDrawable(): Int {
                return R.drawable.ic_subject_malayalam_progress_bar
            }
        },
        COMPUTER_SCIENCE {
            override fun background(): Int {
                return R.drawable.bg_course_timetable_cs
            }

            override fun color(): Int {
                return R.color.subject_computer
            }

            override fun complete(): Int {
                return R.drawable.ic_attended_cs
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_cs
            }

            override fun progressDrawable(): Int {
                return R.drawable.ic_subject_cs_progress_bar
            }
        },
        MARATHI {
            override fun background(): Int {
                return R.drawable.bg_course_timetable_marathi
            }

            override fun color(): Int {
                return R.color.subject_marathi
            }

            override fun complete(): Int {
                return R.drawable.ic_attended_marathi
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_marathi
            }

            override fun progressDrawable(): Int {
                return R.drawable.ic_subject_marathi_progress_bar
            }
        },
        BENGALI {
            override fun background(): Int {
                return R.drawable.bg_course_timetable_bengali
            }

            override fun color(): Int {
                return R.color.subject_bengali
            }

            override fun complete(): Int {
                return R.drawable.ic_attended_bengali
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_bengali
            }

            override fun progressDrawable(): Int {
                return R.drawable.ic_subject_bengali_progress_bar
            }
        };

        abstract fun background(): Int
        abstract fun color(): Int
        abstract fun complete(): Int
        abstract fun icon(): Int
        abstract fun progressDrawable(): Int

    }

    fun getCourseUISettings(courseTitle: String): CourseUISettings {
        return when {
            courseTitle.contains("math", true) -> {
                CourseUISettings.MATHS
            }
            courseTitle.contains("social", true) -> {
                CourseUISettings.SOCIAL_SCIENCE
            }
            courseTitle.contains("science", true) -> {
                CourseUISettings.SCIENCE
            }
            courseTitle.contains("english", true) -> {
                CourseUISettings.ENGLISH
            }
            courseTitle.contains("hindi", true) -> {
                CourseUISettings.HINDI
            }
            courseTitle.contains("kannada", true) -> {
                CourseUISettings.KANNADA
            }
            courseTitle.contains("telugu", true) -> {
                CourseUISettings.TELUGU
            }
            courseTitle.contains("tamil", true) -> {
                CourseUISettings.TAMIL
            }
            courseTitle.contains("malayalam", true) -> {
                CourseUISettings.MALAYALAM
            }
            courseTitle.contains("computer", true) -> {
                CourseUISettings.COMPUTER_SCIENCE
            }
            courseTitle.contains("marathi", true) -> {
                CourseUISettings.MARATHI
            }
            courseTitle.contains("bengali", true) -> {
                CourseUISettings.BENGALI
            }
            else -> {
                CourseUISettings.MATHS
            }
        }
    }

}
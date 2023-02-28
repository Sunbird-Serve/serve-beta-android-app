package org.evidyaloka.common.util

import org.evidyaloka.common.R


/**
 * @author Madhankumar
 * created on 16-03-2021
 *
 */
object SubjectViewUtils {

    enum class SubjectUISettings {
        MATHS {
            override fun background(): Int {
                return R.drawable.bg_subject_maths
            }

            override fun timeTableBackground(): Int {
                return R.drawable.ic_timetable_maths
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_maths
            }

            override fun color(): Int {
                return R.color.subject_maths
            }
            override fun complete(): Int {
                return R.drawable.ic_math_complete
            }
        },
        SCIENCE {
            override fun background(): Int {
                return R.drawable.bg_subject_science
            }

            override fun timeTableBackground(): Int {
                return R.drawable.ic_timetable_science
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_science
            }

            override fun color(): Int {
                return R.color.subject_science
            }
            override fun complete(): Int {
                return R.drawable.ic_science_complete
            }
        },
        ENGLISH {
            override fun background(): Int {
                return R.drawable.bg_subject_english
            }

            override fun timeTableBackground(): Int {
                return R.drawable.ic_timetable_english
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_english
            }

            override fun color(): Int {
                return R.color.subject_english
            }
            override fun complete(): Int {
                return R.drawable.ic_english_complete
            }
        } ,
        SOCIAL_SCIENCE {
            override fun background(): Int {
                return R.drawable.bg_subject_social_studies
            }

            override fun timeTableBackground(): Int {
                return R.drawable.ic_timetable_social
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_social_studies
            }

            override fun color(): Int {
                return R.color.subject_social_studies
            }
            override fun complete(): Int {
                return R.drawable.ic_social_complete
            }
        } ,
        HINDI {
            override fun background(): Int {
                return R.drawable.bg_subject_hindi
            }

            override fun timeTableBackground(): Int {
                return R.drawable.ic_timetable_hindi
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_hindi
            }

            override fun color(): Int {
                return R.color.subject_hindi
            }
            override fun complete(): Int {
                return R.drawable.ic_hindi_complete
            }
        } ,
        KANNADA {
            override fun background(): Int {
                return R.drawable.bg_subject_kannada
            }

            override fun timeTableBackground(): Int {
                return R.drawable.ic_timetable_kannada
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_kannada
            }

            override fun color(): Int {
                return R.color.subject_kannada
            }
            override fun complete(): Int {
                return R.drawable.ic_kannada_complete
            }
        } ,
        TELUGU {
            override fun background(): Int {
                return R.drawable.bg_subject_telugu
            }

            override fun timeTableBackground(): Int {
                return R.drawable.ic_timetable_telegu
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_telugu
            }

            override fun color(): Int {
                return R.color.subject_telugu
            }
            override fun complete(): Int {
                return R.drawable.ic_telugu_complete
            }
        } ,
        TAMIL {
            override fun background(): Int {
                return R.drawable.bg_subject_tamil
            }

            override fun timeTableBackground(): Int {
                return R.drawable.ic_timetable_tamil
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_tamil
            }

            override fun color(): Int {
                return R.color.subject_tamil
            }
            override fun complete(): Int {
                return R.drawable.ic_tamil_complete
            }
        } ,
        MALAYALAM {
            override fun background(): Int {
                return R.drawable.bg_subject_malyalam
            }

            override fun timeTableBackground(): Int {
                return R.drawable.ic_timetable_malayalam
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_malyalam
            }

            override fun color(): Int {
                return R.color.subject_malayalam
            }
            override fun complete(): Int {
                return R.drawable.ic_malayalam_complete
            }
        } ,
        COMPUTER_SCIENCE {
            override fun background(): Int {
                return R.drawable.bg_subject_cs
            }

            override fun timeTableBackground(): Int {
                return R.drawable.ic_timetable_computer
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_cs
            }

            override fun color(): Int {
                return R.color.subject_computer
            }
            override fun complete(): Int {
                return R.drawable.ic_computer_complete
            }
        } ,
        MARATHI {
            override fun background(): Int {
                return R.drawable.bg_subject_marathi
            }

            override fun timeTableBackground(): Int {
                return R.drawable.ic_timetable_marathi
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_marathi
            }

            override fun color(): Int {
                return R.color.subject_marathi
            }
            override fun complete(): Int {
                return R.drawable.ic_marathi_complete
            }
        } ,
        BENGALI {
            override fun background(): Int {
                return R.drawable.bg_subject_bengali
            }

            override fun timeTableBackground(): Int {
                return R.drawable.ic_timetable_bengali
            }

            override fun icon(): Int {
                return R.drawable.ic_subject_bengali
            }

            override fun color(): Int {
                return R.color.subject_bengali
            }

            override fun complete(): Int {
                return R.drawable.ic_bengali_complete
            }
        }
        ;
        abstract fun background(): Int
        abstract fun timeTableBackground(): Int
        abstract fun icon(): Int
        abstract fun color(): Int
        abstract fun complete(): Int
    }

    fun getCourseUISettings(courseTitle:String): SubjectUISettings {
        return when{
            courseTitle.contains("math",true) -> {
                SubjectUISettings.MATHS
            }
            courseTitle.contains("science",true) -> {
                SubjectUISettings.SCIENCE
            }
            courseTitle.contains("english",true) -> {
                SubjectUISettings.ENGLISH
            }
            courseTitle.contains("social",true) -> {
                SubjectUISettings.SOCIAL_SCIENCE
            }
            courseTitle.contains("hindi",true) -> {
                SubjectUISettings.HINDI
            }
            courseTitle.contains("kannada",true) -> {
                SubjectUISettings.KANNADA
            }
            courseTitle.contains("telugu",true) -> {
                SubjectUISettings.TELUGU
            }
            courseTitle.contains("tamil",true) -> {
                SubjectUISettings.TAMIL
            }
            courseTitle.contains("malayalam",true) -> {
                SubjectUISettings.MALAYALAM
            }
            courseTitle.contains("computer",true) -> {
                SubjectUISettings.COMPUTER_SCIENCE
            }
            courseTitle.contains("marathi",true) -> {
                SubjectUISettings.MARATHI
            }
            courseTitle.contains("bengali",true) -> {
                SubjectUISettings.BENGALI
            }
            else ->  {
                SubjectUISettings.MATHS
            }
        }
    }

    /**
     * Get background for app bar based on the name of the subject.
     */
    fun getUIBackground(courseTitle: String?): Int {
        courseTitle?.let {
            return when {
                courseTitle.contains("math", true) -> {
                    R.drawable.ic_header_maths
                }
                courseTitle.contains("science", true) -> {
                    R.drawable.ic_header_science
                }
                courseTitle.contains("english", true) -> {
                    R.drawable.ic_header_english
                }
                courseTitle.contains("social", true) -> {
                    R.drawable.ic_header_social_studies
                }
                courseTitle.contains("hindi", true) -> {
                    R.drawable.ic_header_hindi
                }
                courseTitle.contains("kannada", true) -> {
                    R.drawable.ic_header_kannada
                }
                courseTitle.contains("telugu", true) -> {
                    R.drawable.ic_header_telugu
                }
                courseTitle.contains("tamil", true) -> {
                    R.drawable.ic_header_tamil
                }
                courseTitle.contains("malayalam", true) -> {
                    R.drawable.ic_header_malayalam
                }
                courseTitle.contains("computer", true) -> {
                    R.drawable.ic_header_cs
                }
                courseTitle.contains("marathi", true) -> {
                    R.drawable.ic_header_marathi
                }
                courseTitle.contains("bengali", true) -> {
                    R.drawable.ic_header_bengali
                }
                else -> {
                    // If subject doesn't match use default bg
                    R.drawable.ic_header_maths
                }
            }
            // If no value passed use default background.
        } ?: return R.drawable.ic_header_maths
    }

}
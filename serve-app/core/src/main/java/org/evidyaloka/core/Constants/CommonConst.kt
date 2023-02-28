package org.evidyaloka.core.Constants

/**
 * @author Madhankumar
 * created on 04-03-2021
 * Check the unicode using below link
 * https://developers.google.com/interactive-media-ads/docs/sdks/android/client-side/localization#locale-codes
 */
object CommonConst {
    const val LANGUAGE_INDIA_COUNTRY = "IN"
    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_HINDI = "hi"
    const val LANGUAGE_TAMIL = "ta"
    const val LANGUAGE_KANNADA = "kn"
    const val LANGUAGE_TELUGU = "te"
    const val LANGUAGE_MALAYALAM = "ml"
    const val LANGUAGE_MARATHI = "mr"

    const val PINCODE_PAGINATION_COUNT = 10

    //String to check if user if user loged in or not.
    const val IS_LOGED_IN = "is_loged_in"
    const val IS_JOINEE = "is_joinee"
    const val USER_DETAILS = "user_details"
    const val USER_SETTINGS = "user_settings"
    const val SESSION_ID = "EVD-SESSION-ID"

    const val USER_TYPE = "userType"
    const val USER_ACTION = "userAction"

    const val MS_DOC = "ms-doc"
    const val PDF = "pdf"
    const val URL = "url"
    const val MS_XLS = "ms-xls"
    const val MS_PPT = "ms-ppt"
    const val SHOW_START_DATE_DIALOG = "showStartDateDialog"

    enum class ContentType(val value:String){
        VIDEO("video-standard"),
        MS_DOC("ms-doc"),
        PDF("pdf"),
        URL("url"),
        MS_XLS("ms-xls"),
        MS_PPT("ms-ppt");
        companion object {
            fun valueOf(value: String) = values().find { it.value == value }
        }
    }
    enum class PersonaType {
        Parent, Student, Teacher, Partner
    }

    enum class UserActionType{
        Login, Register, Logged, Registered
    }

    //Regex for OTP; min length of OTP 4, max 6
    const val OTP_REGEX = "(\\d{4,6})"

    //Resed timeout for OTP
    const val RESEND_TIMEOUT = 69

    enum class Gender {
        Boy, Girl
    }

    const val WEB_VIEW_URL = "web_view_url"

    //For PDF view
    const val URL_STARTS_WITH_DRIVE_URL = "https://drive.google.com"
    const val URL_STARTS_WITH_GOOGLE_DOC_URL = "https://docs.google.com"

    const val GOOGLE_DOC_URL = "https://docs.google.com/viewer?embedded=true&url="

    enum class Subjects {
        Maths, Science, English, Hindi, SST, Kannada, Telugu, Tamil, Malayalam, CS, Marathi, Bengali
    }

    const val PERSONA_ACTIVITY_PATH = "org.evidyaloka.main.ui.PersonaActivity"
    const val LOCALE_ACTIVITY_PATH = "org.evidyaloka.main.ui.LocaleActivity"
    const val SPLASH_ACTIVITY_PATH = "org.evidyaloka.main.ui.SplashActivity"

}

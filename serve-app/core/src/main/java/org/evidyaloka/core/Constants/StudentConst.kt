package org.evidyaloka.core.Constants

object StudentConst {

    const val PARENT = "parent"
    const val GUARDIAN = "guardian"
    const val FATHER_NAME = "fatherName"
    const val MOTHER_NAME = "motherName"
    const val SUBTOPIC_ID ="subtopicId"
    const val SUBTOPIC_ID_T_CAPS ="subTopicId"
    const val TOPIC_ID ="topicId"
    const val TIMETABLE_ID ="timetableId"
    const val SESSION_ID ="sessionId"
    const val OFFERING_ID ="offeringId"
    const val DOUBTPIC_ID = "doubtPicId"
    const val CONTENT_DETAILS_ID = "contentDetailsId"
    const val CLASS_TYPE = "classType"
    const val DOUBT_ID ="doubtId"
    const val PROGRESS = "progress"
    const val DOUBT_CREATED_DATE = "createdDate"
    const val ERROR = "error"
    const val HASLIKED = "hasLiked"
    const val DEVICE_ID = "deviceId"
    const val DOWNLOAD_STATUS = "downloadStatus"


    const val SESSION ="session"

    const val timetableStatus: String = "timetableStatus"
    const val timetableId: String = "timetableId"
    const val count: String = "count"
    const val classCount: String = "count"

    //start date
    const val START_DATE = "START_DATE"
    const val END_DATE = "END_DATE"

    //Video Type

    const val MAIN_VIDEO = "Main Video"
    const val SUPPLEMENTARY_VIDEO = "Supplementary Video"

    const val WEB_URL: String = "url"
    const val LOCAL_WEB_URL: String = "local_url"
    const val PAGE_TITLE: String = "title"
    const val CONTENT_TYPE: String = "contentType"

    const val DIGITAL_SCHOOL_ID = "digitalSchoolId"
    const val STUDENT_ID = "studentId"
    const val USER_TYPE = "userType"
    const val USER_ID = "userId"
    const val OFFERINGS = "offerings"
    const val NEW_OFFERINGS = "newOfferings"
    const val IS_UPDATE_REQUIRED = "isUpdateScheduleRequired"

    const val DAYS = "days"
    const val SLOTS = "slots"
    const val SLOT_KEY = "key"
    const val PAGINATION_COUNT = 50
    const val DOUBT_PAGINATION_COUNT = 20

    const val CLASS_FILTER = "filter"

    const val WAITING = "1"
    const val RESOLVED = "2"
    const val RESPONDED = "3"

    //Timeslot
    const val earlyMorning = "earlyMorning"
    const val morning = "morning"
    const val afternoon = "afternoon"
    const val evening = "evening"

    enum class Status {
        Active, Alumini
    }

    // Onboarding status
    const val ONBOARDING_STATUS = "onboarding_status"

    const val pending = "pending"
    const val completed = "completed"
    const val course_not_opted = "course not opted"
    const val schedule_not_opted = "schedule not opted"

    //Enroll student
    const val PARENT_NAME = "parentName"
    const val STUDENT_NAME = "studentName"

    const val MOBILE = "mobile"
    const val RELATIONSHIP_TYPE = "relationshipType"
    const val DOB = "dob"
    const val GENDER = "gender"
    const val GRADE = "grade"
    const val MEDIUM_OF_STYDY = "mediumOfStudy"
    const val HAS_TAKEN_GUARDIAN_CONSENT = "hasTakenGuardianConsent"
    const val PHYSICAL_SCHOOL_NAME = "physicalSchoolName"
    const val OFFERINGS_OPTED = "offeringsOpted"

    const val BOY = "Boy"
    const val GIRL = "Girl"
    const val PROFILE_PIC_ID = "profilePicId"
    const val PUSH_TOKEN = "pushToken"
    const val ACTIVITY_CONTENT = "activityContent"
    const val OFFLINE_CONTENT = "offlineContent"
    const val IS_DEEP_LINK = "isDeepLink"

    enum class UserType {
        guardian, student
    }

    enum class Days(val value : Int) {
        Monday(1), Tuesday(2), Wednesday(3), Thursday(4), Friday(5), Saturday(6), Sunday(7)
    }

    enum class ClassType(val value:Int) {
        VOD(1),Live(2)
    }

    enum class ClassStatus {
        Taken, Missed, Scheduled
    }

    enum class ClassFilter {
        All, Taken, Missed, Scheduled
    }

    enum class TimetableStatus{
        success,pending, in_progress, completed
    }

    enum class DocType(val value: Int) {
        DOUBT(1),
        USERPROFILEPIC(2),
        OTHER(3),
        STUDENT_DOUBT(4),
        STUDENT_PROFILE_PIC(6);

        companion object {
            fun valueOf(value: Int) = DocType.values().find { it.value == value }
        }
    }

    enum class DocFormat(val value: Int) {
        PNG(1),
        JPG(2);

        companion object {
            fun valueOf(value: Int) = DocFormat.values().find { it.value == value }
            fun formatOf(string: String) = if (string.toLowerCase().contains("png")) PNG else JPG
        }
    }

    enum class DoubtViewType{
        EDIT, VIEW
    }

    enum class DoubtStatus {
        //1 =open, 2= resolved, 3 = responded
        Waiting, Resolved, Responded
    }

    enum class DoubtResourceType(val value: Int){
        Text(1),
        Image(2),
        Video(3),
        Audio(4),
        Url(5);
        companion object {
            fun valueOf(value: Int) = DoubtResourceType.values().find { it.value == value }
        }
    }

    enum class DownloadStatus(val value: Int){
        Complete(1),
        Removed(2),
        InProgress(3);
        companion object {
            fun valueOf(value: Int) = values().find { it.value == value }
        }
    }

}

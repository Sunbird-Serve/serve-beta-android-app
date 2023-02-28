package org.evidyaloka.core.Constants

/**
 * @author Madhankumar
 * created on 04-01-2021
 *
 */
object PartnerConst {

    const val IMAGE_WIDTH = 100
    const val IMAGE_HEIGHT = 100

    const val FATHER = "father"
    const val MOTHER = "mother"
    const val GUARDIAN = "guardian"

    const val BOY = "Boy"
    const val GIRL = "Girl"

    const val DSM_USER = "dsm_user_details"
    const val USER_DETAILS = "user_details"
    const val USER_SETTINGS = "user_settings"

    const val OLD_PASSWORD = "oldPassword"
    const val NEW_PASSWORD = "newPassword"

    const val PARTNER_ID = "partnerId"
    const val PARTNER_NAME = "partnerName"

    const val USER_ID = "userId"
    const val NAME = "name"
    const val FIRST_NAME = "fname"
    const val LAST_NAME = "lname"
    const val DESCRIPTION = "description"
    const val PURPOSE = "purpose"
    const val EMAIL = "email"
    const val PHONE = "phone"
    const val TALUK = "taluk"
    const val STATE = "state"
    const val DISTRICT = "district"
    const val PINCODE = "pincode"
    const val PIN_CODE = "pin_code"

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

    const val ADDRESS_LINE_1 = "address"
    const val IS_LOGED_IN = "is_loged_in"
    const val ROLE = "role"
    const val DSP_HAS_DSM_ROLE = "dsp_has_dsm_role"
    const val PARTNER_STATUS = "partner_status"
    const val USER_STATUS = "user_status"
    const val PROFILE_PIC_ID = "profilePicId"
    const val DIGITAL_SCHOOL_ID = "digitalSchoolId"
    const val SCHOOL_ID = "schoolId"
    const val DIGITAL_SCHOOL_NAME = "digitalSchoolName"
    const val IS_EDIT_STUDENT = "is_edit_student"
    const val STUDENT_ID = "student_id"
    const val STUDENT_STATUS = "status"

    const val SESSION_ID = "EVD-SESSION-ID"
    const val AUTH_BASIC = "Authorization"

    const val GRADE_TO = "gradeTo"
    const val GRADE_FROM = "gradeFrom"
    const val COURSE_PROVIDER = "courseProvider"
    const val COURSE_PROVIDER_ID = "courseProviderId"
    const val DELETED_STATE_IDS = "deletedStateIds"
    const val DELETED_PINCODE_IDS = "deletedPincodeIds"


    const val PAGINATION_COUNT = 50

    /*
     * time after which dialog should close automatically
     */
    const val DIALOG_CLOSE_TIME = 5000L

    /*
     * Course
     */
    const val COURSE_OFFERING = "COURSE_OFFERING"

    const val COURSE_ID = "courseId"
    const val ACADEMIC_YEAR_ID = "academicYearId"
    const val START_DATE = "startDate"
    const val END_DATE = "endDate"

    const val STATE_ID = "stateId"
    const val PINCODES = "pincodes"
    const val PINCODE_ID = "id"
    const val PINCODE_CODE = "code"
    const val SELECTED_STATES = "selectedStates"
    const val USER_TYPE = "userType"
    const val STUDENT_IDS = "studentIds"
    const val UPDATE_ALL_STUDENT = "isUpdateAllStudent"
    const val STUDENT_PROMOTION_TYPE = "studentPromotionType"

    enum class SchoolStatus {
        Active,
        Pending {
            override fun toString(): String {
                return "Approved"
            }
        }
    }

    enum class SchoolStatusType {
        PENDING {
            override fun toString(): String {
                return "Pending"
            }
        },
        APPROVED {
            override fun toString(): String {
                return "Approved"
            }
        }
    }

    enum class RoleType {
        DSM, DSP
    }

    enum class SpinnerRoleType {
        DSM {
            override fun toString(): String {
                return "Digital School Manager"
            }
        },
        DSP {
            override fun toString(): String {
                return "Digital School Partner"
            }
        }
    }

    enum class PartnerStatus {
        New, InProcess {
            override fun toString(): String {
                return "In Process"
            }
        },
        Approved, OnHold {
            override fun toString(): String {
                return "On Hold"
            }
        },
        NotApproved {
            override fun toString(): String {
                return "Not Approved"
            }
        }
    }

    enum class UserStatus {
        New, Ready, Active, Dormant, Inactive, Others, Lead
    }

    enum class StudentStatus(val value: Int) {
        Active(1), Alumni(2);

        companion object {
            fun valueOf(value: Int) = values().find { it.value == value }
            fun formatOf(string: String) =
                if (string.toLowerCase().contains("active")) Active else Alumni
        }
    }

    enum class DocType(val value: Int) {
        SCHOOLLOGO(1),
        SCHOOLBANNER(2),
        USERPROFILEPIC(3);

        companion object {
            fun valueOf(value: Int) = DocType.values().find { it.value == value }
        }
    }

    enum class DocFormat(val value: Int) {
        PNG(1),
        JPG(2);

        companion object {
            fun valueOf(value: Int) = values().find { it.value == value }
            fun formatOf(string: String) = if (string.toLowerCase().contains("png")) PNG else JPG
        }
    }


    // Dialog types
    enum class DIALOG_TYPE {
        FULL_SCREEN, NORMAL
    }

    enum class VIEW_TYPE {
        ADD_COURSE_SUCCESS,
        DSM_ASSIGNED_SCHOOL,
        SCHOOL_ENROLMENT
    }

    enum class UserActionType {
        Login, Register, Logged, Registered, Join, Joined
    }

}

/**
 * @author Madhankumar
 * created on 22-02-2021
 *
 */
object Config {
    const val compileSdkVersion = 30
    const val buildToolsVersion = "29.0.3"
    const val applicationId = "org.evidyaloka"
    const val minSdkVersion = 21
    const val targetSdkVersion = 30
    const val versionCode = 11
    const val versionName = "1.0.11"
    const val versionNameString = "\"1.0.11\""
    object Repositories{
        const val gradleMaven = "https://jitpack.io"
    }

    object API{
        object Key {
            const val API_KEY = "API_KEY"
            const val API_PASSWORD = "API_PASSWORD"
        }
        object Value{
            object Prod {
                const val API_KEY = "\"kProdMobileEvdAndroidApp\""
                const val API_PASSWORD = "\"6c41e1058c74d9a085a9466002ff3049\""
            }
            object Debug{
                const val API_KEY = "\"kMobilePartnerAndroidApp\""
                const val API_PASSWORD = "\"6c41e1058c74d9a085a9466002ff3049\""
            }
        }
    }
    object Url {
        object Key {
            const val BASE_IMG_URL = "BASE_IMG_URL"
            const val BASE_URL = "BASE_URL"
            const val PARTNER_URL = "PARTNER_URL"
            const val STUDENT_URL = "STUDENT_URL"
            const val TERMS_AND_CONDITIONS_URL = "TERMS_AND_CONDITIONS_URL"
            const val ABOUT_US_URL = "ABOUT_US_URL"
            const val CONTACT_US_URL = "CONTACT_US_URL"
            const val PRIVACY_POLICY_URL = "PRIVACY_POLICY_URL"
            const val FORGET_PASSWORD_URL = "FORGET_PASSWORD_URL"
            const val VOLUNTEER_EXPLORE = "VOLUNTEER_EXPLORE"
            const val LEARN_THROUGH_TV = "LEARN_THROUGH_TV"
            const val FAQ = "FAQ"
            const val P_LOUNGE = "P_LOUNGE"
        }

        object Prod {
            const val BASE_IMG_URL = "\"https://serve.evidyaloka.org/\""
            const val BASE_URL = "\"https://serve.evidyaloka.org/\""
            const val PARTNER_URL = "\"partner/api/v1/\""
            const val STUDENT_URL = "\"student/api/v1/\""
            const val TERMS_AND_CONDITIONS_URL = "\"https://www.evidyaloka.org/policies_terms_conditions\""
            const val ABOUT_US_URL = "\"https://serve.evidyaloka.org/about/\""
            const val CONTACT_US_URL = "\"https://www.evidyaloka.org/contact_us/\""
            const val PRIVACY_POLICY_URL = "\"https://www.evidyaloka.org/policies/\""
            const val FORGET_PASSWORD_URL = "\"https://serve.evidyaloka.org/accounts/password/reset/\""
              const val VOLUNTEER_EXPLORE = "\"https://serve.evidyaloka.org/volunteer/\""
            const val LEARN_THROUGH_TV = "\"https://www.evidyaloka.org/tttinfo/\""
            const val FAQ = "\"https://www.evidyaloka.org/faq/?user=\""
            const val P_LOUNGE = "\"https://serve.evidyaloka.org/mobile-plounge/\""
        }

        object Debug {
            const val BASE_IMG_URL = "\"https://serve.evidyaloka.org/\""
            const val BASE_URL = "\"https://serve.evidyaloka.org/\""
            const val PARTNER_URL = "\"partner/api/v1/\""
            const val STUDENT_URL = "\"student/api/v1/\""
            const val TERMS_AND_CONDITIONS_URL = "\"https://www.evidyaloka.org/policies_terms_conditions\""
            const val ABOUT_US_URL = "\"https://www.evidyaloka.org/about/\""
            const val CONTACT_US_URL = "\"https://www.evidyaloka.org/contact_us/\""
            const val PRIVACY_POLICY_URL = "\"https://www.evidyaloka.org/policies/\""
            const val FORGET_PASSWORD_URL = "\"https://serve.evidyaloka.org/accounts/password/reset/\""
            const val VOLUNTEER_EXPLORE = "\"https://serve.evidyaloka.org/volunteer/\""
            const val LEARN_THROUGH_TV = "\"https://www.evidyaloka.org/tttinfo/\""
            const val FAQ = "\"https://uat.evidyaloka.org/faq/?user=\""
        }
    }
}

object Module {
    const val app = ":app"
    const val partner = ":Partner"
    const val student = ":student"
    const val core = ":core"
    const val common = ":common"
    const val player= ":player"
}
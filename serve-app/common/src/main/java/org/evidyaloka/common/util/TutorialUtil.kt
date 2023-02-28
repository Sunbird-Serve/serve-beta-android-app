package org.evidyaloka.common.util

import android.content.Context
import org.evidyaloka.common.R
import org.evidyaloka.common.ui.banner.Banner
import org.evidyaloka.core.Constants.CommonConst

object TutorialUtil {

    fun getStudentsTutorial(context: Context): List<Banner> {
        var banners = arrayListOf<Banner>()
        banners.add(
            Banner(
                title = context.resources.getString(R.string.banner_3_learn_from),
                title2 = context.resources.getString(R.string.banner_3_and_curated),
                icon = context.resources.getDrawable(R.mipmap.banner_1, null),
                userType = CommonConst.PersonaType.Student
            )
        )
        banners.add(
            Banner(
                title = context.resources.getString(R.string.banner_1_grasp_concepts),
                title2 = context.resources.getString(R.string.banner_1_practice_your_learning),
                icon = context.resources.getDrawable(R.mipmap.banner_2, null),
                userType = CommonConst.PersonaType.Student
            )
        )
        banners.add(
            Banner(
                title = context.resources.getString(R.string.banner_2_ask_doubts),
                title2 = context.resources.getString(R.string.banner_2_we_will_clarify),
                icon = context.resources.getDrawable(R.mipmap.banner_3, null),
                userType = CommonConst.PersonaType.Student
            )
        )
        return banners
    }

    fun getParentsTutorial(context: Context): List<Banner> {
        var banners = arrayListOf<Banner>()
        banners.add(
            Banner(
                title = context.resources.getString(R.string.banner_parent_1),
                icon = context.resources.getDrawable(R.mipmap.banner_6, null),
                userType = CommonConst.PersonaType.Parent
            )
        )
        banners.add(
            Banner(
                title = context.resources.getString(R.string.banner_parent_2),
                icon = context.resources.getDrawable(R.mipmap.banner_5, null),
                userType = CommonConst.PersonaType.Parent
            )
        )
        banners.add(
            Banner(
                title = context.resources.getString(R.string.banner_parent_3),
                icon = context.resources.getDrawable(R.mipmap.banner_7, null),
                userType = CommonConst.PersonaType.Parent
            )
        )
        banners.add(
            Banner(
                title = context.resources.getString(R.string.banner_parent_4),
                icon = context.resources.getDrawable(R.mipmap.banner_3, null),
                userType = CommonConst.PersonaType.Parent
            )
        )
        return banners
    }

    fun getPartnersTutorial(context: Context): List<Banner> {
        var banners = arrayListOf<Banner>()
        banners.add(
            Banner(
                title = context.resources.getString(R.string.banner_1_partner),
                icon = context.resources.getDrawable(R.mipmap.banner_8, null),
                userType = CommonConst.PersonaType.Partner
            )
        )
        banners.add(
            Banner(
                title = context.resources.getString(R.string.banner_2_partner),
                icon = context.resources.getDrawable(R.mipmap.banner_10, null),
                userType = CommonConst.PersonaType.Partner
            )
        )
        banners.add(
            Banner(
                title = context.resources.getString(R.string.banner_3_partner),
                icon = context.resources.getDrawable(R.mipmap.banner_9, null),
                userType = CommonConst.PersonaType.Partner
            )
        )
        return banners
    }
}
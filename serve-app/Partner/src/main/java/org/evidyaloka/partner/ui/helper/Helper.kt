package org.evidyaloka.partner.ui.helper

import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.partner.R


/**
 * @author Madhankumar
 * created on 24-02-2021
 *
 */
object Helper {
    /*
   Method to get courseIcon based on courseName
    */
    fun courseIcon(courseName: String): Int {
        if (courseName.contains("Math", true)) {
            return R.drawable.ic_maths
        } else if (courseName.contains("Social", true)) {
            return R.drawable.ic_social
        } else if (courseName.contains("Science", true)) {
            return R.drawable.ic_science
        } else if (courseName.contains("English", true)) {
            return R.drawable.ic_english
        } else if (courseName.contains("Kannada")) {
            return R.drawable.ic_computer
        } else if (courseName.contains("Computer")) {
            return R.drawable.ic_computer
        } else {
            return R.drawable.ic_maths
        }
    }

    /*
    Method to provide  background based on Status
     */
    fun statusBackground(status: PartnerConst.SchoolStatusType): Int {
        if (status.name.equals(PartnerConst.SchoolStatusType.APPROVED.name, true)) {
            return R.drawable.button_approved
        } else if (status.name.equals(PartnerConst.SchoolStatusType.PENDING.name)) {
            return R.drawable.button_pending
        } else {
            return R.drawable.button_approved
        }
    }
}
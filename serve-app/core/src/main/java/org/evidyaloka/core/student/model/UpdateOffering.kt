package org.evidyaloka.core.student.model

import com.google.gson.annotations.SerializedName

/**
 * @author Madhankumar
 * created on 17-03-2021
 *
 */
data class UpdateOffering (val isUpdateScheduleRequired: Int = 0, val message: String = "")
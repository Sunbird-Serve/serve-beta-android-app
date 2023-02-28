package org.evidyaloka.common.ui.banner

import android.graphics.drawable.Drawable
import org.evidyaloka.core.Constants.CommonConst

data class Banner(
    var title: String,
    var title2: String? = null,
    var icon: Drawable,
    var userType: CommonConst.PersonaType
)

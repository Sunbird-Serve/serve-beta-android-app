package org.evidyaloka.common.ui.rtc

import org.evidyaloka.core.model.rtc.ContentAttributes

interface
OnItemClickListner {
        fun OnItemClick(type: SessionType, contentAttribute: ContentAttributes)
    }
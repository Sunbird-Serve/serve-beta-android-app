package org.evidyaloka.core.partner.model

import java.io.InputStream

/**
 * @author Madhankumar
 * created on 23-01-2021
 *
 */
data class FileDetails(val docType: Int, val format:Int, val filename:String, val inputStream: InputStream)
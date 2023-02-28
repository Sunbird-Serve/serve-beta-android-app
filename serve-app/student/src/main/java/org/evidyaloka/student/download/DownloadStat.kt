package org.evidyaloka.student.download

data class DownloadStat(
    val id: Int?,
    val status: Int?,
    val reason: Int?,
    val uri: String?,
    val localUri: String?,
    val totalSize: String?,
    val lastModifiedTimestamp: String?,
    val bytesSoFar: String?,
    val mimeType: String?
)

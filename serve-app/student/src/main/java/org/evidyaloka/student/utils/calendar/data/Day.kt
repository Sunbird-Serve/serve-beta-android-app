package org.evidyaloka.student.utils.calendar.data

import android.os.Parcel
import android.os.Parcelable
import org.evidyaloka.common.util.Utils
import java.util.*

/**
 * Created by shrikanthravi on 06/03/18.
 */
class Day : Parcelable {
    var year: Int
        private set
    var month: Int
        private set
    var day = 0
        private set

    constructor(year: Int, month: Int, day: Int) {
        this.year = year
        this.month = month
        this.day = day
    }

    constructor(`in`: Parcel) {
        val data = IntArray(3)
        `in`.readIntArray(data)
        year = data[0]
        month = data[1]
        year = data[2]
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeIntArray(
            intArrayOf(
                year,
                month,
                day
            )
        )
    }

    fun toUnixTime(): Long {
        val date = Date(year-1900, month, day)
        return date.time
    }

    fun getDate(): String? {
        return Utils.formatddMMMMyyyy(toUnixTime())
    }

    fun getDateddMMyyyy(): String? {
        return Utils.format_dd_MM_yyyy(toUnixTime())
    }


    val diff: Int
        get() {
            val todayCal = Calendar.getInstance()
            val day = Day(
                todayCal[Calendar.YEAR],
                todayCal[Calendar.MONTH],
                todayCal[Calendar.DAY_OF_MONTH]
            )
            return ((toUnixTime() - day.toUnixTime())
                    / (1000 * 60 * 60 * 24)).toInt()
        }

    companion object {
        val CREATOR: Parcelable.Creator<*> = object : Parcelable.Creator<Any?> {
            override fun createFromParcel(`in`: Parcel): Day? {
                return Day(`in`)
            }

            override fun newArray(size: Int): Array<Day?> {
                return arrayOfNulls(size)
            }
        }
    }
}
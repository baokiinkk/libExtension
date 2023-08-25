package com.vnpay.extension.extensions

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.EditText
import androidx.annotation.ColorInt
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object VNPDateUtils {
    /**
     * convert string to date
     * if string is blank or format is blank then return null
     * if string cannot be parsed then return null
     * else return date
     */
    fun String.toDate(
        format: String, locale: Locale = Locale.getDefault()
    ): Date? {
        if (this.isBlank() || format.isBlank()) return null
        return try {
            SimpleDateFormat(format, locale).parse(this)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * convert string to time long milliseconds
     * use function string to date
     */
    fun String.toTimeLong(
        format: String, locale: Locale = Locale.getDefault()
    ): Long? = toDate(format, locale)?.time

    /**
     * convert time long milliseconds to string with predefined format
     * if format is blank return null
     * if format is not java date time format then catch Exception and return null
     * else return formatted string
     */
    fun Long.toTimeString(
        format: String, locale: Locale = Locale.getDefault()
    ): String? {
        if (format.isBlank()) return null
        return try {
            SimpleDateFormat(format, locale).format(Date(this))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * change time string format from oldFormat to newFormat
     * if string or oldFormat or newFormat is blank then return null
     * if oldFormat/newFormat is illegal then catch exception and return null
     * else return string
     */
    fun String.changeTimeFormat(
        oldFormat: String, newFormat: String, locale: Locale = Locale.getDefault()
    ): String? {
        if (this.isBlank() || oldFormat.isBlank() || newFormat.isBlank()) return null
        return try {
            val simpleDateFormat = SimpleDateFormat(oldFormat, locale)
            val date = simpleDateFormat.parse(this)
            simpleDateFormat.applyPattern(newFormat)
            if (date != null) simpleDateFormat.format(date)
            else null
        } catch (e: Exception) {
            e.printStackTrace()
            this
        }
    }

    /**
     * convert date to time string
     * if format is wrong or illegal then catch exception and return null
     * else return string
     */
    fun Date.toTimeString(format: String, locale: Locale = Locale.getDefault()): String? {
        return if (format.isBlank()) null
        else try {
            SimpleDateFormat(format, locale).format(this)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * get current date time
     */
    fun getCurrentDateTime(): Date = Calendar.getInstance().time

    /**
     * convert date to calendar
     */
    fun Date.toCalendar(): Calendar {
        return Calendar.getInstance().let {
            it.time = this
            it
        }
    }

    /**
     * get previous month of this date
     */
    fun Date.getPreviousMonth(): Date {
        return Calendar.getInstance().let {
            it.time = this
            it.add(Calendar.MONTH, -1)
            it.time
        }
    }

    /**
     * get next month of this date
     */
    fun Date.getNextMonth(): Date {
        return Calendar.getInstance().let {
            it.time = this
            it.add(Calendar.MONTH, 1)
            it.time
        }
    }

    /**
     * get previous day of this date
     */
    fun Date.getPreviousDay(): Date {
        return Calendar.getInstance().let {
            it.time = this
            it.add(Calendar.DAY_OF_MONTH, -1)
            it.time
        }
    }

    /**
     * get next day of this date
     */
    fun Date.getNextDay(): Date {
        return Calendar.getInstance().let {
            it.time = this
            it.add(Calendar.DAY_OF_MONTH, 1)
            it.time
        }
    }

    fun showDialogDate(
        context: Context?,
        isSetMaxDate: Boolean,
        cboBirthdate: EditText,
        Character: String,
        equalCurrentDate: Boolean,
        @ColorInt colorButton: Int
    ) {
        val dates = cboBirthdate.text.toString().trim { it <= ' ' }
            .split(Character.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val Year: Int
        val Month: Int
        val Day: Int
        if (dates.size == 3) {
            Year = dates[2].toInt()
            Month = dates[1].toInt() - 1
            Day = dates[0].toInt()
        } else {
            Year = getCurrentYear()
            Month = getCurrentMonth() - 1
            Day = getCurrentDate()
        }
        val dialog = DatePickerDialog(
            context!!,
            { view: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val Year12: Int = selectedYear
                val Month12: Int = selectedMonth
                val Day12: Int = selectedDay
                val Year1: Int = getCurrentYear()
                val Month1: Int = getCurrentMonth() - 1
                val Day1: Int = getCurrentDate()
                var check = true
                if (Year12 > Year1) {
                    check = false
                } else {
                    if (Year12 == Year1) {
                        if (Month12 > Month1) {
                            check = false
                        } else {
                            if (Month12 == Month1) {
                                if (equalCurrentDate) {
                                    if (Day12 >= Day1) {
                                        check = false
                                    }
                                } else {
                                    if (Day12 > Day1) {
                                        check = false
                                    }
                                }
                            }
                        }
                    }
                }
                var lc = Calendar.getInstance()
                lc[Calendar.YEAR] = Year12
                lc = Calendar.getInstance()
                lc[Calendar.YEAR] = Year12
                lc[Calendar.MONTH] = Month12
                lc[Calendar.DAY_OF_MONTH] = Day12
                val dateFormat = SimpleDateFormat(
                    "dd" + Character
                            + "MM" + Character + "yyyy",
                    Locale.getDefault()
                )
                cboBirthdate.setText(dateFormat.format(lc.time))
            }, Year, Month, Day
        )
        if (isSetMaxDate) {
            dialog.datePicker.maxDate = Date().time
        }
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
            colorButton
        )
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
            colorButton
        )
    }

    fun showDialogDate(
        context: Context?,
        isSetmaxDate: Boolean,
        maxDate: String?,
        isSetminDate: Boolean,
        minDate: String?,
        cboBirthdate: EditText,
        Character: String,
        equaCurrentDate: Boolean,
        actionSelectedDate: (String) -> Unit,
        @ColorInt colorButton: Int
    ) {
        val dates = cboBirthdate.text.toString().trim { it <= ' ' }
            .split(Character.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val Year: Int
        val Month: Int
        val Day: Int
        if (dates.size == 3) {
            Year = dates[2].toInt()
            Month = dates[1].toInt() - 1
            Day = dates[0].toInt()
        } else {
            Year = getCurrentYear()
            Month = getCurrentMonth() - 1
            Day = getCurrentDate()
        }
        val dialog = DatePickerDialog(
            context!!,
            { view, selectedYear, selectedMonth, selectedDay ->
                val Year: Int
                val Month: Int
                val Day: Int
                Year = selectedYear
                Month = selectedMonth
                Day = selectedDay
                val Year1: Int = getCurrentYear()
                val Month1: Int = getCurrentMonth() - 1
                val Day1: Int = getCurrentDate()
                var check = true
                if (Year > Year1) {
                    check = false
                } else {
                    if (Year == Year1) {
                        if (Month > Month1) {
                            check = false
                        } else {
                            if (Month == Month1) {
                                if (equaCurrentDate) {
                                    if (Day >= Day1) {
                                        check = false
                                    }
                                } else {
                                    if (Day > Day1) {
                                        check = false
                                    }
                                }
                            }
                        }
                    }
                }
                var lc = Calendar.getInstance()
                lc[Calendar.YEAR] = Year
                lc = Calendar.getInstance()
                lc[Calendar.YEAR] = Year
                lc[Calendar.MONTH] = Month
                lc[Calendar.DAY_OF_MONTH] = Day
                var datefornat = SimpleDateFormat(
                    "yyyyMMdd", Locale.getDefault()
                )
                datefornat = SimpleDateFormat(
                    "dd" + Character
                            + "MM" + Character + "yyyy",
                    Locale.getDefault()
                )
                actionSelectedDate(datefornat.format(lc.time))
            }, Year, Month, Day
        )
        if (isSetminDate) {
            val timeTo: Date? = minDate?.getDateDDMMYYYY("dd/MM/yyyy")
            val lcTo = Calendar.getInstance()
            lcTo.time = timeTo
            dialog.datePicker.minDate = lcTo.timeInMillis
        }
        if (isSetmaxDate) {
            val timeTo: Date? = maxDate?.getDateDDMMYYYY("dd/MM/yyyy")
            val lcTo = Calendar.getInstance()
            lcTo.time = timeTo
            dialog.datePicker.maxDate = lcTo.timeInMillis
        }
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
            colorButton
        )
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
            colorButton
        )
    }

    fun convertTimeToString(time: Long): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(time))
    }

    fun getCurrentYear(): Int {
        val cTime = Calendar.getInstance()
        cTime.timeZone = TimeZone.getTimeZone("GMT+7")
        return cTime[Calendar.YEAR]
    }

    fun getCurrentMonth(): Int {
        val cTime = Calendar.getInstance()
        return cTime[Calendar.MONTH] + 1
    }

    fun getCurrentDate(): Int {
        val cTime = Calendar.getInstance()
        cTime.timeZone = TimeZone.getTimeZone("GMT+7")
        return cTime[Calendar.DATE]
    }

    interface ActionSelectedDate {
        fun actionSelectedDate(date: String?)
    }

    fun String.getDateDDMMYYYY(format: String): Date {
        val sdf = SimpleDateFormat(
            format,
            Locale.getDefault()
        )
        return try {
            sdf.parse(this)
        } catch (e: ParseException) {
            Date()
        }
    }

    fun findDayBetweenTwoDates(
        fromDate: String,
        toDate: String
    ): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val mDate11 = dateFormat.parse(fromDate)
        val mDate22 = dateFormat.parse(toDate)
        val mDifference = kotlin.math.abs(mDate11.time - mDate22.time)
        return mDifference / (24 * 60 * 60 * 1000)
    }

    fun getCurrentTimeToString(format: String = "yyyyMMddHHmmss") =
        System.currentTimeMillis().toTimeString(format)

}
package com.psm.mytable.utils

import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*

class CalendarUtils {
    companion object {



        fun getDate(year: Int, month: Int, dayOfMonth: Int): Date {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month - 1)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calendar.set(Calendar.HOUR, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            return calendar.time
        }

        fun getDayOfYear(date: Date): Int {
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar.get(Calendar.YEAR)
        }

        fun getMonthMMM(date: Date): String {
            val df = SimpleDateFormat("MMM", Locale.US)
            return df.format(date)
        }

        fun getMonth(date: Date): Int {
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar.get(Calendar.MONTH) +1
        }

        fun getDayOfMonth(date: Date): Int {
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar.get(Calendar.DAY_OF_MONTH)
        }

        fun getDayOfWee(date: Date): String {
            val df = SimpleDateFormat("E", Locale.getDefault())
            return df.format(date)
        }

        fun getCurrentMonth(): Int {
            val calendar = Calendar.getInstance()
            return calendar.get(Calendar.MONTH) + 1
        }

        fun getCurrentYear(): Int {
            val calendar = Calendar.getInstance()
            return calendar.get(Calendar.YEAR)
        }
        fun getCurrentDay(): Int {
            val calendar = Calendar.getInstance()
            return calendar.get(Calendar.DAY_OF_MONTH)
        }

        fun getCurrentMonth(calendar: Calendar): Int {
            return calendar.get(Calendar.MONTH) + 1
        }

        fun getCurrentYear(calendar: Calendar): Int {
            return calendar.get(Calendar.YEAR)
        }

        fun getCurrentDay(calendar: Calendar): Int {
            return calendar.get(Calendar.DAY_OF_MONTH)
        }

        fun nextMonthYear(year: Int, month: Int): Array<Int>  {
            val cal = Calendar.getInstance()
            cal.time = getDate(year, month, 1)
            cal.add(Calendar.MONTH, 1)
            return arrayOf(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
        }

        fun previousMonthYear(year: Int, month: Int): Array<Int> {
            val cal = Calendar.getInstance()
            cal.time = getDate(year, month, 1)
            cal.add(Calendar.MONTH, -1)
            return arrayOf(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
        }

        fun addYear(date: Date, year: Int): Date  {
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.YEAR, year)
            return cal.time
        }


        fun equalDay(date1: Date, date2: Date): Boolean {
            val cal1 = Calendar.getInstance()
            cal1.time = date1

            val cal2 = Calendar.getInstance()
            cal2.time = date2

            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                    && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
        }

        fun equalMonth(dashDate: String, dashDate2: String): Boolean {
            return dashDate.length > 7 && dashDate2.length > 7 && dashDate.substring(0, 7) == dashDate2.substring(0, 7)
        }

        fun getMonthFirstSunday(date: Date): Date {
            val calendar = Calendar.getInstance()
            calendar.time = date

            calendar.set(Calendar.DAY_OF_MONTH, 1)

            // 요일
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            // 첫 번째 주에서 1일까지 간격
            val intervalDay = dayOfWeek - 1

            if(intervalDay > 0){
                calendar.add(Calendar.DATE, - intervalDay)
            }
            return calendar.time
        }

        fun getDDay(dashDate: String): String {
            return if(dashDate.isNotEmpty()){
                val now = System.currentTimeMillis()
                val date = Date(now)
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val nowDate = sdf.format(date)

                val date1 = sdf.parse(dashDate)
                val date2 = sdf.parse(nowDate)

                val diffSec = (date2.time - date1.time) / 1000
                val diffDays = (diffSec / (24 * 60 * 60))

                val sign = if(diffDays > 0){
                    "+"
                }else{
                    "-"
                }
                "$sign${kotlin.math.abs(diffDays)}"
            }else{
                ""
            }


        }
    }
}
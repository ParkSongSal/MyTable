package com.psm.mytable.utils

import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import com.psm.mytable.type.PhotoType
import com.psm.mytable.type.RecipeType
import com.psm.mytable.ui.dialog.DialogConstant
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DialogUtils {
    companion object {

        fun clickRecipeType(recipeType: RecipeType): Bundle {
            return bundleOf(
                DialogConstant.EVENT_CLICK to DialogConstant.POSITIVE_BUTTON,
                DialogConstant.RECIPE_TYPE to recipeType
            )
        }

        fun getRecipeType(bundle: Bundle): RecipeType{
            return bundle.getSerializable(DialogConstant.RECIPE_TYPE) as RecipeType
        }

        fun clickPhotoType(photoTYpe: PhotoType): Bundle {
            return bundleOf(
                DialogConstant.EVENT_CLICK to DialogConstant.POSITIVE_BUTTON,
                DialogConstant.PHOTO_TYPE to photoTYpe
            )
        }

        fun getPhotoType(bundle: Bundle): PhotoType{
            return bundle.getSerializable(DialogConstant.PHOTO_TYPE) as PhotoType
        }


        fun getYear(bundle: Bundle): Int {
            return bundle.getInt(DialogConstant.YEAR)
        }

        fun getMonth(bundle: Bundle): Int {
            return bundle.getInt(DialogConstant.MONTH)
        }

        fun getDay(bundle: Bundle): Int {
            return bundle.getInt(DialogConstant.DAY)
        }

        fun getHour(bundle: Bundle): Int {
            return bundle.getInt(DialogConstant.HOUR)
        }

        fun getMinute(bundle: Bundle): Int {
            return bundle.getInt(DialogConstant.MINUTE)
        }

        fun clickYearMonth(year: Int, month: Int): Bundle {
            return bundleOf(
                DialogConstant.EVENT_CLICK to DialogConstant.POSITIVE_BUTTON,
                DialogConstant.YEAR to year,
                DialogConstant.MONTH to month
            )
        }

        fun clickYearMonthDay(year: Int, month: Int, day: Int): Bundle {
            return bundleOf(
                DialogConstant.EVENT_CLICK to DialogConstant.POSITIVE_BUTTON,
                DialogConstant.YEAR to year,
                DialogConstant.MONTH to month,
                DialogConstant.DAY to day
            )
        }

        fun clickInputSchedule(
            text: String,
            year: Int,
            month: Int,
            day: Int,
            hour: Int,
            minute: Int
        ): Bundle {
            return bundleOf(
                DialogConstant.EVENT_CLICK to DialogConstant.POSITIVE_BUTTON,
                DialogConstant.INPUT_TEXT to text,
                DialogConstant.YEAR to year,
                DialogConstant.MONTH to month,
                DialogConstant.DAY to day,
                DialogConstant.HOUR to hour,
                DialogConstant.MINUTE to minute
            )
        }

        fun clickInputHealthData(
            text: String,
            year: Int,
            month: Int,
            day: Int,
            hour: Int,
            minute: Int
        ): Bundle {
            return bundleOf(
                DialogConstant.EVENT_CLICK to DialogConstant.POSITIVE_BUTTON,
                DialogConstant.INPUT_TEXT to text,
                DialogConstant.YEAR to year,
                DialogConstant.MONTH to month,
                DialogConstant.DAY to day,
                DialogConstant.HOUR to hour,
                DialogConstant.MINUTE to minute
            )
        }

        fun clickInputHealthChart(uri: Uri, text: String, text2: String): Bundle {
            return bundleOf(
                DialogConstant.EVENT_CLICK to DialogConstant.POSITIVE_BUTTON,
                DialogConstant.INPUT_URI to uri.toString(),
                DialogConstant.INPUT_TEXT to text,
                DialogConstant.INPUT_TEXT2 to text2
            )
        }

        fun clickInputHospitalAdd(hospitalName: String, address: String, telNum: String): Bundle {
            return bundleOf(
                DialogConstant.EVENT_CLICK to DialogConstant.POSITIVE_BUTTON,
                DialogConstant.INPUT_TEXT to hospitalName,
                DialogConstant.INPUT_TEXT2 to address,
                DialogConstant.INPUT_TEXT3 to telNum
            )
        }

        fun isHiddenChartClick(bundle: Bundle): Boolean {
            return bundle.getInt(DialogConstant.EVENT_CLICK) == DialogConstant.HIDDEN_CHART_BUTTON
        }

        fun isPositiveClick(bundle: Bundle): Boolean {
            return bundle.getInt(DialogConstant.EVENT_CLICK) == DialogConstant.POSITIVE_BUTTON
        }

        fun isEditClick(bundle: Bundle): Boolean {
            return bundle.getInt(DialogConstant.EVENT_CLICK) == DialogConstant.EDIT_BUTTON
        }

        fun isNegativeClick(bundle: Bundle): Boolean {
            return bundle.getInt(DialogConstant.EVENT_CLICK) == DialogConstant.NEGATIVE_BUTTON
        }

        fun isCloseClick(bundle: Bundle): Boolean {
            return bundle.getInt(DialogConstant.EVENT_CLICK) == DialogConstant.CLOSE_BUTTON
        }

        fun getInputUri(bundle: Bundle): Uri? {
            return Uri.parse(bundle.getString(DialogConstant.INPUT_URI))
        }

        fun getInputText(bundle: Bundle): String? {
            return bundle.getString(DialogConstant.INPUT_TEXT)
        }

        fun getInputText2(bundle: Bundle): String? {
            return bundle.getString(DialogConstant.INPUT_TEXT2)
        }

        fun getInputText3(bundle: Bundle): String? {
            return bundle.getString(DialogConstant.INPUT_TEXT3)
        }

        fun clickInputText(inputText: String): Bundle {
            return bundleOf(
                DialogConstant.EVENT_CLICK to DialogConstant.POSITIVE_BUTTON,
                DialogConstant.INPUT_TEXT to inputText
            )
        }

        fun clickPositive(): Bundle {
            return bundleOf(DialogConstant.EVENT_CLICK to DialogConstant.POSITIVE_BUTTON)
        }

        fun clickEdit(): Bundle {
            return bundleOf(DialogConstant.EVENT_CLICK to DialogConstant.EDIT_BUTTON)
        }

        fun clickNegative(): Bundle {
            return bundleOf(DialogConstant.EVENT_CLICK to DialogConstant.NEGATIVE_BUTTON)
        }

        fun clickClose(): Bundle {
            return bundleOf(DialogConstant.EVENT_CLICK to DialogConstant.CLOSE_BUTTON)
        }


        fun getRecommenderCode(bundle: Bundle): String? {
            return bundle.getString(DialogConstant.RECOMMENDER_CODE)
        }

        fun getDescriptionCode(bundle: Bundle): String? {
            return bundle.getString(DialogConstant.DESCRIPTION_CODE)
        }


        fun clickPositive(data: String): Bundle {
            return bundleOf(
                DialogConstant.EVENT_CLICK to DialogConstant.POSITIVE_BUTTON,
                DialogConstant.STRING_DATA to data
            )
        }

        fun getStringData(bundle: Bundle): String? {
            return bundle.getString(DialogConstant.STRING_DATA)
        }


        fun getDashDate(date: Date): String {
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return df.format(date)
        }

        fun getCurrentDashDate(): String {
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return df.format(Date())
        }

        fun getDotDate(date: Date): String {
            val df: DateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            return df.format(date)
        }

        fun getSlashDate(date: Date): String {
            val df: DateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            return df.format(date)
        }

        fun getCurrentDotDate(): String {
            val df: DateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            return df.format(Date())
        }
    }
}
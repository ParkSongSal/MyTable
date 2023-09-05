package com.psm.mytable.ui.ingredients.ingredientUpdate

import android.net.Uri
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.psm.mytable.BR
import com.psm.mytable.R
import timber.log.Timber

class IngredientUpdateViewData: BaseObservable() {

    var ingredientId: Long = -1


    // 수정 전 재료명
    var originalIngredientName: String = ""

    // 수정 전 개수
    var originalIngredientCount: String = ""

    // 수정전 보관 방법
    var originalStorage: String = ""

    var originalStorageType: Int = 0

    // 수정 전 유통(소비)기한
    var originalExpirationDate: String = ""

    var originalMemo: String = ""



    // 재료명
    @get:Bindable
    var ingredientName: String = ""
        set(value){
            field = value
            notifyPropertyChanged(BR.ingredientName)
            checkRequiredData()
        }

    // 개수
    @get:Bindable
    var ingredientCount: String = ""
        set(value){
            field = value
            notifyPropertyChanged(BR.ingredientCount)
            checkRequiredData()
        }

    // 보관방법 선택 (냉장, 냉동, 실온)
    @get:Bindable
    var checkedStorage:Int = R.id.rdoCold
        set(value) {
            field = value
            notifyPropertyChanged(BR.checkedStorage)
            checkRequiredData()
            setStorageData(value)
        }

    // 보관방법
    var storage: String = ""

    @get:Bindable
    var storageType : Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.storageType)
            checkRequiredData()
        }

    fun setStorageData(checkedValue : Int){
        when(checkedValue){
            R.id.rdoCold -> {
                storage = "냉장"
                storageType = 1
            }
            R.id.rdoFrozen -> {
                storage = "냉동"
                storageType = 2
            }
            R.id.rdoRoomTemperature -> {
                storage = "실온"
                storageType = 3
            }
        }
    }

    fun setStorage(storageType: Int){
        checkedStorage = when(storageType){
            1 -> {
                R.id.rdoCold
            }
            2 -> {
                R.id.rdoFrozen
            }
            3 -> {
                R.id.rdoRoomTemperature
            }
            else -> R.id.rdoCold
        }
    }

    // 유통(소비) 기한
    @get:Bindable
    var expirationDate: String = ""
        set(value){
            field = value
            notifyPropertyChanged(BR.expirationDate)
            checkRequiredData()
        }

    // 메모
    @get:Bindable
    var memo: String = ""
        set(value){
            field = value
            notifyPropertyChanged(BR.memo)
            checkRequiredData()
        }

    @get:Bindable
    var requiredDataComplete: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.requiredDataComplete)
        }

    /**
     * 필수값 체크
     * ingredientName : 재료명
     * ingredientCount : 개수
     * checkedStorage : 보관방법
     * expirationDate : 유통(소비)기한
     * */
    private fun checkRequiredData() {
        requiredDataComplete = (originalIngredientName != ingredientName && ingredientName.isNotEmpty()) ||
                (originalIngredientCount != ingredientCount && ingredientCount.isNotEmpty()) ||
                (originalStorageType != storageType) ||
                (originalExpirationDate != expirationDate && expirationDate.isNotEmpty()) ||
                (originalMemo != memo)
    }

}
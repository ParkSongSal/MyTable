package com.psm.mytable.ui.ingredients.ingredientsAdd

import android.net.Uri
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.psm.mytable.BR
import com.psm.mytable.R
import timber.log.Timber

class IngredientViewData: BaseObservable() {

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

    var storageType : Int = 0

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
        requiredDataComplete = ingredientName.isNotEmpty() &&
                ingredientCount.isNotEmpty()&&
                checkedStorage > 0 &&
                expirationDate.isNotEmpty()
    }

}
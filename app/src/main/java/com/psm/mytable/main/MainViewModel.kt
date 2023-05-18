package com.psm.mytable.main

import androidx.lifecycle.ViewModel
import com.psm.mytable.room.MyTableRepository

/**
 * 인트로 화면 노출 및 앱 초기화
 * [앱 초기화]
 * - 클라이언트 ID 정보가 없는 경우 받오온다.
 * - FCM 토큰 등록이 안되 있으면 등록 한다.
 * - 로그인 사용자, 비 로그인 사용자에 해당되는 앱 초기화 완료 이벤트를 전달한다.
 * - 버전 체크 (강제 또는 선택 업데이트 알럿 노출)
 */
class MainViewModel(
    private val repository: MyTableRepository
) : ViewModel(){


}
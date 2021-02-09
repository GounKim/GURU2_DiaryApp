package com.example.guru2_diaryapp

//테스트용 더미 데이터를 생성하는 클래스입니다. 개발이 끝나면 삭제할게요.
class MakeDummyData{



    var categorys = ArrayList<String>()

    //name카테고리를 생성한다. 같은 이름의 카테고리가 있을 경우 생성하지 않고 false 를 반환한다.
    public fun addCategory(name:String): Boolean {

        return true
    }

    //아이디가 id인 카티고리를 삭제한다.
    fun deleteCategory(id:Int) {

    }

    //일기를 diary_posts 에 저장한다.
    //date: 작성날짜 , category_id: 카테고리 아이디 , weather: 날씨 , content: 내용, imgs 이미지 정보
    //같은 날짜, 동일한 카테고리에 글이 있을 경우 처리하지 않고 false 를 반환한다.
    fun writePostThisDay_Category(date:Int,
                                  category_id:Int,
                                  weather:Int,
                                  content:String,
                                  imgs:ArrayList<String>): Boolean {

        return true
    }

    // 아이디 값이 id인 일기를 삭제한다. (첨부된 사진들도 같이 삭제된다)
    // 해당하는 일기가 없다면 처리하지 않고 false를 반환한다.
    fun DeletePost(id:Int): Boolean {

        return true
    }

}
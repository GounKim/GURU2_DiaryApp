package com.example.guru2_diaryapp

//테이블에서 불러온 데이터들을 처리하기 위한 클래스
class DiaryData {

    var reporting_date:Int = 0  //날짜
    var category_name:String = ""    //카테고리(join한 뒤 저장된 아이디로 실제 명칭 찾아옴)
    var content:String = ""     //글 본문
    var pic_count:ArrayList<String> = ArrayList(10)

        constructor(){

        }

        constructor(date:Int, category:String, content:String){
            this.reporting_date = date
            this.category_name = category
            this.content =  content
        }
}
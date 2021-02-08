package com.example.guru2_diaryapp

//테이블에서 불러온 데이터들을 처리하기 위한 클래스
class DiaryData {

    var post_id:Int = 0         //글번호
    var reporting_date:Int = 0  //날짜
    var category:String = ""    //카테고리(join한 뒤 저장된 아이디로 실제 명칭 찾아옴)
    var content:String = ""     //글 본문
    var pic_count:Int = 0       //첨부된 사진이 있다면 그 개수 저장

        constructor(){

        }

        constructor(post_id:Int, date:Int, category:String, content:String){
            this.post_id = post_id
            this.reporting_date = date
            this.category = category
            this.content =  content
        }
}
package com.back;

import lombok.AllArgsConstructor;

@AllArgsConstructor // 모든 필드 생성자
public class WiseSaying {
    private int id;
    private String content;
    private String author;

    public int getId() {
        // 같은 이름의 지역 변수나 매개변수가 없으면 this 생략 가능
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
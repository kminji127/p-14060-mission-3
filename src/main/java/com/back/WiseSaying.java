package com.back;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // 모든 필드 생성자
public class WiseSaying {
    private int id;
    private String content;
    private String author;
}
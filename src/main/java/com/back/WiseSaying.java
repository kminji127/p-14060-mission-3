package com.back;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor  // 모든 필드 생성자
//@NoArgsConstructor  // 인자 없는 생성자 (final 불가)
@RequiredArgsConstructor // 필수 생성자 자동 생성 (final, @NotNull 필드)
public class WiseSaying {
    private final int id; // 변경 불가하므로 final 추가 -> 생성자에서 반드시 초기화해야 함
    private String content;
    private String author;
}
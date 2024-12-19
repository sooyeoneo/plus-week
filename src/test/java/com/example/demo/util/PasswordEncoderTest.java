package com.example.demo.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PasswordEncoderTest {

    // encode 테스트
    @Test
    public void testEncode() {
        String rawPassword = "abcd123456";

        // 암호화된 비밀번호
        String encodedPassword = PasswordEncoder.encode(rawPassword);

        // 암호화된 비밀번호가 원래 비밀번호와 다른지 확인
        assertNotEquals(rawPassword, encodedPassword);
    }

    @Test
    public void testMatches() {
        String rawPassword = "abcd123456";

        String encodedPassword = PasswordEncoder.encode(rawPassword);

        // matches 가 true 를 반환
        assertTrue(PasswordEncoder.matches(rawPassword, encodedPassword));

        // 잘못된 비밀번호로 비교시 false 반환
        assertFalse(PasswordEncoder.matches("wrongPassword", encodedPassword));
    }

}

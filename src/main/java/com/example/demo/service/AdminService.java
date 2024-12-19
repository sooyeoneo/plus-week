package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // TODO: 4. find or save 예제 개선
    // 사용자 신고 기능: 사용자 상태를 'BLOCKED' 로 일괄 변경
    @Transactional
    public void reportUsers(List<Long> userIds) {
        // 제공된 사용자 Id로 사용자 데이터 쿼리 한 번으로 조회
        List<User> users = userRepository.findAllById(userIds);

        // 조회된 사용자 수가 요청된 Id 수와 다르면 예외 처리
        if (users.size() != userIds.size()) {
            throw new IllegalArgumentException("사용자 Id에 맞는 값이 존재하지 않습니다.");
        }

        // JPQL 을 사용하여 사용자 상태를 'BLOCKED' 일괄 변경 - DB 접근 최소화를 위해 update 쿼리 실행
        userRepository.updateStatusToBlockedForUsers(userIds);

        // saveAll 을 호출하지 않아도 @Transactional 에 의해 변경사항 반영
        // 기존 문제: findById() 사용자 데이터 매번 조회, save() 매번 사용자 데이터 저장 -> 데이터 양이 많아질 수록 쿼리수가 (조회+저장)2*N
    }
}

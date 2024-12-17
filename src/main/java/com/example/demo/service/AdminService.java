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
    @Transactional
    public void reportUsers(List<Long> userIds) {
        // 쿼리 한 번으로 모든 사용자 조회
        List<User> users = userRepository.findAllById(userIds);

        if (users.size() != userIds.size()) {
            throw new IllegalArgumentException("일부 사용자 Id에 맞는 값이 존재하지 않습니다.");
        }

        // 상태를 변경
        users.forEach((User::updateStatusToBlocked));

        // saveAll 을 호출하지 않아도 @Transactional 에 의해 변경사항 반영
        // userRepository.saveAll(users); 필요시 명시적 호출

        // 기존 문제: findById() 사용자 데이터 매번 조회, save() 매번 사용자 데이터 저장 -> 데이터 양이 많아질 수록 쿼리수가 (조회+저장)2*N
    }
}

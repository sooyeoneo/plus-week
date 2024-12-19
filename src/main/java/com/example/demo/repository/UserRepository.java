package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    // JPQL 을 사용하여 특정 Id 리스트에 해당하는 사용자 상태 'BLOCKED' 로 일괄 변경
    @Modifying
    @Query("UPDATE User u SET u.status = 'BLOCKED' WHERE u.id IN :userIds")
    void updateStatusToBlockedForUsers(@Param("userIds")List<Long> userIds);

    default User findUserById(Long id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("Id에 맞는 값이 존재하지 않습니다."));
    }
}

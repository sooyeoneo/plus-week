package com.example.demo.repository;

import com.example.demo.entity.QItem;
import com.example.demo.entity.QReservation;
import com.example.demo.entity.QUser;
import com.example.demo.entity.Reservation;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Reservation> searchReservations(Long userId, Long itemId) {

        // Q타입 정의 (QueryDSL 에서 생성된 Q 클래스)
        QUser user = QUser.user;
        QItem item = QItem.item;
        QReservation reservation = QReservation.reservation;

        // 조건을 동적으로 추가하는 BooleanBuilder
        BooleanBuilder builder = new BooleanBuilder();

        if (userId != null) {
            builder.and(reservation.user.id.eq(userId));
        }

        if (itemId != null) {
            builder.and(reservation.item.id.eq(itemId));
        }

        // QueryDSL 을 사용해 쿼리 생성
        return queryFactory
                .selectFrom(reservation)
                .leftJoin(reservation.user, user).fetchJoin()
                .leftJoin(reservation.item, item).fetchJoin()
                .where(builder) // 조건에 맞는 데이터 필터링
                .fetch(); // 결과 반환
    }
}

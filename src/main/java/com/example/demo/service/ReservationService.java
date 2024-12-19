package com.example.demo.service;

import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.Item;
import com.example.demo.entity.RentalLog;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationStatus;
import com.example.demo.entity.User;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RentalLogService rentalLogService;

    public ReservationService(ReservationRepository reservationRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository,
                              RentalLogService rentalLogService) {
        this.reservationRepository = reservationRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.rentalLogService = rentalLogService;
    }

    // TODO: 1. 트랜잭션 이해
    /*
     * @Transactional 선언으로 메서드 내 모든 작업이 하나의 트랜잭션으로 묶임.
     * 에러 발생 시 트랜잭션 롤백, 정상 수행 시 트랜잭션 커밋.
     */
    @Transactional // All or Nothing 동작을 보장하기위해 @Transactional 어노테이션 사용
    public ReservationResponseDto createReservation(Long itemId, Long userId, LocalDateTime startAt, LocalDateTime endAt) {

        Item item = itemRepository.findItemById(itemId);
        User user = userRepository.findUserById(userId);

        Reservation reservation = new Reservation(item, user, ReservationStatus.PENDING, startAt, endAt);
        Reservation savedReservation = reservationRepository.save(reservation);

        rentalLogService.save(new RentalLog(savedReservation, "Reservation created", RentalLog.LogType.SUCCESS));

        return new ReservationResponseDto(savedReservation);
    }

    // TODO: 3. N+1 문제
    // Fetch Join 을 사용해 reservation, user, item 데이터 한 번에 가져오기
    public List<ReservationResponseDto> getReservations() {

        List<Reservation> reservations = reservationRepository.findAllWithUserAndItem();

        return reservations.stream()
                .map(reservation -> new ReservationResponseDto(
                reservation.getId(),
                reservation.getUser().getNickname(),
                reservation.getItem().getName(),
                reservation.getStartAt(),
                reservation.getEndAt()
        )).toList();
    }

    // TODO: 5. QueryDSL 검색 개선
    // QueryDSL 을 활용해 사용자와 아이템 ID에 맞는 예약 조회
    public List<ReservationResponseDto> searchAndConvertReservations(Long userId, Long itemId) {

        List<Reservation> reservations = reservationRepository.searchReservations(userId, itemId);

        return convertToDto(reservations);
    }

    private List<ReservationResponseDto> convertToDto(List<Reservation> reservations) {
        return reservations.stream()
                .map(reservation -> new ReservationResponseDto(
                        reservation.getId(),
                        reservation.getUser().getNickname(),
                        reservation.getItem().getName(),
                        reservation.getStartAt(),
                        reservation.getEndAt()
                ))
                .toList();
    }

    // TODO: 7. 리팩토링
    @Transactional
    public ReservationResponseDto updateReservationStatus(Long reservationId, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 데이터가 존재하지 않습니다."));

        // 예약 상태에 맞게 상태 업데이트
        switch (status) {
            case APPROVED :
                if (reservation.getStatus() != ReservationStatus.PENDING) {
                    throw new IllegalArgumentException("PENDING 상태만 APPROVED로 변경 가능합니다.");
                }
                reservation.updateStatus(ReservationStatus.APPROVED);
                break;

            case CANCELED:
                if (reservation.getStatus() != ReservationStatus.EXPIRED) {
                    throw new IllegalArgumentException("EXPIRED 상태인 예약은 취소할 수 없습니다.");
                }
                reservation.updateStatus(ReservationStatus.CANCELED);
                break;

            case EXPIRED:
                if (reservation.getStatus() != ReservationStatus.PENDING) {
                    throw new IllegalArgumentException("PENDING 상태만 EXPIRED로 변경 가능합니다.");
                }
                reservation.updateStatus(ReservationStatus.EXPIRED);
                break;

            default:
                throw new IllegalArgumentException("올바르지 않은 상태: " + status);
        }

        reservation.updateStatus(status);
        return new ReservationResponseDto(reservation);
    }
}

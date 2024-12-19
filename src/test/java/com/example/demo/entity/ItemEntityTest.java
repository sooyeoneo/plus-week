package com.example.demo.entity;

import com.example.demo.repository.ItemRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ItemEntityTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @Transactional
    public void testItemStatusNotNull() {

        // Item 객체 생성 시 status 는 기본값으로 'PENDING' 설정
        Item item = new Item("Test Item", "Description", new User(), new User());

        // Item 저장
        Item savedItem = itemRepository.save(item);

        // 저장된 Item 의 status 값 확인
        assertNotNull(savedItem.getStatus(), " Item status 는 null 이 아니어야 합니다.");
        assertEquals(Item.ItemStatus.PENDING, savedItem.getStatus(), "기본값은 'PENDING' 이어야 합니다.");
    }

    @Test
    public void testItemStatusNotNullable() {

        Item item = new Item("Test Item", "Description", new User(), new User());

        // Item 저장 시 status 는 기본값으로 'PENDING' 으로 설정
        assertThrows(ConstraintViolationException.class, () -> {
            itemRepository.save(item); // status 가 null 이면 예외가 발생해야 함
        });
    }
}

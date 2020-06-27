package com.heungyeol.homework.service.spray;

import com.heungyeol.homework.controller.spray.ReturnDto;
import com.heungyeol.homework.controller.spray.SprayDto;
import com.heungyeol.homework.controller.spray.TokenDto;
import com.heungyeol.homework.repository.spray.ReceiverInfo;
import com.heungyeol.homework.repository.spray.Spray;
import com.heungyeol.homework.repository.spray.SprayRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestConstructor;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class SprayServiceTest {
    private final SprayService sprayService;

    public SprayServiceTest(SprayService sprayService) {
        this.sprayService = sprayService;
    }

    @MockBean
    private SprayRepository sprayRepository;

    @ParameterizedTest
    @MethodSource("getSprayDto")
    public void request(SprayDto dto) {
        String token = sprayService.request(dto);
        assertTrue(StringUtils.isNotEmpty(token));
    }

    static Stream<Arguments> getSprayDto() {
        SprayDto dto = new SprayDto(1000L, 3);
        dto.setHeaderDto("TEST", "ROOM1");
        return Stream.of(
                Arguments.arguments(dto)
        );
    }

    private Date searchDate(int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(field, amount);
        return calendar.getTime();
    }

    private Spray testSpray(String roomId, String token) {
        Spray spray = new Spray();
        spray.setOwnerId("OWNER");
        spray.setRoomId(roomId);
        spray.setPerson(3);
        spray.setAmount(1000L);
        spray.setExpired(false);
        spray.setCreatedAt(searchDate(Calendar.MINUTE, -5));
        spray.setToken(token);

        List<ReceiverInfo> receiverInfoList = new ArrayList<>();

        ReceiverInfo receiverInfo1 = new ReceiverInfo();
        receiverInfo1.setAmount(355L);
        ReceiverInfo receiverInfo2 = new ReceiverInfo();
        receiverInfo2.setAmount(511L);
        ReceiverInfo receiverInfo3 = new ReceiverInfo();
        receiverInfo3.setAmount(134L);
        receiverInfoList.add(receiverInfo1);
        receiverInfoList.add(receiverInfo2);
        receiverInfoList.add(receiverInfo3);

        spray.setReceiverInfoList(receiverInfoList);
        return spray;
    }

    @ParameterizedTest
    @MethodSource("getTokenDto")
    public void receive(TokenDto tokenDto) {
        Spray spray = testSpray(tokenDto.getRoomId(), tokenDto.getToken());
        doReturn(spray).when(sprayRepository).findByTokenAndCreatedAtAfterAndRoomId(eq(tokenDto.getToken()), any(), eq(tokenDto.getRoomId()));
        Long receivedAmount = sprayService.receive(tokenDto);
        assertEquals(spray.getReceiverInfoList().iterator().next().getAmount(), receivedAmount);
    }

    @ParameterizedTest
    @MethodSource("getTokenDto")
    public void receive_받을돈_없음(TokenDto tokenDto) {
        doReturn(null).when(sprayRepository).findByTokenAndCreatedAtAfterAndRoomId(tokenDto.getToken(), searchDate(Calendar.MINUTE, -15), tokenDto.getRoomId());

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            sprayService.receive(tokenDto);
        });
        assertEquals(exception.getMessage(), "받을 돈이 존재하지 않습니다.");
    }

    @ParameterizedTest
    @MethodSource("getTokenDto")
    public void receive_뿌린사람(TokenDto tokenDto) {
        Spray spray = testSpray(tokenDto.getRoomId(), tokenDto.getToken());
        spray.setOwnerId(tokenDto.getUserId());
        doReturn(spray).when(sprayRepository).findByTokenAndCreatedAtAfterAndRoomId(eq(tokenDto.getToken()), any(), eq(tokenDto.getRoomId()));
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            sprayService.receive(tokenDto);
        });
        assertEquals(exception.getMessage(), "뿌린 사람은 돈을 받을 수 없습니다.");
    }

    @ParameterizedTest
    @MethodSource("getTokenDto")
    public void receive_이미받음(TokenDto tokenDto) {
        Spray spray = testSpray(tokenDto.getRoomId(), tokenDto.getToken());
        spray.getReceiverInfoList().get(0).setPayedBy(tokenDto.getUserId());
        doReturn(spray).when(sprayRepository).findByTokenAndCreatedAtAfterAndRoomId(eq(tokenDto.getToken()), any(), eq(tokenDto.getRoomId()));
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            sprayService.receive(tokenDto);
        });
        assertEquals(exception.getMessage(), "이미 받았습니다.");
    }

    @ParameterizedTest
    @MethodSource("getTokenDto")
    public void receive_이미_모두_받음(TokenDto tokenDto) {
        Spray spray = testSpray(tokenDto.getRoomId(), tokenDto.getToken());
        for (ReceiverInfo receiverInfo : spray.getReceiverInfoList()) {
            receiverInfo.setPayedBy("T");
        }

        doReturn(spray).when(sprayRepository).findByTokenAndCreatedAtAfterAndRoomId(eq(tokenDto.getToken()), any(), eq(tokenDto.getRoomId()));
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            sprayService.receive(tokenDto);
        });
        assertEquals(exception.getMessage(), "이미 모두 받아갔습니다.");
    }


    @ParameterizedTest
    @MethodSource("getTokenDto")
    public void inquire(TokenDto tokenDto) {
        tokenDto.setUserId("OWNER");
        Spray spray = testSpray(tokenDto.getRoomId(), tokenDto.getToken());
        doReturn(spray).when(sprayRepository).findByTokenAndCreatedAtAfterAndOwnerIdAndRoomId(eq(tokenDto.getToken()), any(),eq("OWNER"), eq(tokenDto.getRoomId()));
        ReturnDto dto = sprayService.inquire(tokenDto);
        assertEquals(spray.getAmount(), dto.getAmount());
    }

    @ParameterizedTest
    @MethodSource("getTokenDto")
    public void inquire_조회할_데이터없음(TokenDto tokenDto) {
        tokenDto.setUserId("OWNER");
        Spray spray = testSpray(tokenDto.getRoomId(), tokenDto.getToken());
        doReturn(null).when(sprayRepository).findByTokenAndCreatedAtAfterAndOwnerIdAndRoomId(eq(tokenDto.getToken()), any(),eq("OWNER"), eq(tokenDto.getRoomId()));

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
           sprayService.inquire(tokenDto);
        });
        assertEquals(exception.getMessage(), "조회할 데이터가 존재하지 않습니다.");
    }

    static Stream<Arguments> getTokenDto() {
        TokenDto dto = new TokenDto("ABC");
        dto.setHeaderDto("TEST", "ROOM1");
        return Stream.of(
                Arguments.arguments(dto)
        );
    }
}
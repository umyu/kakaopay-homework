package com.heungyeol.homework.service.spray;

import com.google.common.base.Preconditions;
import com.heungyeol.homework.controller.spray.FinishDto;
import com.heungyeol.homework.controller.spray.ReturnDto;
import com.heungyeol.homework.controller.spray.SprayDto;
import com.heungyeol.homework.controller.spray.TokenDto;
import com.heungyeol.homework.repository.spray.ReceiverInfo;
import com.heungyeol.homework.repository.spray.ReceiverInfoRepository;
import com.heungyeol.homework.repository.spray.Spray;
import com.heungyeol.homework.repository.spray.SprayRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.Well512a;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SprayService {
    private final SprayRepository sprayRepository;
    private final ReceiverInfoRepository receiverInfoRepository;

    private Well512a well = new Well512a();

    @Autowired
    public SprayService(SprayRepository sprayRepository, ReceiverInfoRepository receiverInfoRepository) {
        this.sprayRepository = sprayRepository;
        this.receiverInfoRepository = receiverInfoRepository;
    }

    private Date searchDate(int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(field, amount);
        return calendar.getTime();
    }

    public String request(SprayDto dto) {
        Integer person = dto.getPerson();
        Long remain = dto.getAmount();

        String token = "";

        //필요하면 활성화된 토큰 수를 10*25*25 갯수 이하만 생성되게 할 것.

        for (int i = 0; i < 3; i++) {
            token = tokenMaker();
            Spray existSpray = sprayRepository.findByTokenAndCreatedAtAfter(token, searchDate(Calendar.MINUTE, -10));
            if (existSpray == null) {
                break;
            }
        }

        Preconditions.checkArgument(StringUtils.isNotEmpty(token), "토큰이 생성되지 않았습니다. 다시 한번 시도하시기 바랍니다.");
        Spray spray = new Spray();
        spray.setAmount(dto.getAmount());
        spray.setPerson(dto.getPerson());
        spray.setOwnerId(dto.getUserId());
        spray.setRoomId(dto.getRoomId());
        spray.setToken(token);
        spray.setCreatedAt(new Date());

        Spray saved = sprayRepository.save(spray);

        Set<Long> remainSet = new HashSet<>();

        do {
            long amount = well.nextLong(remain);
            if (!remainSet.contains(amount) && amount > person) {
                remainSet.add(amount);
                if (amount > remain) {
                    remain = amount - remain;
                } else {
                    remain = remain - amount;
                }
                person--;
            }
        } while (person > 1);

        remainSet.add(remain);

        List<ReceiverInfo> receiverInfoList = remainSet.stream().map(data -> {
            ReceiverInfo receiverInfo = new ReceiverInfo();
            receiverInfo.setSpray(saved);
            receiverInfo.setAmount(data);
            return receiverInfo;
        }).collect(Collectors.toList());
        receiverInfoRepository.saveAll(receiverInfoList);

        return token;
    }

    private String tokenMaker() {
        String token = "";
        for (int i = 0; i < 3; i++) {
            int index = well.nextInt(2) + 1;
            switch (index) {
                case 0://숫자
                    int first = well.nextInt(9) + 48;
                    token += String.valueOf((char) first);
                    break;
                case 1://대문자
                    int second = well.nextInt(25) + 65;
                    token += String.valueOf((char) second);
                    break;
                case 2://소문자
                    int third = well.nextInt(25) + 97;
                    token += String.valueOf((char) third);
                    break;
            }
        }
        Preconditions.checkArgument(StringUtils.isNotEmpty(token), "TOKEN이 만들어지지 않았습니다.");
        return token;
    }

    public Long receive(TokenDto tokenDto) {
        String token = tokenDto.getToken();
        Spray spray = sprayRepository.findByTokenAndCreatedAtAfterAndRoomId(token, searchDate(Calendar.MINUTE, -10), tokenDto.getRoomId());
        Preconditions.checkArgument(spray != null, "받을 돈이 존재하지 않습니다.");

        Preconditions.checkArgument(!spray.getOwnerId().equals(tokenDto.getUserId()), "뿌린 사람은 돈을 받을 수 없습니다.");

        List<ReceiverInfo> receiverInfoList = spray.getReceiverInfoList();
        Optional<ReceiverInfo> optReceiverInfoByReceive = receiverInfoList.stream().filter(data -> tokenDto.getUserId().equals(data.getPayedBy())).findFirst();
        Preconditions.checkArgument(!optReceiverInfoByReceive.isPresent(), "이미 받았습니다.");

        Optional<ReceiverInfo> optReceiverInfo = receiverInfoList.stream().filter(data -> StringUtils.isEmpty(data.getPayedBy())).findFirst();
        Preconditions.checkArgument(optReceiverInfo.isPresent(), "이미 모두 받아갔습니다.");

        ReceiverInfo receiverInfo = optReceiverInfo.get();
        receiverInfo.setPayed(true);
        receiverInfo.setPayedAt(new Date());
        receiverInfo.setPayedBy(tokenDto.getUserId());

        receiverInfoRepository.save(receiverInfo);

        boolean allMatch = receiverInfoList.stream().allMatch(data -> data.getPayed());
        if (allMatch) {
            spray.setExpired(true);
            sprayRepository.save(spray);
        }

        return receiverInfo.getAmount();
    }

    public ReturnDto inquire(TokenDto tokenDto) {
        Spray spray = sprayRepository.findByTokenAndCreatedAtAfterAndOwnerIdAndRoomId(tokenDto.getToken(), searchDate(Calendar.DATE, -7), tokenDto.getUserId(), tokenDto.getRoomId());
        Preconditions.checkArgument(spray != null, "조회할 데이터가 존재하지 않습니다.");
        List<FinishDto> finishDtoList = new ArrayList<>();
        Long receivedAmount = 0L;
        for (ReceiverInfo receiverInfo : spray.getReceiverInfoList()) {
            if (receiverInfo.getPayed()) {
                receivedAmount += receiverInfo.getAmount();
                finishDtoList.add(new FinishDto(receiverInfo.getAmount(), receiverInfo.getPayedBy()));
            }
        }
        ReturnDto returnDto = new ReturnDto(spray.getCreatedAt(), spray.getAmount(), receivedAmount, finishDtoList);
        return returnDto;
    }
}

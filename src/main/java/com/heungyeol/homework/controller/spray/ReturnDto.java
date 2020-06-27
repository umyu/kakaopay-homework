package com.heungyeol.homework.controller.spray;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnDto {
    private Date sprayedAt;
    private Long amount;
    private Long receivedAmount;
    private List<FinishDto> finishList;
}

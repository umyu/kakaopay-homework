package com.heungyeol.homework.controller.spray;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeaderDto {
    private String userId;
    private String roomId;


    public void setHeaderDto(String userId, String roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }
}

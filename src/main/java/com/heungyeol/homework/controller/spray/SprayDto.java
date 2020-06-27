package com.heungyeol.homework.controller.spray;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SprayDto extends HeaderDto {
    private Long amount;
    private Integer person;
}

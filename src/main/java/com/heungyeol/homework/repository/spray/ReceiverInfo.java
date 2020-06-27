package com.heungyeol.homework.repository.spray;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class ReceiverInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Spray.class)
    @JoinColumn(name="sprayId")
    private Spray spray;

    private Long amount;

    private Boolean payed = false;

    private String payedBy;

    private Date payedAt;

}

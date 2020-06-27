package com.heungyeol.homework.repository.spray;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Where(clause = "expired = false" )
public class Spray {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ownerId;

    private String roomId;

    private Integer person;

    private Long amount;

    private Boolean expired = false;

    private Date createdAt;


    private String token;

    @OneToMany(mappedBy = "spray")
    private List<ReceiverInfo> receiverInfoList;





}

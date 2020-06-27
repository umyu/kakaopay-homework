package com.heungyeol.homework.repository.spray;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface SprayRepository  extends JpaRepository<Spray, Long> {
    Spray findByTokenAndCreatedAtAfterAndRoomId(String token, Date searchDate, String roomId);
    Spray findByTokenAndCreatedAtAfter(String token, Date searchDate);
    Spray findByTokenAndCreatedAtAfterAndOwnerIdAndRoomId(String token, Date searchDate, String owner, String roomId);
}

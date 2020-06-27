package com.heungyeol.homework.repository.spray;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiverInfoRepository extends JpaRepository<ReceiverInfo, Long> {
}

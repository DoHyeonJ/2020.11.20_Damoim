package com.damoim.club;

import com.damoim.domain.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ClubRepository extends JpaRepository<Club, Long> {

    boolean existsByPath(String path);
}

package com.damoim.modules.club;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ClubRepositoryExtension {

    List<Club> findByKeyword(String keyword);

}

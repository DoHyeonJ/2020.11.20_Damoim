package com.damoim.modules.club;

import com.damoim.modules.account.QAccount;
import com.damoim.modules.tag.QTag;
import com.damoim.modules.zone.QZone;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ClubRepositoryExtensionImpl extends QuerydslRepositorySupport implements ClubRepositoryExtension {

    public ClubRepositoryExtensionImpl() {
        super(Club.class);
    }

    @Override
    public Page<Club> findByKeyword(String keyword, Pageable pageable) {
        QClub club = QClub.club;
        JPQLQuery<Club> query = from(club).where(club.published.isTrue()
                .and(club.title.containsIgnoreCase(keyword))
                .or(club.tags.any().title.containsIgnoreCase(keyword))
                .or(club.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .leftJoin(club.tags, QTag.tag).fetchJoin()
                .leftJoin(club.zones, QZone.zone).fetchJoin()
                .distinct();
        JPQLQuery<Club> pagebleQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Club> fetchResults = pagebleQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }

}

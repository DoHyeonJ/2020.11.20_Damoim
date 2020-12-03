package com.damoim.modules.club;

import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ClubRepositoryExtensionImpl extends QuerydslRepositorySupport implements ClubRepositoryExtension {

    public ClubRepositoryExtensionImpl() {
        super(Club.class);
    }

    @Override
    public List<Club> findByKeyword(String keyword) {
        QClub club = QClub.club;
        JPQLQuery<Club> query = from(club).where(club.published.isTrue()
                .and(club.title.containsIgnoreCase(keyword))
                .or(club.tags.any().title.containsIgnoreCase(keyword))
                .or(club.zones.any().localNameOfCity.containsIgnoreCase(keyword)));
        return query.fetch();
    }

}

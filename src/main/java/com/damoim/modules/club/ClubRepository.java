package com.damoim.modules.club;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ClubRepository extends JpaRepository<Club, Long> {

    boolean existsByPath(String path);

    @EntityGraph(value = "Club.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Club findByPath(String path);

    @EntityGraph(value = "Club.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Club findClubWithTagsByPath(String path);

    @EntityGraph(value = "Club.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Club findClubWithZonesByPath(String path);

    @EntityGraph(value = "Club.withManagers", type = EntityGraph.EntityGraphType.FETCH)
    Club findClubWithManagersByPath(String path);

    @EntityGraph(value = "Club.withMembers", type = EntityGraph.EntityGraphType.FETCH)
    Club findClubWithMembersByPath(String path);

    Club findClubOnlyByPath(String path);

    @EntityGraph(value = "Club.withTagsAndZones", type = EntityGraph.EntityGraphType.FETCH)
    Club findClubWithTagsAndZonesById(Long id);
}

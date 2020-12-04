package com.damoim.modules.club;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ClubRepository extends JpaRepository<Club, Long>, ClubRepositoryExtension {

    boolean existsByPath(String path);

    @EntityGraph(attributePaths = {"tags", "zones", "managers", "members"}, type = EntityGraph.EntityGraphType.LOAD)
    Club findByPath(String path);

    @EntityGraph(attributePaths = {"tags", "managers"})
    Club findClubWithTagsByPath(String path);

    @EntityGraph(attributePaths = {"zones", "managers"})
    Club findClubWithZonesByPath(String path);

    @EntityGraph(attributePaths = "managers")
    Club findClubWithManagersByPath(String path);

    @EntityGraph(attributePaths = "members")
    Club findClubWithMembersByPath(String path);

    Club findClubOnlyByPath(String path);

    @EntityGraph(attributePaths = {"zones", "tags"})
    Club findClubWithTagsAndZonesById(Long id);

    @EntityGraph(attributePaths = {"members", "managers"})
    Club findClubWithManagersAndMembersById(Long id);

    List<Club> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);
}

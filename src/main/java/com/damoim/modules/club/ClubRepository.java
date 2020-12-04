package com.damoim.modules.club;

import com.damoim.modules.account.Account;
import com.damoim.modules.tag.Tag;
import com.damoim.modules.zone.Zone;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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

    @EntityGraph(attributePaths = {"zones", "tags"})
    List<Club> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);

    List<Club> findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Club> findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Club> findAllByManagersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Club> findAllByMembersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);
}

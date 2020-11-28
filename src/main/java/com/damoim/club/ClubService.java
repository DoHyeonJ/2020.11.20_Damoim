package com.damoim.club;

import com.damoim.club.form.ClubDescriptionForm;
import com.damoim.domain.Account;
import com.damoim.domain.Club;
import com.damoim.domain.Tag;
import com.damoim.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.damoim.club.form.ClubForm.VALID_PATH_PATTERN;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ModelMapper modelMapper;

    private void checkIfManager(Account account, Club club) {
        if (!account.isManagerOf(club)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    private void checkIfExistingClub(String path, Club club) {
        if (club == null) {
            throw new IllegalArgumentException(path + "에 해당하는 동호회가 없습니다.");
        }
    }

    public Club createNewClub(Club club, Account account) {
        Club newClub = clubRepository.save(club);
        newClub.addManager(account);
        return newClub;
    }

    public Club getClub(String path) {
        Club club = this.clubRepository.findByPath(path);
        checkIfExistingClub(path, club);
        return club;
    }

    public void updateClubDescription(Club club, ClubDescriptionForm clubDescriptionForm) {
        modelMapper.map(clubDescriptionForm, club);
    }

    public Club getClubToUpdate(Account account, String path) {
        Club club = this.getClub(path);
        checkIfManager(account, club);
        return club;
    }

    public void updateClubImage(Club club, String image) {
        club.setImage(image);
    }

    public void enableClubBanner(Club club) {
        club.setUseBanner(true);
    }

    public void disableClubBanner(Club club) {
        club.setUseBanner(false);
    }

    public void addTag(Club club, Tag tag) {
        club.getTags().add(tag);
    }

    public void removeTag(Club club, Tag tag) {
        club.getTags().remove(tag);
    }

    public void addZone(Club club, Zone zone) {
        club.getZones().add(zone);
    }

    public void removeZone(Club club, Zone zone) {
        club.getZones().remove(zone);
    }

    public Club getClubToUpdateTag(Account account, String path) {
        Club club = clubRepository.findClubWithTagsByPath(path);
        checkIfExistingClub(path, club);
        checkIfManager(account, club);
        return club;
    }

    public Club getClubToUpdateZone(Account account, String path) {
        Club club = clubRepository.findClubWithZonesByPath(path);
        checkIfExistingClub(path, club);
        checkIfManager(account, club);
        return club;
    }

    public Club getClubToUpdateStatus(Account account, String path) {
        Club club = clubRepository.findClubWithManagersByPath(path);
        checkIfExistingClub(path, club);
        checkIfManager(account, club);
        return club;
    }

    public void publish(Club club) {
        club.publish();
    }

    public void close(Club club) {
        club.close();
    }

    public void startRecruit(Club club) {
        club.startRecruit();
    }

    public void stopRecruit(Club club) {
        club.stopRecruit();
    }

    public boolean isValidPath(String newPath) {
        if (!newPath.matches(VALID_PATH_PATTERN)){
            return false;
        }

        return !clubRepository.existsByPath(newPath);
    }

    public void updateClubPath(Club club, String newPath) {
        club.setPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() <= 50;
    }

    public void updateClubTitle(Club club, String newTitle) {
        club.setTitle(newTitle);
    }

    public void remove(Club club) {
        if (club.isRemovable()) {
            clubRepository.delete(club);
        } else {
            throw new IllegalArgumentException("동호회를 삭제할 수 없습니다.");
        }
    }

}

package com.damoim.club;

import com.damoim.club.form.ClubDescriptionForm;
import com.damoim.domain.Account;
import com.damoim.domain.Club;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ModelMapper modelMapper;

    public Club createNewClub(Club club, Account account) {
        Club newClub = clubRepository.save(club);
        newClub.addManager(account);
        return newClub;
    }

    public Club getclub(String path) {
        Club club = this.clubRepository.findByPath(path);
        if (club == null) {
            throw new IllegalArgumentException(path + "에 해당하는 동호회가 없습니다.");
        }

        return club;
    }

    public void updateClubDescription(Club club, ClubDescriptionForm clubDescriptionForm) {
        modelMapper.map(clubDescriptionForm, club);
    }

    public Club getClubToUpdate(Account account, String path) {
        Club club = this.getclub(path);
        if (!account.isManagerOf(club)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }

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

}

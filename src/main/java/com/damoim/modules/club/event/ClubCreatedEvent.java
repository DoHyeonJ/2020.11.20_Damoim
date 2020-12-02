package com.damoim.modules.club.event;

import com.damoim.modules.club.Club;
import lombok.Getter;

@Getter
public class ClubCreatedEvent {

    private Club club;

    public ClubCreatedEvent(Club club) {
        this.club = club;
    }

}

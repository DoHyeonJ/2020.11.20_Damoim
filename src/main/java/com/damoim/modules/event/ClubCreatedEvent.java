package com.damoim.modules.event;

import com.damoim.modules.club.Club;
import lombok.Getter;

@Getter
public class ClubCreatedEvent {

    private Club club;

    public ClubCreatedEvent(Club club) {
        this.club = club;
    }

}

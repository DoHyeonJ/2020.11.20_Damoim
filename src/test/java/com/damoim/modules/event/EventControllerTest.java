package com.damoim.modules.event;

import com.damoim.infra.MockMvcTest;
import com.damoim.modules.account.AccountFactory;
import com.damoim.modules.account.AccountRepository;
import com.damoim.modules.account.WithAccount;
import com.damoim.modules.club.ClubControllerTest;
import com.damoim.modules.account.Account;
import com.damoim.modules.club.Club;
import com.damoim.modules.club.ClubFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ClubFactory clubFactory;
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    EventService eventService;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    AccountRepository accountRepository;


    @Test
    @DisplayName("선착순 모임에 참가 신청 - 자동수락")
    @WithAccount("dohyeon")
    void newEnrollment_to_FCFS_event_accepted() throws Exception {
        Account gildong = accountFactory.createAccount("gildong");
        Club club = clubFactory.createClub("test-club", gildong);
        Event event = createEvent("test-event", EventType.FCFS, 2, club, gildong);

        mockMvc.perform(post("/club/" + club.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/events/" + event.getId()));

        Account dohyeon = accountRepository.findByNickname("dohyeon");
        isAccepted(dohyeon, event);
    }

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 대기중 (인원꽉찬 경우)")
    @WithAccount("dohyeon")
    void newEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account gildong = accountFactory.createAccount("gildong");
        Club club = clubFactory.createClub("test-club", gildong);
        Event event = createEvent("test-event", EventType.FCFS, 2, club, gildong);

        Account may = accountFactory.createAccount("may");
        Account june = accountFactory.createAccount("june");
        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, june);

        mockMvc.perform(post("/club/" + club.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/events/" + event.getId()));

        Account dohyeon = accountRepository.findByNickname("dohyeon");
        isNotAccepted(dohyeon, event);
    }

    @Test
    @DisplayName("참가신청 확정자가 참가 신청을 취소한경우, 바로다음 대기자를 자동 신청")
    @WithAccount("dohyeon")
    void accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account dohyeon = accountRepository.findByNickname("dohyeon");
        Account gildong = accountFactory.createAccount("gildong");
        Account may = accountFactory.createAccount("may");
        Club club = clubFactory.createClub("test-club", gildong);
        Event event = createEvent("test-event", EventType.FCFS, 2, club, gildong);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, dohyeon);
        eventService.newEnrollment(event, gildong);

        isAccepted(may, event);
        isAccepted(dohyeon, event);
        isNotAccepted(gildong, event);

        mockMvc.perform(post("/club/" + club.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(gildong, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, dohyeon));
    }

    @Test
    @DisplayName("참가신청 비확정자가 참가신청을 취소하는경우, 기존 확정자를 그대로 유지하고 새로운 확정자 없음.")
    @WithAccount("dohyeon")
    void not_accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account dohyeon = accountRepository.findByNickname("dohyeon");
        Account gildong = accountFactory.createAccount("gildong");
        Account may = accountFactory.createAccount("may");
        Club club = clubFactory.createClub("test-club", gildong);
        Event event = createEvent("test-event", EventType.FCFS, 2, club, gildong);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, gildong);
        eventService.newEnrollment(event, dohyeon);

        isAccepted(may, event);
        isAccepted(gildong, event);
        isNotAccepted(dohyeon, event);

        mockMvc.perform(post("/club/" + club.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(gildong, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, dohyeon));
    }

    @Test
    @DisplayName("관리자 확인 모임에 참가 신청 - 대기")
    @WithAccount("dohyeon")
    void newEnrollment_to_CONFIMATIVE_event_not_accepted() throws Exception {
        Account gildong = accountFactory.createAccount("gildong");
        Club club = clubFactory.createClub("test-club", gildong);
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, club, gildong);

        mockMvc.perform(post("/club/" + club.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/events/" + event.getId()));

        Account dohyeon = accountRepository.findByNickname("dohyeon");
        isNotAccepted(dohyeon, event);
    }

    private void isNotAccepted(Account dohyeon, Event event) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, dohyeon).isAccepted());
    }

    private void isAccepted(Account account, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Club club, Account account) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        return eventService.createEvent(event, club, account);
    }

}
package com.damoim.modules.club;

import com.damoim.infra.MockMvcTest;
import com.damoim.modules.account.AccountFactory;
import com.damoim.modules.account.WithAccount;
import com.damoim.modules.account.AccountRepository;
import com.damoim.modules.account.Account;
import com.damoim.modules.club.Club;
import com.damoim.modules.club.ClubRepository;
import com.damoim.modules.club.ClubService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class ClubControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ClubService clubService;
    @Autowired
    ClubRepository clubRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    ClubFactory clubFactory;

    @Test
    @WithAccount("dohyeon")
    @DisplayName("동호회 개설 폼")
    void createClubForm() throws Exception {
        mockMvc.perform(get("/new-club"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("clubForm"));
    }

    @Test
    @WithAccount("dohyeon")
    @DisplayName("동호회 개설 - 성공")
    void createClub_success() throws Exception {
        mockMvc.perform(post("/new-club")
                .param("path", "test-path")
                .param("title", "club title")
                .param("shortDescription", "short description of a club")
                .param("fullDescription", "full description of a club")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/test-path"));

        Club club = clubRepository.findByPath("test-path");
        assertNotNull(club);
        Account account = accountRepository.findByNickname("dohyeon");
        assertTrue(club.getManagers().contains(account));
    }

    @Test
    @WithAccount("dohyeon")
    @DisplayName("동호회 개설 - 실패")
    void createClub_fail() throws Exception {
        mockMvc.perform(post("/new-club")
                .param("path", "wrong path")
                .param("title", "club title")
                .param("shortDescription", "short description of a club")
                .param("fullDescription", "full description of a club")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("club/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("clubForm"));
        Club club = clubRepository.findByPath("test-path");
        assertNull(club);
    }

    @Test
    @WithAccount("dohyeon")
    @DisplayName("동호회 가입")
    void joinClub() throws Exception {
        Account gilDong = accountFactory.createAccount("gilDong");

        Club club = clubFactory.createClub("test-club", gilDong);

        mockMvc.perform(get("/club/" + club.getPath() + "/join"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/members"));

        Account account = accountRepository.findByNickname("dohyeon");
        assertTrue(club.getMembers().contains(account));
    }

    @Test
    @WithAccount("dohyeon")
    @DisplayName("동호회 탈퇴")
    void leaveClub() throws Exception {
        Account gilDong = accountFactory.createAccount("gilDong");
        Club club = clubFactory.createClub("test-club", gilDong);

        Account account = accountRepository.findByNickname("dohyeon");
        clubService.addMember(club, account);

        mockMvc.perform(get("/club/" + club.getPath() + "/leave"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/members"));

        assertFalse(club.getMembers().contains(account));
    }

}
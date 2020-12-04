package com.damoim.modules.club;

import com.damoim.infra.MockMvcTest;
import com.damoim.modules.account.AccountFactory;
import com.damoim.modules.account.AccountRepository;
import com.damoim.modules.account.WithAccount;
import com.damoim.modules.account.Account;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class ClubSettingsControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ClubFactory clubFactory;
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ClubRepository clubRepository;

    @Test
    @WithAccount("dohyeon")
    @DisplayName("동호회 소개 수정 폼 조회 - 성공")
    void updateDescriptionForm_success() throws Exception {
        Account account = accountRepository.findByNickname("dohyeon");
        Club club = clubFactory.createClub("test-club", account);

        mockMvc.perform(get("/club/" + club.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/settings/description"))
                .andExpect(model().attributeExists("clubDescriptionForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("club"));
    }

    @Test
    @WithAccount("dohyeon")
    @DisplayName("동호회 소개 수정 폼 조회 - 실패 (권한없음)")
    void updateDescriptionForm_fail() throws Exception {
        Account account = accountFactory.createAccount("account");
        Club club = clubFactory.createClub("test-club", account);

        mockMvc.perform(get("/club/" + club.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }

    @Test
    @WithAccount("dohyeon")
    @DisplayName("동호회 소개 수정 - 성공")
    void updateDescription_success() throws Exception {
        Account account = accountRepository.findByNickname("dohyeon");
        Club club = clubFactory.createClub("test-club", account);

        String settingsDescriptionUrl = "/club/" + club.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                .param("shortDescription", "short description")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(settingsDescriptionUrl))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @WithAccount("dohyeon")
    @DisplayName("동호회 소개 수정 - 실패")
    void updateDescription_fail() throws Exception {
        Account account = accountRepository.findByNickname("dohyeon");
        Club club = clubFactory.createClub("test-club", account);

        String settingsDescriptionUrl = "/club/" + club.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                .param("shortDescription", "")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("clubDescriptionForm"))
                .andExpect(model().attributeExists("club"));
    }

}
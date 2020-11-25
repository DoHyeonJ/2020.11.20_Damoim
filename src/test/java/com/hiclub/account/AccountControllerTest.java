package com.hiclub.account;

import com.hiclub.domain.Account;
import com.hiclub.account.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원가입 VIEW TEST")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 처리 - 정상")
    @Test
    void signUpSubmit_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "dohyeon")
                .param("email", "dohyeon@email.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("dohyeon"));

        Account account = accountRepository.findByEmail("dohyeon@email.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "12345678");
        assertNotNull(account.getEmailCheckToken());
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @DisplayName("회원가입 처리 - 오류")
    @Test
    void signUpSubmit_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "gildong")
                .param("email", "email..")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("인증메일 확인 - 정상")
    @Test
    void checkEmailToken_correct() throws Exception {
        Account account = Account.builder()
                .email("dohyeon@email.com")
                .password("12345678")
                .nickname("dohyeon")
                .build();
        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();
    }

    @DisplayName("인증메일 확인 - 오류")
    @Test
    void checkEmailToken_wrong() throws Exception {
        mockMvc.perform(get("/check-email-token")
                .param("token", "asdqfwwerwr")
                .param("email", "dohyeon@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("mail/checked-email"))
                .andExpect(unauthenticated());
    }


}
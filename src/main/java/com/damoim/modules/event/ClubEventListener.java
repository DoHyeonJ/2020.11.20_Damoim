package com.damoim.modules.event;

import com.damoim.infra.config.AppProperties;
import com.damoim.infra.mail.EmailMessage;
import com.damoim.infra.mail.EmailService;
import com.damoim.modules.account.Account;
import com.damoim.modules.account.AccountPredicates;
import com.damoim.modules.account.AccountRepository;
import com.damoim.modules.club.Club;
import com.damoim.modules.club.ClubRepository;
import com.damoim.modules.notification.Notification;
import com.damoim.modules.notification.NotificationRepository;
import com.damoim.modules.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class ClubEventListener {

    private final ClubRepository clubRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handelClubCreatedEvent(ClubCreatedEvent clubCreatedEvent) {
        Club club = clubRepository.findClubWithTagsAndZonesById(clubCreatedEvent.getClub().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(club.getTags(), club.getZones()));
        accounts.forEach(account -> {
            if (account.isClubCreatedByEmail()) {
                sendClubCreatedEmail(club, account);
            }

            if (account.isClubCreatedByWeb()) {
                saveClubCreatedNotification(club, account);
            }
        });
    }

    private void saveClubCreatedNotification(Club club, Account account) {
        Notification notification = new Notification();
        notification.setTitle(club.getTitle());
        notification.setLink("/club/" + club.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedLocalDateTime(LocalDateTime.now());
        notification.setMessage(club.getShortDescription());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.CLUB_CREATED);
        notificationRepository.save(notification);
    }

    private void sendClubCreatedEmail(Club club, Account account) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/club/" + club.getEncodedPath());
        context.setVariable("linkName", club.getTitle());
        context.setVariable("message", "새로운 동호회가 생겼습니다.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("다모임, '" + club.getTitle() + "' 동호회가 생겼습니다.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);

    }

}

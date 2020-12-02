package com.damoim.modules.club.event;

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
    public void handleClubCreatedEvent(ClubCreatedEvent clubCreatedEvent) {
        Club club = clubRepository.findClubWithTagsAndZonesById(clubCreatedEvent.getClub().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(club.getTags(), club.getZones()));
        accounts.forEach(account -> {
            if (account.isClubCreatedByEmail()) {
                sendClubCreatedEmail(club, account, "새로운 동호회가 생겼습니다.",
                        "다모임, '" + club.getTitle() + "' 스터디가 생겼습니다.");
            }

            if (account.isClubCreatedByWeb()) {
                createNotification(club, account, club.getShortDescription(), NotificationType.CLUB_CREATED);
            }
        });
    }

    private void createNotification(Club club, Account account, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(club.getTitle());
        notification.setLink("/club/" + club.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(message);
        notification.setAccount(account);
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
    }

    private void sendClubCreatedEmail(Club club, Account account, String contextMessage, String emailSubject) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/club/" + club.getEncodedPath());
        context.setVariable("linkName", club.getTitle());
        context.setVariable("message", contextMessage);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(emailSubject)
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);

    }

}

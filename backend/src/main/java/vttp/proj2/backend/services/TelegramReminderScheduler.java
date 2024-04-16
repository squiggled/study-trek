package vttp.proj2.backend.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TelegramReminderScheduler {

    @Autowired
    private SubscriptionService subscriptionSvc;

    @Autowired
    private TelegramBotService telegramBotSvc;

    @Scheduled(cron = "0 0 11 * * ?") 
    public void sendDailyReminders() {
        subscriptionSvc.getActivePremiumUserDetails().forEach(userDetails -> {
            Long chatId = (Long) userDetails.get("chatId");
            if (chatId != null) {
                telegramBotSvc.sendMessage(chatId, "👋 Don't forget to set some time to learn today! 📆: https://study-trek.up.railway.app/#/calendar");
            }
        });
    }
}


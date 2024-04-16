package vttp.proj2.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import vttp.proj2.backend.services.TelegramBotService;

@Configuration
public class TelegramBotInitialiser {
    
    private static final Logger log = LoggerFactory.getLogger(TelegramBotInitialiser.class);

    @Autowired
    private TelegramBotService telegramBotService;

    @PostConstruct
    public void start() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBotService);
            log.info("🟢 Telegram bot has been registered successfully.");
        } catch (Exception e) {
            log.error("🔴 Failed to register Telegram bot", e);
        }
    }

    @PreDestroy
    public void shutDown() {
        log.info("🟡 Application shutdown initiated, stopping Telegram bot.");
    }
}


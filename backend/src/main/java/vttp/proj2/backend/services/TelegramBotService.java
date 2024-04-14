package vttp.proj2.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import vttp.proj2.backend.dtos.AccountInfoDTO;
import vttp.proj2.backend.models.OpenAIRequest;
import vttp.proj2.backend.models.OpenAIResponse;
import vttp.proj2.backend.repositories.SubscriptionRepository;
import vttp.proj2.backend.repositories.UserRepository;

@Component
public class TelegramBotService extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String username;

    @Value("${telegram.bot.token}")
    private String token;

    @Autowired
    UserService userSvc;
    @Autowired
    SubscriptionRepository subscriptionRepo;
    @Autowired
    UserRepository userRepo;
    @Autowired
    private OpenAIService openAISvc;

    @Override
    public String getBotUsername() {
        // System.out.println("Bot username: " + username);
        return username;
    }

    @Override
    public String getBotToken() {
        // System.out.println("Bot token: " + token);
        return token;
    }

    @Override
public void onUpdateReceived(Update update) {
    Long chatId = update.getMessage().getChatId();
    if (update.hasMessage() && update.getMessage().hasText()) {
        String messageText = update.getMessage().getText();

        String[] parts = messageText.split("\\s+", 3); 
        String command = parts[0];

        switch (command) {
            case "/start":
                sendStartMessage(chatId);
                break;
            case "/courses":
                sendUserCourses(chatId);
                break;
            case "/navigator":
                if (parts.length < 3) {
                    sendMessage(chatId, "Usage: /navigator &lt;number&gt; &lt;topic&gt;");
                } else {
                    handleNavigatorCommand(chatId, parts[1], parts.length > 2 ? parts[2] : "");
                }
                break;
            case "/link":
                if (parts.length < 3) {
                    sendMessage(chatId, "Usage: /link &lt;code&gt; &lt;email&gt;");
                } else {
                    processLinkCommand(chatId, parts[1], parts[2]);
                }
                break;
            default:
                sendMessage(chatId, "Unknown command. Please try again.");
                break;
        }
    }
}

    //parse commands
    private void sendStartMessage(Long chatId) {
        String welcomeText = "Welcome! Please log in at <a href=\"https://study-trek.up.railway.app/#/telegram\">https://study-trek.up.railway.app/#/telegram</a> and enter the code provided to link your Telegram account.";
        sendMessage(chatId, welcomeText);
    }

    private void handleNavigatorCommand(Long chatId, String number, String topic) {
        sendOpenAIResponse(chatId, number, topic);
    }
    
    private void sendOpenAIResponse(Long chatId, String number, String topic) {
        String prompt = "Recommend " + number + " course(s) on " + topic + " from udemy and coursera only";
        OpenAIRequest req = new OpenAIRequest("gpt-3.5-turbo", prompt);
        OpenAIResponse response = openAISvc.getResponse(req);
        String responseText = response != null ? extractContent(response) : "Failed to fetch response.";
        sendMessage(chatId, responseText);
    }

    private void sendUserCourses(Long chatId) {
        String userId = getUserIdByChatId(chatId);
        if (userId == null) {
            sendMessage(chatId, "Your account is not linked. Please link your account using /link.");
            return;
        }
        List<String> courses = subscriptionRepo.findCoursesByUserId(userId);
        String response = courses.isEmpty() ? "No courses registered." : String.join("\n", courses);
        sendMessage(chatId, response);
    }

    private void processLinkCommand(Long chatId, String linkCode, String email) {
        processAccountLinking(email, linkCode, chatId);
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode("HTML");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //other helper methods
    private void processAccountLinking(String email, String linkCode, Long chatId) {
        Optional<AccountInfoDTO> user = verifyUserAndLinkCode(email, linkCode);
        if (user.isPresent()) {
            boolean linked = linkTelegramUserId(user.get().getUserId(), chatId);
            if (linked) {
                sendMessage(chatId, "Your account has been successfully linked! You can now use all premium features.");
            } else {
                sendMessage(chatId, "Linking failed. Please contact support if this continues.");
            }
        } else {
            sendMessage(chatId, "Invalid link code or email. Please check the details provided and try again.");
        }
    }

    public boolean storeLinkCode(String linkCode, String userId) {
        return subscriptionRepo.storeLinkCode(linkCode, userId);
    }

    public Optional<AccountInfoDTO> verifyUserAndLinkCode(String email, String linkCode) {
        return subscriptionRepo.findAccountInfoByUserIdndLinkCode(email, linkCode);
    }

    private boolean linkTelegramUserId(String userId, Long chatId) {
        return subscriptionRepo.storeChatIdIfNew(userId, chatId);
    }

    public String getUserIdByChatId(Long chatId) {
        return userRepo.findUserIdByChatId(chatId)
                             .orElseThrow(() -> new RuntimeException("No user linked with the provided chat ID."));
    }
    
    private String extractContent(OpenAIResponse response) {
        if (response.getChoices() != null && !response.getChoices().isEmpty()) {
            OpenAIResponse.Choice choice = response.getChoices().get(0);
            return choice.getMessage().getContent();
        }
        return "No response found.";
    }

}

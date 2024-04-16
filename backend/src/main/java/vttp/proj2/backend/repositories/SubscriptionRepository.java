package vttp.proj2.backend.repositories;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp.proj2.backend.dtos.AccountInfoDTO;

@Repository
public class SubscriptionRepository {

    @Autowired
    private JdbcTemplate template;

    public boolean updateSubscriptionStatus(String userId, String roleName) {
        return template.update(Queries.SQL_SUBSCRIPTION_UPDATE_STATUS, roleName, userId) > 0;
    }

    public boolean addPremiumSubscription(String userId){
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTimestamp.getTime());
        cal.add(Calendar.MONTH, 1);
        Timestamp oneMonthLater = new Timestamp(cal.getTimeInMillis());
        Date sqlDate = new Date(System.currentTimeMillis());
        return template.update(Queries.SQL_SUBSCRIPTION_INSERT, userId, "PREMIUM", sqlDate, null, "ACTIVE", true, currentTimestamp, oneMonthLater) > 0;
       
    }

    public List<Map<String, Object>> findActivePremiumUserDetails() {
        return template.queryForList(Queries.SQL_SUBSCRIPTION_GET_ACTIVE_PREMIUM);
    }

    public boolean storeChatIdIfNew(String userId, Long newChatId) {
        System.out.println("SubscriptionRepo: userId " + userId);
        System.out.println("SubscriptionRepo: chatId " + newChatId);
    
        //check if there is an existing chatID for the user.
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_TELEGRAM_FIND_CHATID_BY_USERID, userId);
        if (rs.next()) {
            Long existingChatId = rs.getLong("chatId");
            //if the existing ID is different, update 
            if (!existingChatId.equals(newChatId)) {
                int updated = template.update(Queries.SQL_TELEGRAM_UPDATE_ID, newChatId, userId);
                return updated > 0;  //true if the update was successful.
            }
            return true; 
        } else {
            // if no chatID exists, insert new one
            int inserted = template.update(Queries.SQL_TELEGRAM_CREATE_NEW_ID, userId, newChatId);
            return inserted > 0;  //true if the insertion was successful.
        }
    }
    

    public Optional<AccountInfoDTO> findAccountInfoByUserIdndLinkCode(String email, String linkCode) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_TELEGRAM_VERIFY_LINKCODE, email, linkCode);
        if (rs.next()){
            AccountInfoDTO user = new AccountInfoDTO();
            user.setEmail(rs.getString("email"));
            user.setTelegramId(rs.getLong("telegram_user_id"));
            user.setUserId(rs.getString("userId"));
            System.out.println("Link code verified for user: " + user.getUserId());
            return Optional.of(user);
        }
        System.out.println("No matching record found for link code: " + linkCode + " and email: " + email);
        return Optional.empty();
    }

    public boolean storeLinkCode(String linkCode, String userId) {
        return template.update(Queries.SQL_TELEGRAM_SET_LINKCODE, linkCode, userId) > 0;
    }

    public List<String> findCoursesByUserId(String userId) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_TELEGRAM_GET_COURSES, userId);
        List<String> courses = new ArrayList<>();
        while (rs.next()){
            courses.add(rs.getString("title"));
        }
        return courses;
    }

}

package vttp.proj2.backend.repositories;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
    
}

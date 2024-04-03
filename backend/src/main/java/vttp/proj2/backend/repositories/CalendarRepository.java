package vttp.proj2.backend.repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp.proj2.backend.models.CalendarEvent;

@Repository
public class CalendarRepository {
    
    @Autowired
    private JdbcTemplate template;

    public List<CalendarEvent> getAllEventsForUser(String userId) {
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_USER_CALENDAR_GET_ALL, userId);
        List<CalendarEvent> events = new ArrayList<>();
        while (rs.next()){
            CalendarEvent event = mapRsToCalendarEvent(rs);
            events.add(event);
        }
        System.out.println("Calendar Repo - user events " + events);
        return events;
    }

    public CalendarEvent createNewEvent(CalendarEvent calEvent) {
        // int rowsAffected = template.update(Queries.SQL_USER_CALENDAR_CREATE_NEW, calEvent.getUserId(), calEvent.getTitle(), calEvent.getText(), calEvent.getSelectedHour(), calEvent.getDate());
        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(Queries.SQL_USER_CALENDAR_CREATE_NEW, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, calEvent.getUserId());
            ps.setString(2, calEvent.getTitle());
            ps.setString(3, calEvent.getText());
            ps.setInt(4, calEvent.getSelectedHour());
            ps.setDate(5, new java.sql.Date(calEvent.getDate().getTime()));
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys().size() > 1) {
            calEvent.setCalendarId((Integer) keyHolder.getKeys().get("calendarId")); 
        } else {
            calEvent.setCalendarId(keyHolder.getKey().intValue()); 
        }
        System.out.println("Calendar Repo - new calendar event " + calEvent);
        return calEvent;
    }

    public CalendarEvent mapRsToCalendarEvent(SqlRowSet rs){
        CalendarEvent event = new CalendarEvent();
        event.setCalendarId(rs.getInt("calendarId"));
        event.setDate(rs.getDate("date"));
        event.setUserId(rs.getString("userId"));
        event.setSelectedHour(rs.getInt("selectedHour"));
        event.setTitle(rs.getString("title"));
        event.setText(rs.getString("text"));
        return event;
    }
}

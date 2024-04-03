package vttp.proj2.backend.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEvent {
    private int calendarId;
    private String userId;
    private String title;
    private String text;
    private int selectedHour;
    private Date date;
}

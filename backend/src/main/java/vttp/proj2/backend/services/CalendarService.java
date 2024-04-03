package vttp.proj2.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.proj2.backend.models.CalendarEvent;
import vttp.proj2.backend.repositories.CalendarRepository;

@Service
public class CalendarService {

    @Autowired
    CalendarRepository calendarRepo;

    public List<CalendarEvent> getAllEventsForUser(String userId) {
        return calendarRepo.getAllEventsForUser(userId);
    }

    public CalendarEvent createNewEvent(CalendarEvent calEvent) {
        CalendarEvent newEvent = calendarRepo.createNewEvent(calEvent);
        return newEvent;
    }
    
}

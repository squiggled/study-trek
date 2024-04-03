package vttp.proj2.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vttp.proj2.backend.models.CalendarEvent;
import vttp.proj2.backend.services.CalendarService;

@RestController
@RequestMapping("/api")
public class CalendarController {

    @Autowired
    CalendarService calendarSvc;

    @GetMapping("/calendar/{userId}")
    public ResponseEntity<?> getAllEventsForUser(@PathVariable String userId){
        List<CalendarEvent> events = calendarSvc.getAllEventsForUser(userId);
        if (events==null){
            System.out.println("CalendarController: no events found");
            return ResponseEntity.notFound().build(); 
        }
        return ResponseEntity.ok(events);
    }

    @PostMapping("/calendar/{userId}/new")
    public ResponseEntity<?> newCalendarEvent(@PathVariable String userId, @RequestBody CalendarEvent calEvent){
        CalendarEvent newEvent = calendarSvc.createNewEvent(calEvent);
        if (newEvent==null){
            System.out.println("🔴 CalendarController: unable to create new event");
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(newEvent);
    }
    
}

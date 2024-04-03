import { Injectable, inject } from '@angular/core';
import { MyCalendarEvent } from '../models';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CalendarStore } from '../stores/calendar.store';

@Injectable({
  providedIn: 'root',
})
export class CalendarService {
    private httpClient = inject(HttpClient);
    private calendarStore = inject(CalendarStore)
    
    private addTokenToHeader(): HttpHeaders {
        const token = localStorage.getItem('jwtToken');
        return new HttpHeaders().set('Authorization', `Bearer ${token}`);
    }
    
    createEvent(calEvent: MyCalendarEvent) {
      return this.httpClient.post<MyCalendarEvent>(`/api/calendar/${calEvent.userId}/new`, calEvent, { headers: this.addTokenToHeader() })
        .subscribe({
          next: (newEvent) => {
            this.calendarStore.updateEvents(newEvent);
          },
          error: (error) => console.error("Error creating event", error)
        });
    }
   
    getAllCalendarEventsForUser(userId: string) {
      return this.httpClient.get<MyCalendarEvent[]>(`/api/calendar/${userId}`, { headers: this.addTokenToHeader() });
    }

}

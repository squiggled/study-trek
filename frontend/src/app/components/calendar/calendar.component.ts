import { Component, Input, OnDestroy, inject } from '@angular/core';
import { ChangeDetectorRef } from '@angular/core';
import {
  ChangeDetectionStrategy,
  OnInit,
  ViewEncapsulation,
} from '@angular/core';
import { CalendarEventTitleFormatter, CalendarEvent as AngularCalendarEvent } from 'angular-calendar';
import { WeekViewHourSegment } from 'calendar-utils';
import { addDays, startOfWeek } from 'date-fns';
import { MatDialog } from '@angular/material/dialog';
import { DatePipe } from '@angular/common';
import { CreateEventComponent } from './create-event.component';
import {  FormGroup } from '@angular/forms';
import { MyCalendarEvent, HourSegmentClickedEvent } from '../../models';
import { CalendarService } from '../../services/calendar.service';
import { CalendarStore } from '../../stores/calendar.store';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrl: './calendar.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    DatePipe,
    {
      provide: CalendarEventTitleFormatter,
    },
  ],
  encapsulation: ViewEncapsulation.None,
})

export class CalendarComponent implements OnInit, OnDestroy {

  dragToCreateActive = false;
  events: AngularCalendarEvent[] = [];
  days: any[] = [];
  slots: any[] = [];

  viewDate = new Date();
  weekStartsOn: 0 = 0;
  viewStartHour: number = 8; // set view start hour to 8 am
  viewStart = 8
  viewEnd = 20
  calendarForm!: FormGroup;
  
  userId!:string
  showAlert: boolean = true; 
  constructor(private cdr: ChangeDetectorRef, private datePipe: DatePipe, public dialog: MatDialog) {}
  private calendarSvc = inject(CalendarService)
  private calendarStore = inject(CalendarStore)
  private eventsSubscription!: Subscription;
  private subscription: Subscription = new Subscription();


  ngOnInit(): void {
    const userId = localStorage.getItem('userId');
    if (userId) {
      this.userId = userId;
      this.fetchAndStoreEvents();
      this.eventsSubscription = this.calendarStore.events$.subscribe(events => {
        console.log("Current events in the store:", events);
        this.events = this.transformMyEventsToAngularCalendarEvents(events);
        this.cdr.markForCheck(); 
      });
    }
    
    setTimeout(() => {
      this.showAlert = false;
      this.cdr.markForCheck(); 
    }, 3000);
    this.getUserEvents();
  }

  fetchAndStoreEvents() {
    const userId = localStorage.getItem('userId');
    if (userId) {
      this.subscription.add(
        this.calendarSvc.getAllCalendarEventsForUser(userId).subscribe({
          next: (events) => {
            this.calendarStore.setEvents(events);
          },
          error: (error) => console.error('Failed to load events', error),
        })
      );
    }
  }

  date!: string;
  time!: string;
  ownerName!: string;

  getUserEvents() {}

  transformMyEventsToAngularCalendarEvents(events: MyCalendarEvent[]): AngularCalendarEvent[] {
    return events.map(event => {
      const startDate = new Date(event.date);
      const endDate = new Date(startDate.getTime());
      startDate.setHours(event.selectedHour, 0, 0, 0);
      // const startDate = new Date(event.date.getFullYear(), event.date.getMonth(), event.date.getDate(), event.selectedHour);
      // const endDate = new Date(startDate.getTime());
      endDate.setHours(startDate.getHours() + 2); // Set the event to end 2 hours later
  
      return {
        start: startDate,
        end: endDate, 
        title: event.title,
        meta: {
          calendarId: event.calendarId,
          userId: event.userId,
          text: event.text,
          selectedHour: event.selectedHour,
        }
      };
    });
  }
 
 // To add an event
// In your component

// In your Angular component

onHourSegmentClicked(event: HourSegmentClickedEvent): void {
  const userId = localStorage.getItem('userId');
  if (userId) {
    const selectedDate = event.date;
    const selectedHour = selectedDate.getHours();

    const dialogRef = this.dialog.open(CreateEventComponent, {
      width: '500px',
      data: { date: selectedDate, hour: selectedHour, userId: userId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.calendarSvc.createEvent({
          userId: userId, 
          title: result.title,
          text: result.text,
          selectedHour: result.hour, 
          date: result.date
        });
      }
    });
  }
}




  refresh() {
    this.events = [...this.events];
    this.cdr.detectChanges();
    // this.getSlots();
  }

  convertTime(t: string | number | Date) {
    return new Date(t).toTimeString();
  }

  convertDay(d: string | number | Date) {
    return new Date(d).toLocaleString('en-us', {
      weekday: 'long',
    });
  }
  removeSlot(id: string | number | undefined) {
    for (let j = 0; j < this.slots.length; j++) {
      this.slots[j].time = this.slots[j].time.filter(
        (t: { id: string | number | undefined }) => t.id !== id
      );
    }
  }
  goToNextWeek() {
    this.viewDate = addDays(
      startOfWeek(this.viewDate, { weekStartsOn: this.weekStartsOn }),
      7
    );
    this.refresh();
  }
  goToPreviousWeek() {
    this.viewDate = addDays(
      startOfWeek(this.viewDate, { weekStartsOn: this.weekStartsOn }),
      -7
    );
    this.refresh();
  }

  ngOnDestroy(): void {
    if (this.eventsSubscription) {
      this.eventsSubscription.unsubscribe(); 
    }
  }
}

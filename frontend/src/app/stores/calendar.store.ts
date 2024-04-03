import { Injectable } from '@angular/core';
import { ComponentStore } from '@ngrx/component-store';
import { MyCalendarEvent } from '../models';

export interface CalendarEventsSlice {
  events: MyCalendarEvent[];
}

const INIT_STATE: CalendarEventsSlice = {
  events: [],
};

@Injectable({
  providedIn: 'root',
})
export class CalendarStore extends ComponentStore<CalendarEventsSlice> {

  constructor() {super(INIT_STATE); }

  readonly events$ = this.select((state) => state.events);

  readonly setEvents = this.updater((state, events: MyCalendarEvent[]) => ({
    ...state,
    events,
  }));

  readonly updateEvents = this.updater((state, newEvent: MyCalendarEvent) => ({
    ...state,
    events: [...state.events, newEvent]
  }));
}



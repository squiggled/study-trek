import { Injectable, OnDestroy, OnInit, inject } from '@angular/core';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { UserSessionStore } from '../stores/user.store';
import { AccountDetails, Notification } from '../models';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class NotificationService implements OnInit, OnDestroy{
 
  private httpClient = inject(HttpClient);
  private userSessionStore = inject(UserSessionStore);
  private notificationsSubject = new BehaviorSubject<Notification[]>([]);
  public notifications$ = this.notificationsSubject.asObservable();
  userId!: string; 
  private subscription: Subscription = new Subscription();

  ngOnInit(): void {
    this.subscription.add(
      this.userSessionStore.userId$.subscribe((id) => {
        this.userId = id;
      })
    );
    this.getNotifications(this.userId);
  }
  
  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  getNotifications(userId: string): Observable<Notification[]> {
    const params = new HttpParams()
    .set('userId', userId);
    return this.httpClient.get<Notification[]>(`/api/notifications/`);
  }

  setNotifications(notifications: Notification[]) {
    this.notificationsSubject.next(notifications);
  }
}

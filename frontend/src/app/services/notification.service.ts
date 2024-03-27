import { Injectable, OnDestroy, OnInit, inject } from '@angular/core';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { UserSessionStore } from '../stores/user.store';
import { AccountDetails, Notification } from '../models';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class NotificationService implements OnInit, OnDestroy {
  private httpClient = inject(HttpClient);
  private userSessionStore = inject(UserSessionStore);
  private notificationsSubject = new BehaviorSubject<Notification[]>([]);
  public notifications$ = this.notificationsSubject.asObservable();
  userId!: string;
  private subscription: Subscription = new Subscription();

  private addTokenToHeader(): HttpHeaders {
    const token = localStorage.getItem('jwtToken');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  ngOnInit(): void {
    this.subscription.add(
      this.userSessionStore.userId$.subscribe((id) => {
        this.userId = id;
        this.fetchAndSetNotifications(); 
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  fetchAndSetNotifications(): void {
    const userId = localStorage.getItem('userId');
    if (userId) {
      this.userId = userId;
      this.getNotifications(userId).subscribe((notifications) => {
        this.setNotifications(notifications);
      });
    }
  }

  // getNotifications(userId: string): Observable<Notification[]> {
  //   const userId1 = localStorage.getItem('userId');
  //   if (!userId1) {
  //     console.error('User ID not found in local storage.');
  //   }
  //   const url = `/api/user/notifications/${userId1}`;
  //   console.log('Request URL: ' + url);
  //   this.httpClient
  //     .get<Notification[]>(url, { headers: this.addTokenToHeader() })
  //     .subscribe((notifications) => {
  //       this.notificationsSubject.next(notifications);
  //     });
  //   return this.notificationsSubject.asObservable();
  // }

  getNotifications(userId: string): Observable<Notification[]> {
    const userId1 = localStorage.getItem('userId');
    if (!userId1) {
      console.error('User ID not found in local storage.');
    }
    const url = `/api/user/notifications/${userId1}`;
    console.log('Request URL: ' + url);
    return this.httpClient
      .get<Notification[]>(url, { headers: this.addTokenToHeader() });
}

  setNotifications(notifications: Notification[]) {
    this.notificationsSubject.next(notifications);
  }
}

import { Injectable, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService implements OnInit{
 
  private notificationsSubject = new BehaviorSubject<Notification[]>([]);
  public notifications$ = this.notificationsSubject.asObservable();
  
  ngOnInit(): void {
    this.getNotifications();
  }
  

  getNotifications(userId: string): import("rxjs").Observable<Notification[]> {
    throw new Error('Method not implemented.');
  }
}

import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { SubscriptionService } from '../../services/subscription.service';
import { Observable, Subscription } from 'rxjs';
import { UserSessionStore } from '../../stores/user.store';
import { AuthService } from '../../services/auth.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-subscribe',
  templateUrl: './subscribe.component.html',
  styleUrl: './subscribe.component.css',
})
export class SubscribeComponent implements OnInit, OnDestroy {
  private subscribeSvc = inject(SubscriptionService);
  private authSvc = inject(AuthService);
  private userSessionStore = inject(UserSessionStore);
  constructor(private titleService: Title) { }

  email!:string;
  private subscription: Subscription = new Subscription();
  isSubscriber: boolean = false;

  ngOnInit(): void {
    this.titleService.setTitle('Study Trek | Subscribe');
    this.subscription.add(
      this.userSessionStore.email$.subscribe(email => {
        this.email = email;
      })
    );
    this.isSubscriber = this.authSvc.isSubscriber();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  subscribe() {
    const userId = localStorage.getItem('userId');

    if (userId !== null) {
      console.log('Found userId:', userId);
      this.subscribeSvc.goToPayment( userId, this.email);
    } else {
      console.log('userId not found in local storage');
    }
  }
}

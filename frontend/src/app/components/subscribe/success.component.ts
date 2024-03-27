import { Component, OnInit, inject } from '@angular/core';
import { SubscriptionService } from '../../services/subscription.service';
import { jwtDecode } from 'jwt-decode';
import { Subscription } from 'rxjs';
import { UserSessionStore } from '../../stores/user.store';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-success',
  templateUrl: './success.component.html',
  styleUrl: './success.component.css',
})
export class SubscribeSuccessComponent implements OnInit {
  userSessionStore = inject(UserSessionStore);
  subscriptionSvc = inject(SubscriptionService);
  authSvc = inject(AuthService);
  router = inject(Router);
  private subscription: Subscription = new Subscription();
  isSubscriber: boolean = false;

  ngOnInit() {
    console.log('got to success oninit');
    
    this.subscription.add(
      this.userSessionStore.email$.subscribe((email) => {
        if (email) {
          const token = localStorage.getItem('jwtToken');
          if (token) {
            const decoded = jwtDecode(token);
            const userId = decoded.sub;
            console.log(`Email: ${email}, UserId: ${userId}`);
            if (userId) {
              this.subscription.add(
                this.subscriptionSvc.paymentSuccess(userId, email).subscribe({
                  next: (response) => {
                    console.log('Subscription update successful', response);
                  },
                  error: (error) => {
                    console.error('Error updating subscription', error);
                  },
                })
              );
            }
          }
        }
      })
    );
    this.isSubscriber = this.authSvc.isSubscriber();
  }
  exploreCourses(){
    this.router.navigate(['/course-navigator']);
  }

  subscribe(){
    this.router.navigate(['/join/subscribe']);
  }
}

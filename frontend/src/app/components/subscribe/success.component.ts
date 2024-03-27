import { Component, OnInit, inject } from '@angular/core';
import { SubscriptionService } from '../../services/subscription.service';
import { jwtDecode } from 'jwt-decode';
import { Subscription } from 'rxjs';
import { UserSessionStore } from '../../stores/user.store';

@Component({
  selector: 'app-success',
  templateUrl: './success.component.html',
  styleUrl: './success.component.css',
})
export class SubscribeSuccessComponent implements OnInit {
  userSessionStore = inject(UserSessionStore);
  subscriptionSvc = inject(SubscriptionService);
  private subscription: Subscription = new Subscription();

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
  }
}

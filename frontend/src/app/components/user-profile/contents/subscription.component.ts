import { Component, OnInit, inject } from '@angular/core';
import { isOnStateInitDefined } from '@ngrx/component-store/src/lifecycle_hooks';
import { UserSessionStore } from '../../../stores/user.store';
import { Subscription } from 'rxjs';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-subscription',
  templateUrl: './subscription.component.html',
  styleUrl: './subscription.component.css'
})
export class SubscriptionComponent implements OnInit{

  private userSessionStore = inject(UserSessionStore);
  private authSvc = inject(AuthService);
  private router = inject(Router)
  constructor(private titleService: Title) { }

  email!:string;
  private subscription: Subscription = new Subscription();
  isSubscriber: boolean = false;


  ngOnInit(): void {
    this.titleService.setTitle('Study Trek | My Subscription');
    this.subscription.add(
      this.userSessionStore.email$.subscribe(email => {
        this.email = email;
      })
    );
    this.isSubscriber = this.authSvc.isSubscriber();
  }

  goToSubscriptionPage(){
    this.router.navigate(['/join/subscribe']);
  }

}

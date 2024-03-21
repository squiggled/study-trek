import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { ThemeService } from '../../services/theme.service';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { Observable, Subscription } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { UserSessionStore } from '../../stores/user.store';
import { FriendRequest, Notification } from '../../models';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})

export class NavbarComponent implements OnInit, OnDestroy{
  
  themeSvc = inject(ThemeService);
  private router = inject(Router);
  private userSvc = inject(UserService);
  private authSvc = inject(AuthService);
  private notificationSvc = inject(NotificationService);
  private userSessionStore = inject(UserSessionStore);

  isLoggedIn$: Observable<boolean> = this.userSvc.isLoggedIn;
  notifications$!: Observable<Notification[]>;
  userProfilePicUrl$ = this.userSvc.userProfilePicUrl$;
  firstName$ = this.userSvc.firstName;
  userId!: string; 
  friendId!:string;
  private subscription: Subscription = new Subscription();

  ngOnInit(): void {
    this.isLoggedIn$ = this.authSvc.isLoggedIn$;
    this.subscription.add(
      this.userSessionStore.userId$.subscribe((id) => {
        this.userId = id;
      })
    );
    this.subscription.add(
      this.userSvc.foundFriend$.subscribe((friendInfo) => {
        if (friendInfo) {
          console.log("Friend's ID:", friendInfo.userId);
          this.friendId = friendInfo.userId;
        }
      })
    );
    this.notifications$ = this.notificationSvc.notifications$;
    this.notifications$.subscribe(notifications => {
      console.log('Notifications:', notifications);
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
  
  get isDarkMode(): boolean {
    return document.body.classList.contains('dark');
  }
  
  toggleTheme(): void {
    this.themeSvc.toggleTheme();
  }

  login(){
    this.router.navigate(['/join/login']);
  }

  register(){
    this.router.navigate(['/join/register'])
  }

  logout() {
    this.authSvc.logout(); 
    this.router.navigate(['/']);
  }

  navigateToMyCourses(){
    this.router.navigate(['/home/my-courses']);
  }

  goToProfile(){
    this.router.navigate(['/home/profile']);
  }
  
  onNotificationClick(notification: Notification): void {
    // Handle click, e.g., navigate to a page or mark as read
  }

  acceptFriendRequest(notification: Notification, friendReq: FriendRequest, accept: boolean){

  }
}

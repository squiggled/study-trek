import {
  Component,
  HostListener,
  OnDestroy,
  OnInit,
  inject,
} from '@angular/core';
import { ThemeService } from '../../services/theme.service';
import { NavigationEnd, Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import {
  Observable,
  Subject,
  Subscription,
  filter,
} from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { UserSessionStore } from '../../stores/user.store';
import { FriendRequest, Notification as AppNotification } from '../../models';
import { debounceTime } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent implements OnInit, OnDestroy {
  themeSvc = inject(ThemeService);
  private router = inject(Router);
  private userSvc = inject(UserService);
  private authSvc = inject(AuthService);
  private notificationSvc = inject(NotificationService);
  private fetchNotificationsSubject = new Subject<void>();

  isLoggedIn$: Observable<boolean> = this.userSvc.isLoggedIn;
  notifications$!: Observable<AppNotification[]>;
  userProfilePicUrl$ = this.userSvc.userProfilePicUrl$;
  firstName$ = this.userSvc.firstName;
  userId!: string;
  friendId!: string;
  private subscription: Subscription = new Subscription();
  isDropdownVisible = false;

  ngOnInit(): void {
    this.isLoggedIn$ = this.authSvc.isLoggedIn$;
    const userId = localStorage.getItem('userId');
    if (userId) {
      this.userId = userId;
    }
    this.subscription.add(
      this.userSvc.foundFriend$.subscribe((friendInfo) => {
        if (friendInfo) {
          console.log("Friend's ID:", friendInfo.userId);
          this.friendId = friendInfo.userId;
        }
      })
    );
    this.notifications$ = this.notificationSvc.notifications$;
    this.fetchNotificationsSubject
      .pipe(
        debounceTime(10000) 
      )
      .subscribe(() => {
        this.fetchNotifications();
      });

    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => {
        this.fetchNotificationsSubject.next();
      });
  }

  fetchNotifications() {
    const userIdFromStorage = localStorage.getItem('userId');
    if (userIdFromStorage) {
      this.userId = userIdFromStorage;
      this.notificationSvc
        .getNotifications(this.userId)
        .subscribe((notifications) => {
          console.log('Notifications:', notifications);
        });
    } else {
      console.error('User ID not found in local storage.');
    }
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

  login() {
    this.router.navigate(['/join/login']);
  }

  register() {
    this.router.navigate(['/join/register']);
  }

  logout() {
    this.authSvc.logout();
    this.router.navigate(['/']);
  }

  navigateToCourseNavigator() {
    this.router.navigate(['/course-navigator']);
  }

  onNotificationClick(): void {}

  @HostListener('document:click', ['$event'])
  clickout(event: MouseEvent) {
    const target = event.target as Element;
    if (!target.closest('.profile-dropdown')) {
      this.isDropdownVisible = false;
    }
  }

  acceptFriendRequest(
    notif: AppNotification,
    friendReq: FriendRequest,
    accept: boolean
  ) {
    this.userSvc.amendFriendRequest(notif, friendReq, accept).subscribe({
      next: () => {
        console.log('Friend list updated successfully');
      },
      error: (error) => {
        console.error('Failed to update friend list', error);
      },
    });
  }
}

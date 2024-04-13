import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserSessionStore } from '../stores/user.store';
import { UserService } from './user.service';
import { jwtDecode } from 'jwt-decode';
import { NotificationService } from './notification.service';
import { FriendListStore } from '../stores/friends.store';

@Injectable()
export class AuthService {
  private httpClient = inject(HttpClient);
  private router = inject(Router);
  private userSessionStore = inject(UserSessionStore);
  private notificationSvc = inject(NotificationService);
  private friendStore = inject(FriendListStore);

  loginFailed: boolean = false;
  loginAttempted: boolean = false;
  private isLoggedInSubject = new BehaviorSubject<boolean>(this.hasToken());

  private addTokenToHeader(): HttpHeaders {
    const token = localStorage.getItem('jwtToken');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }
  
  private hasToken(): boolean {
    return !!localStorage.getItem('jwtToken');
  }

  //expose the login state
  get isLoggedIn$(): Observable<boolean> {
    return this.isLoggedInSubject.asObservable();
  }

  processLogin(email: any, password: any) {
    this.loginAttempted = true;
    const loginData = { email, password };
    this.httpClient
      .post<any>('/api/auth/login', loginData, {
        headers: { 'Content-Type': 'application/json' },
      })
      .subscribe({
        next: (response: any) => {
          console.log(response)
          this.loginFailed = false;
          this.loginAttempted = false;
          localStorage.setItem('jwtToken', response.token); //store jwt token
          localStorage.setItem('userId', response.user.userId);
          localStorage.setItem('accountDetails', JSON.stringify(response.user));          
          localStorage.setItem('enrolledCourses', JSON.stringify(response.user.registeredCourses));
          console.log("registered courses " + JSON.stringify(response.user.registeredCourses));
          
          localStorage.setItem('isLoggedIn', 'true');
          this.isLoggedInSubject.next(true);

          //update user store
          this.userSessionStore.loginSuccess({
            accountDetails: response.user,
            isAuthenticated: true,
          });
          //set notifs
          this.notificationSvc.setNotifications(response.notifications);
          console.log('userdetails', response.user);

          //set friendlist
          if (response.friendList) {
            this.friendStore.setFriendList(response.friendList);
          }
          this.router.navigate(['/']);
        },
        error: (error: any) => {
          this.loginFailed = true;
          console.error(error);
        },
      });
  }

  processRegister(
    firstName: string,
    lastName: string,
    email: string,
    password: string
  ) {
    const registrationData = { firstName, lastName, email, password };
    this.httpClient
      .post<any>('/api/auth/register', registrationData, {
        headers: { 'Content-Type': 'application/json' },
      })
      .subscribe({
        next: (response: any) => {
          console.log(response);
          this.processLogin(email, password);
          this.router.navigate(['/']);
        },
        error: (error: any) => {
          console.error('Registration or Login Error', error);
        },
      });
  }

  changePassword(currentPassword: string, newPassword: string): Observable<any> {
    const userId = localStorage.getItem('userId'); 
    if (!userId) {
      throw new Error('User ID not found');
    }
    const dto = { currentPassword, newPassword };
    return this.httpClient.post(`/api/auth/password/${userId}`, dto, { headers: this.addTokenToHeader() });
  }

  logout(): void {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');
    localStorage.removeItem('accountDetails');
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('enrolledCourses');
    this.isLoggedInSubject.next(false);
    this.userSessionStore.resetState();
  }

  //initialise login state
  checkTokenOnStartup(): void {
    this.isLoggedInSubject.next(this.hasToken());
  }

  hasRole(expectedRole: string): boolean {
    const token = localStorage.getItem('jwtToken');
    if (!token) return false;
    const decoded: any = jwtDecode(token);
    const roles: string[] = decoded.scope || [];

    return roles.includes(expectedRole);
  }

  isSubscriber(): boolean {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
      console.log(
        'No token found. The user is not logged in or the session has expired.'
      );
      return false;
    }
    try {
      const decoded: any = jwtDecode(token);
      const roles: string[] = decoded.scope || [];

      if (roles.includes('ROLE_SUBSCRIBER')) {
        console.log('The user is a subscriber.');
        return true;
      } else {
        console.log('The user is not a subscriber.');
        return false;
      }
    } catch (error) {
      console.error('Failed to decode the token:', error);
      return false;
    }
  }
}

import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { UserSessionStore } from '../stores/user.store';
import { AccountDetails, CourseDetails, FriendInfo } from '../models';

export class UserService {
  
  private httpClient = inject(HttpClient);
  private userSessionStore = inject(UserSessionStore);
  // foundFriend!: Observable<FriendInfo>;
  private foundFriendSubject = new BehaviorSubject<FriendInfo | null>(null);
  public foundFriend$ = this.foundFriendSubject.asObservable();

  private addTokenToHeader(): HttpHeaders {
    const token = localStorage.getItem('jwtToken');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  fetchUserDetails(): Observable<AccountDetails> {
    return this.httpClient.get<AccountDetails>('/api/auth/loaduser', { headers: this.addTokenToHeader() })
      .pipe(tap(userDetails => this.userSessionStore.loginSuccess({
        accountDetails: userDetails,
        isAuthenticated: true
      })));
  }

  searchForFriendByEmail(userId: string, email: string) {
    const params = new HttpParams()
        .set('userId', userId)
        .set('email', email);
    this.httpClient.get<FriendInfo>(`/api/user/friend-search`, { headers: this.addTokenToHeader(), params })
      .subscribe({
        next: (data) => { this.foundFriendSubject.next(data);},
        error: (error ) => { console.error(error) },
      });
  }

  get isLoggedIn(): Observable<boolean> {
    return this.userSessionStore.isLoggedIn$;
  }

  get firstName():Observable<string>{
    return this.userSessionStore.firstName$;
  }

  get userProfilePicUrl$(): Observable<string> {
    return this.userSessionStore.select(state => state.accountDetails?.profilePicUrl);
  }

  addRegisteredCourseToUser(userId: string, courseDetails: CourseDetails): Observable<any> {
    return this.httpClient.post(`/api/user/${userId}/courses`, courseDetails, { headers: this.addTokenToHeader() });
  }
}

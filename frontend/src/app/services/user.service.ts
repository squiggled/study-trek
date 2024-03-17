import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { UserSessionStore } from '../stores/user.store';
import { AccountDetails, CourseDetails } from '../models';

export class UserService {
  logout() {
    throw new Error('Method not implemented.');
  }
  private httpClient = inject(HttpClient);
  private userSessionStore = inject(UserSessionStore);

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

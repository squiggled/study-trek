import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject } from '@angular/core';
import { BehaviorSubject, Observable, catchError, tap, throwError, map} from 'rxjs';
import { UserSessionStore } from '../stores/user.store';
import { AccountDetails, CourseDetails, FriendInfo, FriendRequest, Notification, UserPartialUpdate } from '../models';
import { FriendListStore } from '../stores/friends.store';

export class UserService {
  
  private httpClient = inject(HttpClient);
  private userSessionStore = inject(UserSessionStore);
  private friendStore = inject(FriendListStore);
  private foundFriendSubject = new BehaviorSubject<FriendInfo | null>(null);
  public foundFriend$ = this.foundFriendSubject.asObservable();

  private addTokenToHeader(): HttpHeaders {
    const token = localStorage.getItem('jwtToken');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
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

  fetchUserDetails(): Observable<AccountDetails> {
    return this.httpClient.get<AccountDetails>('/api/auth/loaduser', { headers: this.addTokenToHeader() })
      .pipe(
        tap(userDetails => {
          // console.log("Received user details:", userDetails.registeredCourses); 
          this.userSessionStore.loginSuccess({
            accountDetails: userDetails,
            isAuthenticated: true
          });
        })
      );
  }

  addRegisteredCourseToUser(userId: string, courseDetails: CourseDetails): Observable<any> {
    return this.httpClient.post<CourseDetails>(`/api/user/${userId}/courses`, courseDetails, { headers: this.addTokenToHeader() })
    .pipe(
      tap(response => console.log("Response from backend: ", response)),
      catchError(error => {
          console.error("Error adding course: ", error);
          return throwError(() => new Error('Failed to add course'));
      }));
  }

  //friends
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

  addFriend(friendRequest: FriendRequest): Observable<FriendInfo> {
    return this.httpClient.post<FriendInfo>(`/api/user/addfriend`, friendRequest,{ headers: this.addTokenToHeader()});
  }

  getFriends(userId: string): Observable<FriendInfo[]> {
    return this.httpClient.get<FriendInfo[]>(`/api/user/${userId}/friends`, { headers: this.addTokenToHeader() });
  }

  updateFoundFriendStatus(friendId: string, status: string) {
    const currentFriendInfo = this.foundFriendSubject.value;
  
    if (currentFriendInfo && currentFriendInfo.userId === friendId) {
      const updatedFriendInfo = { ...currentFriendInfo, status: status };
  
      console.log("updated status");
      
      this.foundFriendSubject.next(updatedFriendInfo);
    }
  }
  amendFriendRequest(notif: Notification, friendReq: FriendRequest, status: boolean){
    let data = {
      notification: notif,
      friendRequest: friendReq,
      status: status
    }
    return this.httpClient.put<FriendInfo[]>(`/api/user/addfriend/amend`, data, { headers: this.addTokenToHeader() })
    .pipe(
      tap(friendList => {
        // Update the friend list in the store
        this.friendStore.setFriendList(friendList);
      })
    );
  }
  
  uploadPicture(userId: string, croppedBlob: any) {
    console.log("userId " , userId , "croppedImg ", croppedBlob);
    const formData = new FormData();
    formData.append('image', croppedBlob, 'cropped-image.png'); //'image' is name of the field expected by BE
    return this.httpClient.post<any>(`/api/user/${userId}/upload-picture`, formData, { headers: this.addTokenToHeader() }).pipe(
      tap(response => {
        //response contains the new profile picture URL
        const newProfilePicUrl = response.profilePicUrl;
        this.userSessionStore.updateProfilePicUrl(newProfilePicUrl);
      }),
      catchError(error => {
        console.error('Error uploading profile picture:', error);
        return throwError(() => new Error('Error uploading profile picture'));
      })
    );
  }

  updateUserProfile(updatedDetails: { firstName: string; lastName: string; interests: string[] }): Observable<any> {
    const userId = localStorage.getItem('userId');
    
    return this.httpClient.put<UserPartialUpdate>(`/api/user/${userId}/profile`, updatedDetails, { headers: this.addTokenToHeader() })
      .pipe(
        map(response => response as UserPartialUpdate), 
        tap((response: UserPartialUpdate) => {
          this.userSessionStore.updateUserPartialDetails(response);
        })
      );
  }


}

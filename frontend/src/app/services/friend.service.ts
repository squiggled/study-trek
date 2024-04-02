import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FriendInfo } from '../models';

@Injectable({
  providedIn: 'root'
})
export class FriendService {
  constructor(private httpClient: HttpClient) {}

  private addTokenToHeader(): HttpHeaders {
    const token = localStorage.getItem('jwtToken');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getFriends(userId: string): Observable<FriendInfo[]> {
    // console.log("calling get friends");
    return this.httpClient.get<FriendInfo[]>(`/api/user/${userId}/friends`, { headers: this.addTokenToHeader()});
  }

}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FriendInfo } from '../models';

@Injectable({
  providedIn: 'root'
})
export class FriendService {
  constructor(private httpClient: HttpClient) {}

  getFriends(userId: string): Observable<FriendInfo[]> {
    console.log("calling get friends");
    
    return this.httpClient.get<FriendInfo[]>(`/api/user/${userId}/friends`);
  }

}

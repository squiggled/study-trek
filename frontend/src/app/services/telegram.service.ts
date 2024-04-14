import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TelegramService {

  constructor(private http: HttpClient) {}

  private addTokenToHeader(): HttpHeaders {
    const token = localStorage.getItem('jwtToken');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  storeLinkCode(linkCode: string, userId: string): Observable<any> {
    console.log(linkCode, userId);
    return this.http.post('/api/telegram/store-link-code', { linkCode, userId, headers: this.addTokenToHeader() });
  }
}
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ForumThread, ThreadMessage } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ForumService {
  
  private httpClient = inject(HttpClient);

  private addTokenToHeader(): HttpHeaders {
    const token = localStorage.getItem('jwtToken');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }
  
  getAllThreads():Observable<ForumThread[]>{
    return this.httpClient.get<ForumThread[]>(`/api/forum`, { headers: this.addTokenToHeader()});
  }

  createThread(thread: ForumThread): Observable<ForumThread> {
    return this.httpClient.post<ForumThread>(`/api/forum/topic/new`, thread, { headers: this.addTokenToHeader()});
  }

  replyToThread(threadId: string, message: ThreadMessage): Observable<ForumThread> {
    return this.httpClient.post<ForumThread>(`/api/forum/topic/${threadId}/reply`, message,{ headers: this.addTokenToHeader()} );
  }

}

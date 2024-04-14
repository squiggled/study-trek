import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, Subject, tap } from 'rxjs';
import { CommonUtilsService } from './common.utils.service';
import { DomSanitizer } from '@angular/platform-browser';

@Injectable()
export class OpenAIService {
  
  private responseSubject = new Subject<string>();
  response$ = this.responseSubject.asObservable();
  private httpClient = inject(HttpClient);
  private utilsSvc = inject(CommonUtilsService);

  private addTokenToHeader(): HttpHeaders {
    const token = localStorage.getItem('jwtToken');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  processSubmit(prompt: string, number: string): Observable<string> {
    let params = new HttpParams().set('prompt', prompt).set('number', number);
    return this.httpClient
      .get('/api/bot', {
        responseType: 'text',
        params: params,
        headers: this.addTokenToHeader(),
      })
      .pipe(
        tap((data) =>
          this.responseSubject.next(this.utilsSvc.formatResponse(data))
        )
      );
  }
}

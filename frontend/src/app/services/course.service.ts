import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CourseNote } from '../models';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class CourseService {
  private httpClient = inject(HttpClient);
  private addTokenToHeader(): HttpHeaders {
    const token = localStorage.getItem('jwtToken');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  saveNote(newNote: CourseNote): Observable<CourseNote> {
    const courseId = newNote.courseId;
    return this.httpClient.post<CourseNote>(`/api/courses/${courseId}/notes`, newNote, { headers: this.addTokenToHeader() });
  }

  getLatestCourseNoteByCourseId(courseId:number): Observable<CourseNote> {
    return this.httpClient.get<CourseNote>(`/api/courses/${courseId}/notes/latest`, { headers: this.addTokenToHeader() });
}
}

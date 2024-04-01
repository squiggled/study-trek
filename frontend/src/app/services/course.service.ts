import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CourseNote } from '../models';

@Injectable({
  providedIn: 'root',
})
export class CourseService {

  saveNote(courseId: string, userId: string, newNote: any): Observable<CourseNote> {
    throw new Error('Method not implemented.');
  }
  fetchNotes(courseId: string, userId: string): Observable<CourseNote[]> {
    throw new Error('Method not implemented.');
  }
}

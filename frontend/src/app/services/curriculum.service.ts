import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable, catchError, tap, throwError } from "rxjs";
import { Curriculum } from "../models";

@Injectable({
    providedIn: 'root'
  })
export class CurriculumService {
    private httpClient = inject(HttpClient)

    private addTokenToHeader(): HttpHeaders {
        const token = localStorage.getItem('jwtToken');
        return new HttpHeaders().set('Authorization', `Bearer ${token}`);
      }
    
    fetchCurriculum(courseId: number, userId: string): Observable<Curriculum[]> {
      return this.httpClient.get<Curriculum[]>(`/api/courses/${courseId}/curriculum/${userId}`, { headers: this.addTokenToHeader()});
    }
  
    toggleCompletion(userId: string, curriculumId: number, completed: boolean): Observable<any> {
      console.log(`Toggling completion: userId=${userId}, curriculumId=${curriculumId}, completed=${completed}`);
      return this.httpClient.put(`/api/courses/toggle/${userId}/${curriculumId}`, { completed }, { headers: this.addTokenToHeader() })
        .pipe(
          tap(response => console.log("Toggle completion response: ", response)),
          catchError(error => {
            console.error("Error toggling completion", error);
            return throwError(error);
          })
        );
    }
}
  
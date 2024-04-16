import { Injectable, inject } from "@angular/core";
import { CurriculumService } from "../services/curriculum.service";
import { BehaviorSubject, Observable, catchError, tap } from "rxjs";
import { Curriculum } from "../models";

@Injectable({
    providedIn: 'root'
  })
  export class CurriculumStore {
    private _curriculumItems = new BehaviorSubject<Curriculum[]>([]);
    readonly curriculumItems = this._curriculumItems.asObservable();
    private curriculumSvc = inject(CurriculumService);   

    loadCurriculum(courseId: number, userId: string): void {
        console.log(`Loading curriculum for Course ID: ${courseId}, User ID: ${userId}`);
        this.curriculumSvc.fetchCurriculum(courseId, userId).subscribe({
            next: (data) => {
              console.log('Curriculum data loaded:', data);
              this._curriculumItems.next(data);
            },
            error: (error) => console.error('Could not load curriculum.', error)
          });
    }
  
    updateCurriculumItem(userId: string, curriculumId: number, completed: boolean): Observable<any> {
        return this.curriculumSvc.toggleCompletion(userId, curriculumId, completed).pipe(
          tap(() => {
            let items = this._curriculumItems.getValue();
            let index = items.findIndex(item => item.curriculumId === curriculumId);
            if (index !== -1) {
              items[index].completed = !items[index].completed; 
              this._curriculumItems.next([...items]);
            }
          }),
          catchError(error => {
            console.error('Could not update curriculum completion.', error);
            throw error; 
          })
        );
      }

    updateCurriculumItemOptimistic(userId: string, curriculumId: number, completed: boolean): void {
        let items = this._curriculumItems.getValue();
        let index = items.findIndex(item => item.curriculumId === curriculumId);
        if (index !== -1) {
          const updatedItem = {...items[index], completed: completed};
          this._curriculumItems.next([
            ...items.slice(0, index),
            updatedItem,
            ...items.slice(index + 1)
          ]);
        }
      }
  }
  
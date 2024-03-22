// import { Injectable } from "@angular/core";
// import { BehaviorSubject, Observable } from "rxjs";

// @Injectable()
// export class SharedService{
//     private userIdSubject = new BehaviorSubject<string | null>(null);
//     // userId$ = this.userIdSubject.asObservable();

//     setUserId(userId: string | null) {
//         this.userIdSubject.next(userId);
//       }
    
//     get userId$(): Observable<string | null> {
//     return this.userIdSubject.asObservable();
//     }
// }
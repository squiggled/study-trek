import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Router } from "@angular/router";
import { BehaviorSubject, Observable, firstValueFrom } from "rxjs";
import { UserSessionStore } from "../stores/user.store";
import { UserService } from "./user.service";

@Injectable()
export class AuthService{

    private httpClient = inject(HttpClient);
    private router = inject(Router);
    private userSessionStore = inject(UserSessionStore);
    private userSvc = inject(UserService);

    loginFailed: boolean = false;
    loginAttempted: boolean = false;

    private isLoggedInSubject = new BehaviorSubject<boolean>(this.hasToken());
    
    private hasToken(): boolean {
        return !!localStorage.getItem('jwtToken');
    }
    
    //expose the login state
    get isLoggedIn$(): Observable<boolean> {
    return this.isLoggedInSubject.asObservable();
    }

    processLogin(email: any, password: any) {
        this.loginAttempted = true;
        const loginData = { email, password };
        this.httpClient.post<any>('/api/auth/login', loginData, {headers: { 'Content-Type': 'application/json' }})
        .subscribe({
            next:((response: any) => {
                this.loginFailed=false;
                this.loginAttempted = false;
                localStorage.setItem('jwtToken', response.token); //store jwt token
                this.isLoggedInSubject.next(true);

                //update user store
                this.userSessionStore.loginSuccess({
                    accountDetails: response.user, 
                    isAuthenticated: true
                });

                console.log("userdetails" , response.user)
                this.router.navigate(['/'])
            }),
            error:((error: any) => {
                this.loginFailed=true;
                console.error(error);
            })
        })
    }

    processRegister(firstName: string, lastName:string, email:string, password:string){
        const registrationData = { firstName, lastName, email, password };
        this.httpClient.post<any>('/api/auth/register', registrationData , {headers: { 'Content-Type': 'application/json' }})
        .subscribe({
            next:( (response:any) => {
                console.log(response);
                this.processLogin(email, password);
                this.router.navigate(['/'])
            }),
            error: ((error:any) => {
                console.error('Registration or Login Error', error);
            })
        });
    }

    logout(): void {
        localStorage.removeItem('jwtToken');
        this.isLoggedInSubject.next(false);
    }
    
    //initialise login state
    checkTokenOnStartup(): void {
        this.isLoggedInSubject.next(this.hasToken());
    }

}

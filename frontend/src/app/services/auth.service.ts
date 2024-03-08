import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Router } from "@angular/router";
import { firstValueFrom } from "rxjs";

@Injectable()
export class AuthService{

    private httpClient = inject(HttpClient);
    private router = inject(Router);

    loginFailed: boolean = false;
    loginAttempted: boolean = false;

    processLogin(email: any, password: any) {
        this.loginAttempted = true;
        const loginData = { email, password };
        this.httpClient.post<any>('/api/user/login', loginData, {headers: { 'Content-Type': 'application/json' }})
        .subscribe({
            next:((response: any) => {
                console.log("login success");
                this.loginFailed=false;
                this.loginAttempted = false;
                this.router.navigate(['/'])
            }),
            error:((error: any) => {
                this.loginFailed=true;
                console.error(error);
            })
        })
            
        
    }

    processRegister(formData: FormData ){
        console.log("registerdata in auth service", formData);
        
        this.httpClient.post<any>('/api/user/register', formData , {headers: { 'Content-Type': 'application/json' }})
        .subscribe(
            response => console.log(response),
            error => console.error(error)
        );
    }

}

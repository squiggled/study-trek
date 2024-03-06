import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { firstValueFrom } from "rxjs";

@Injectable()
export class AuthService{

    private httpClient = inject(HttpClient);

    processLogin(email: any, password: any) {

        const loginData = { email, password };
        console.log("logindata ", loginData);
        
        this.httpClient.post<any>('/api/user/login', loginData, {
            headers: { 'Content-Type': 'application/json' }
        }).subscribe(response => {
            console.log(response);
        }, error => {
            console.error(error);
        });
        
      }

}

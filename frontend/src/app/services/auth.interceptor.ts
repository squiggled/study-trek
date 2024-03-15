import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const jwtToken = localStorage.getItem('jwtToken');
        if (jwtToken) {
            //clone the request to add the new header
            const clonedRequest = request.clone({
                headers: request.headers.set("Authorization", "Bearer " + jwtToken)
            });

            //pass the cloned request instead of the original request to the next handle
            return next.handle(clonedRequest);
        }

        return next.handle(request);
    }
}

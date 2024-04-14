import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { loadStripe } from '@stripe/stripe-js';
import { catchError, firstValueFrom, tap, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SubscriptionService {
  constructor() {}
  private httpClient = inject(HttpClient);
  stripePromise = loadStripe(environment.stripePublishableKey);
  stripePromiseTest = loadStripe(environment.stripePublishableTest);

  sucessUrl:string = `${window.location.origin}/#/join/subscribe/success`
  cancelUrl:string = `${window.location.origin}/#/join/subscribe/fail`
  
  private addTokenToHeader(): HttpHeaders {
    const token = localStorage.getItem('jwtToken');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  async goToPayment(userId: string, email: string) {
    console.log(
      '>> PaymentService: function called, requesting /api/subscription/create-link'
    );

    const stripe = await this.stripePromiseTest;
    firstValueFrom(
      this.httpClient.post<any>(
        '/api/subscription/create-link',
        {
          userId: userId,
          email:email,
          successUrl: this.sucessUrl,
          cancelUrl: this.cancelUrl,
          // priceId: "price_1OyGWhRovknRUrZ7YpWqQpuK", // stripe price ID
        },
        {
          headers: this.addTokenToHeader(),
        }
      )
    )
      .then((res) => {
        console.log('PaymentService: Response received');
        console.log(res);
        console.log('PaymentService: Redirecting to Stripe Checkout');
        const successUrl = `${window.location.origin}/join/subscribe/success`;
        const cancelUrl = `${window.location.origin}/#/join/subscribe/fail`;
        stripe
          ?.redirectToCheckout({
            sessionId: res.sessionId,
          })
          .then(function (result) {
            if (result.error) {
              alert(result.error.message);
            }
          });
      })
      .catch((err) => {
        console.log('>> PaymentService: Error');
        console.log(err);
      });
  }

  paymentSuccess(userId: string, email:string) {
    console.log('>> PaymentService: function called, requesting /api/subscription/process');
    
    const requestBody = { userId: userId, email: email, successUrl: this.sucessUrl, cancelUrl:this.cancelUrl};
    const headers = this.addTokenToHeader().set(
      'Content-Type',
      'application/json'
    );

    return this.httpClient
      .post<any>('/api/subscription/process', requestBody, { headers: headers })
      .pipe(
        tap((response) => {
          console.log(
            'Response received from /api/subscription/process',
            response
          );
          if (response.token) {
            console.log('Updating local storage with new JWT', response.token);
            localStorage.setItem('jwtToken', response.token);
          }
        }),
        catchError((error) => {
          console.error('Error in paymentSuccess', error);
          return throwError(() => error);
        })
      );
  }
}

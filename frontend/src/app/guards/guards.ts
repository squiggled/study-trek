import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';

export const isLoggedIn: CanActivateFn = (_route, _state) => {
  const authSvc = inject(AuthService);
  const router = inject(Router);
  console.info('ROLE_USER; can proceed');
  if (authSvc.hasRole('ROLE_USER') || authSvc.hasRole('ROLE_SUBSCRIBER')) {
    return true;
  } else {
    router.navigate(['/join/login']);
    return false;
  }
}; 

export const isSubscriber: CanActivateFn = (_route, _state) => {
  const authSvc = inject(AuthService);
  const router = inject(Router);
  
  if (authSvc.isSubscriber()) {
    console.info('ROLE_SUBSCRIBER; can proceed');
    return true;
  } else {
    router.navigate(['/join/subscribe']);
    return false;
  }
}; 

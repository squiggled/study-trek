import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './services/auth.service';
import { inject } from '@angular/core';

export const isLoggedIn: CanActivateFn = (_route, _state) => {
  const authSvc = inject(AuthService);
  const router = inject(Router);
  console.info('in can proceed');
  if (authSvc.hasRole('ROLE_USER') || authSvc.hasRole('ROLE_MEMBER')) {
    return true;
  } else {
    router.navigate(['/join/login']);
    return false;
  }
}; 

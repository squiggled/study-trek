import { CanActivateFn, CanDeactivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';
import { CreateThreadComponent } from '../components/forum/contents/create-thread.component';

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

export const canLeaveCreateThread: CanDeactivateFn<CreateThreadComponent> = 
(comp, _route, _state) => {
  if (comp.createThreadForm.dirty && !comp.isSubmitting) {
    return confirm('Would you like to discard your post?');
  }
  return true;
};

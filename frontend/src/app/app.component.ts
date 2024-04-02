import { Component, OnInit, inject } from '@angular/core';
import { AuthService } from './services/auth.service';
import { UserSessionStore } from './stores/user.store';
import { UserService } from './services/user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  title = 'frontend';
 
  private authSvc = inject(AuthService);
  private userSvc = inject(UserService);
  private userSessionStore = inject(UserSessionStore)

  ngOnInit(): void {
    this.authSvc.checkTokenOnStartup();
    if (localStorage.getItem('jwtToken')) {
      this.userSvc.fetchUserDetails().subscribe({
        next: (details) => {
          localStorage.setItem('accountDetails', JSON.stringify(details));
          localStorage.setItem('isLoggedIn', 'true');

          this.userSessionStore.updateUserDetails(details);
        },
        error: (error) => {
          console.error('Failed to fetch user details', error);
          
          if (error.status === 401) {
            this.authSvc.logout();
          }
        }
      });
    }
  }
  
}

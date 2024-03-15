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
          // Assuming you have a method to update user details in UserSessionStore
          this.userSessionStore.updateUserDetails(details);
        },
        error: (error) => {
          console.error('Failed to fetch user details', error);
          // If you get a 401, it means the token might be expired or invalid
          if (error.status === 401) {
            this.authSvc.logout(); // Clears the local storage and updates auth state
          }
        }
      });
    }
  }
  
}

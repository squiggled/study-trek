import { Component, inject } from '@angular/core';
import { ThemeService } from '../../services/theme.service';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { Observable } from 'rxjs';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})

export class NavbarComponent {
  themeSvc = inject(ThemeService);
  private router = inject(Router);
  private userSvc = inject(UserService);
  private authSvc = inject(AuthService);


  isLoggedIn$: Observable<boolean> = this.userSvc.isLoggedIn;
  userProfilePicUrl$ = this.userSvc.userProfilePicUrl$;
  firstName$ = this.userSvc.firstName;

  ngOnInit(): void {
    this.isLoggedIn$ = this.authSvc.isLoggedIn$;
  }
  
  get isDarkMode(): boolean {
    return document.body.classList.contains('dark');
  }
  get userFirstName(){
    return 
  }
  toggleTheme(): void {
    this.themeSvc.toggleTheme();
  }

  login(){
    this.router.navigate(['/join/login']);
  }

  register(){
    this.router.navigate(['/join/register'])
  }

  logout() {
    this.authSvc.logout(); 
    this.router.navigate(['/']);
  }

  navigateToMyCourses(){
    this.router.navigate(['/home/my-courses']);
  }
}

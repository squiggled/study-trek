import { Component, inject } from '@angular/core';
import { ThemeService } from '../../services/theme.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})

export class NavbarComponent {
  themeSvc = inject(ThemeService);
  private router = inject(Router);

  get isDarkMode(): boolean {
    return document.body.classList.contains('dark');
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
}

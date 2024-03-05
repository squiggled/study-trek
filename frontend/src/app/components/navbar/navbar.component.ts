import { Component, inject } from '@angular/core';
import { ThemeService } from '../../services/theme.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})

export class NavbarComponent {
  themeSvc = inject(ThemeService);

  get isDarkMode(): boolean {
    return document.body.classList.contains('dark');
  }

  toggleTheme(): void {
    this.themeSvc.toggleTheme();
  }
}

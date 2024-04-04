// theme.service.ts
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  private themeKey = 'theme';
  
  constructor() {
    this.loadTheme();
  }

  toggleTheme(): void {
    console.log('Before toggle:', document.body.classList.contains('dark'));
    if (document.body.classList.contains('dark')) {
      document.body.classList.remove('dark');
      localStorage.setItem(this.themeKey, 'light');
    } else {
      document.body.classList.add('dark');
      localStorage.setItem(this.themeKey, 'dark');
    }
    console.log('After toggle:', document.body.classList.contains('dark'));
  }

  private loadTheme(): void {
    // Try to load the theme from localStorage
    const storedTheme = localStorage.getItem(this.themeKey);
    
    // If no theme is stored, use system preference
    if (storedTheme === null) {
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      document.body.classList.toggle('dark', prefersDark);
      localStorage.setItem(this.themeKey, prefersDark ? 'dark' : 'light');
    } else {
      // Apply the stored theme
      document.body.classList.toggle('dark', storedTheme === 'dark');
    }
  }
}

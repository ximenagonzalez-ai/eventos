import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private currentTheme: string = 'dark';

  constructor() {
    const savedTheme = localStorage.getItem('vibe-theme');
    if (savedTheme) {
      this.setTheme(savedTheme);
    } else {
      this.setTheme('dark');
    }
  }

  setTheme(theme: string): void {
    this.currentTheme = theme;
    localStorage.setItem('vibe-theme', theme);
    document.documentElement.setAttribute('data-theme', theme);
  }

  getTheme(): string {
    return this.currentTheme;
  }
}

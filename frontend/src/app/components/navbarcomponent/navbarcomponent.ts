import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ThemeService } from '../../services/themeservice';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbarcomponent.html',
  styleUrls: ['./navbarcomponent.css'],
  standalone: false
})
export class NavbarComponent {
  searchQuery: string = '';
  isLoggedIn: boolean = false;
  isDropdownOpen: boolean = false;
  isCustomizerOpen: boolean = false;

  constructor(
    private router: Router,
    private themeService: ThemeService
  ) {}

  onSearch(): void {
    if (this.searchQuery.trim()) {
      console.log('Buscando:', this.searchQuery);
    }
  }

  toggleDropdown(event: Event): void {
    event.stopPropagation();
    this.isDropdownOpen = !this.isDropdownOpen;
    this.isCustomizerOpen = false;
  }

  toggleCustomizer(event: Event): void {
    event.stopPropagation();
    this.isCustomizerOpen = !this.isCustomizerOpen;
    this.isDropdownOpen = false;
  }

  changeTheme(theme: string): void {
    this.themeService.setTheme(theme);
    this.isCustomizerOpen = false;
  }

  logout(event: Event): void {
    event.preventDefault();
    localStorage.removeItem('token');
    this.isLoggedIn = false;
    this.router.navigate(['/login']);
  }
}

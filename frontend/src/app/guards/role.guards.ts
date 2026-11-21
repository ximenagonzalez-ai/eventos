import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

function tieneSesion(): boolean {
  if (typeof localStorage === 'undefined') return false;
  const token = localStorage.getItem('token');
  return !!token && token !== 'null' && token !== 'undefined';
}

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  return tieneSesion() || router.createUrlTree(['/login']);
};

export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);
  if (!tieneSesion()) return router.createUrlTree(['/login']);

  const rol = localStorage.getItem('rol') || localStorage.getItem('role');
  return rol === 'ADMIN' || router.createUrlTree(['/']);
};

export type Rol = 'USER' | 'ADMIN' | 'INCOGNITO';

export interface Usuario {
  id?: number;
  nombre: string;
  email: string;
  contrasena?: string;
  rol: Rol;
  activo: boolean;
  emailVerificado?: boolean;
  fechaCreacion?: string;
}

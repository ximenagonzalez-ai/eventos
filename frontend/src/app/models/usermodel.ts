export type RolUsuario = 'USER' | 'ADMIN';

export interface Usuario {
  id: number;
  nombre: string;
  email: string;
  rol: RolUsuario;
  verificado?: boolean;
}

export interface LoginRequest {
  email: string;
  contrasena: string;
}

export interface LoginResponse {
  token: string;
  role: string;
}

export interface RegistroRequest {
  nombre: string;
  email: string;
  contrasena: string;
}

export interface OlvidePasswordRequest {
  email: string;
}

export interface RestablecimientoPasswordRequest {
  token: string;
  nuevaContrasena: string;
}

export interface ReenviarVerificacionRequest {
  email: string;
}

export interface AuthMessageResponse {
  codigo: string;
  mensaje: string;
}

export type VerificacionResponse = AuthMessageResponse;

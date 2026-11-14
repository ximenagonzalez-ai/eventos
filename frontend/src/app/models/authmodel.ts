export interface RegistroRequest {
  nombre: string;
  email: string;
  contrasena: string;
}

export interface LoginRequest {
  email: string;
  contrasena: string;
}

export interface VerificacionResponse {
  codigo: 'VERIFICADO' | 'TOKEN_INVALIDO' | 'TOKEN_USADO' | 'TOKEN_EXPIRADO';
  mensaje: string;
}

export interface ReenviarVerificacionRequest {
  email: string;
}

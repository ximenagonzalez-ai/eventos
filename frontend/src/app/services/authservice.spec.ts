import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './authservice';

describe('AuthService', () => {
  let service: AuthService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('shares an in-flight email verification request for the same token', () => {
    const responses: string[] = [];

    service.verificarEmail('token-prueba').subscribe((response) => responses.push(response.mensaje));
    service.verificarEmail('token-prueba').subscribe((response) => responses.push(response.mensaje));

    const request = httpTestingController.expectOne((req) =>
      req.url === 'http://localhost:8080/api/auth/verificar-email'
      && req.params.get('token') === 'token-prueba');
    expect(request.request.method).toBe('POST');

    request.flush({ codigo: 'VERIFICADO', mensaje: 'Correo verificado.' });

    service.verificarEmail('token-prueba').subscribe((response) => responses.push(response.mensaje));
    httpTestingController.expectNone((req) =>
      req.url === 'http://localhost:8080/api/auth/verificar-email'
      && req.params.get('token') === 'token-prueba');

    expect(responses).toEqual([
      'Correo verificado.',
      'Correo verificado.',
      'Correo verificado.',
    ]);
  });
});

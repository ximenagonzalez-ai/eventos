import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, ParamMap, RouterModule, convertToParamMap } from '@angular/router';
import { BehaviorSubject, Observable, TimeoutError, of, throwError } from 'rxjs';
import { AuthMessageResponse } from '../../models/authmodel';
import { AuthService } from '../../services/authservice';
import { VerificacionComponent } from './verificacioncomponent';

describe('VerificacionComponent', () => {
  let component: VerificacionComponent;
  let fixture: ComponentFixture<VerificacionComponent>;
  let queryParamMap$: BehaviorSubject<ParamMap>;
  let verificationResponse$: Observable<AuthMessageResponse>;
  let verificationCalls: number;

  beforeEach(async () => {
    queryParamMap$ = new BehaviorSubject(convertToParamMap({ token: 'token-prueba' }));
    verificationResponse$ = of({ codigo: 'VERIFICADO', mensaje: 'Correo verificado.' });
    verificationCalls = 0;

    await TestBed.configureTestingModule({
      declarations: [VerificacionComponent],
      imports: [CommonModule, RouterModule.forRoot([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { queryParamMap: queryParamMap$ },
        },
        {
          provide: AuthService,
          useValue: {
            verificarEmail: () => {
              verificationCalls += 1;
              return verificationResponse$;
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(VerificacionComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('shows the backend message and stops loading after an HTTP error', () => {
    verificationResponse$ = throwError(() => new HttpErrorResponse({
      status: 400,
      error: { codigo: 'TOKEN_USADO', mensaje: 'Este enlace ya fue utilizado' },
    }));

    fixture.detectChanges();

    expect(component.cargando).toBe(false);
    expect(component.error).toBe(true);
    expect(component.mensaje).toBe('Este enlace ya fue utilizado');
    expect(component.codigoError).toBe('TOKEN_USADO');
    expect(fixture.nativeElement.textContent).toContain('Este enlace ya fue utilizado');
    expect(fixture.nativeElement.textContent).toContain('TOKEN_USADO');
  });

  it('shows a fallback when the HTTP error has no message body', () => {
    verificationResponse$ = throwError(() => new HttpErrorResponse({ status: 400 }));

    fixture.detectChanges();

    expect(component.cargando).toBe(false);
    expect(component.error).toBe(true);
    expect(component.mensaje).toContain('no ser válido');
  });

  it('stops loading and shows a timeout message when the request does not respond', () => {
    verificationResponse$ = throwError(() => new TimeoutError());

    fixture.detectChanges();

    expect(component.cargando).toBe(false);
    expect(component.error).toBe(true);
    expect(component.mensaje).toContain('tardó demasiado');
  });

  it('does not verify the same token again when route params emit again', () => {
    fixture.detectChanges();
    queryParamMap$.next(convertToParamMap({ token: 'token-prueba' }));

    expect(verificationCalls).toBe(1);
  });
});

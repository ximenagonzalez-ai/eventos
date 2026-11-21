import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { of, throwError } from 'rxjs';
import { CategoriaService } from '../../services/categoriaservice';
import { AdministracionComponent } from './administracioncomponent';

describe('AdministracionComponent', () => {
  let fixture: ComponentFixture<AdministracionComponent>;
  let component: AdministracionComponent;
  let categoriaService: {
    obtenerTodas: ReturnType<typeof vi.fn>;
    crear: ReturnType<typeof vi.fn>;
    actualizar: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    categoriaService = {
      obtenerTodas: vi.fn().mockReturnValue(of([])),
      crear: vi.fn().mockReturnValue(of('Categoría creada')),
      actualizar: vi.fn().mockReturnValue(of('Categoría actualizada'))
    };

    await TestBed.configureTestingModule({
      declarations: [AdministracionComponent],
      imports: [CommonModule, FormsModule, RouterModule.forRoot([])],
      providers: [{ provide: CategoriaService, useValue: categoriaService }]
    }).compileComponents();

    fixture = TestBed.createComponent(AdministracionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('creates a trimmed event category and refreshes the list', () => {
    component.nombre = '  Música en vivo  ';
    component.descripcion = '  Conciertos y festivales  ';

    component.guardarCategoria();

    expect(categoriaService.crear).toHaveBeenCalledWith({
      nombre: 'Música en vivo',
      descripcion: 'Conciertos y festivales'
    });
    expect(categoriaService.obtenerTodas).toHaveBeenCalledTimes(2);
    expect(component.mensaje).toBe('Categoría creada');
    expect(component.guardando).toBe(false);
  });

  it('renders the backend error when a category name already exists', () => {
    categoriaService.crear.mockReturnValueOnce(throwError(() => new HttpErrorResponse({
      status: 409,
      error: 'El nombre ya está en uso'
    })));
    component.nombre = 'Concierto';

    component.guardarCategoria();
    fixture.detectChanges();

    expect(component.error).toBe('El nombre ya está en uso');
    expect(component.guardando).toBe(false);
    expect(fixture.nativeElement.textContent).toContain('El nombre ya está en uso');
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VerificacionComponent } from './verificacioncomponent';

describe('VerificacionComponent', () => {
  let component: VerificacionComponent;
  let fixture: ComponentFixture<VerificacionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerificacionComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(VerificacionComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

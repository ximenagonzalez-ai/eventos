import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MiBoletaComponent } from './miboletacomponent';

describe('MiBoletaComponent', () => {
  let component: MiBoletaComponent;
  let fixture: ComponentFixture<MiBoletaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MiBoletaComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(MiBoletaComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

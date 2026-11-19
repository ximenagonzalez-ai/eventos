import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RutaComponent } from './rutacomponent';

describe('RutaComponent', () => {
  let component: RutaComponent;
  let fixture: ComponentFixture<RutaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RutaComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(RutaComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

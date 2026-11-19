import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EventoDetalleComponent } from './eventodetallecomponent';

describe('EventoDetalleComponent', () => {
  let component: EventoDetalleComponent;
  let fixture: ComponentFixture<EventoDetalleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventoDetalleComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EventoDetalleComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

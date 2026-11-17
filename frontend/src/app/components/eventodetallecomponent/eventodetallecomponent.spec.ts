import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Eventodetallecomponent } from './eventodetallecomponent';

describe('Eventodetallecomponent', () => {
  let component: Eventodetallecomponent;
  let fixture: ComponentFixture<Eventodetallecomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Eventodetallecomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Eventodetallecomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

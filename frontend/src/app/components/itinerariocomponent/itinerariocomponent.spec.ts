import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ItinerarioComponent } from './itinerariocomponent';

describe('ItinerarioComponent', () => {
  let component: ItinerarioComponent;
  let fixture: ComponentFixture<ItinerarioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ItinerarioComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ItinerarioComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

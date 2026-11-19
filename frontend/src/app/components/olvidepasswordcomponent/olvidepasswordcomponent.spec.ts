import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OlvidePasswordComponent } from './olvidepasswordcomponent';

describe('OlvidePasswordComponent', () => {
  let component: OlvidePasswordComponent;
  let fixture: ComponentFixture<OlvidePasswordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OlvidePasswordComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(OlvidePasswordComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

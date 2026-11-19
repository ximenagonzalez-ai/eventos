import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RestablecerPasswordComponent } from './restablecerpasswordcomponent';

describe('RestablecerPasswordComponent', () => {
  let component: RestablecerPasswordComponent;
  let fixture: ComponentFixture<RestablecerPasswordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RestablecerPasswordComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(RestablecerPasswordComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

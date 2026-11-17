import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Restablecerpasswordcomponent } from './restablecerpasswordcomponent';

describe('Restablecerpasswordcomponent', () => {
  let component: Restablecerpasswordcomponent;
  let fixture: ComponentFixture<Restablecerpasswordcomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Restablecerpasswordcomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Restablecerpasswordcomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

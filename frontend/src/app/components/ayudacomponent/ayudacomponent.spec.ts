import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Ayudacomponent } from './ayudacomponent';

describe('Ayudacomponent', () => {
  let component: Ayudacomponent;
  let fixture: ComponentFixture<Ayudacomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Ayudacomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Ayudacomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

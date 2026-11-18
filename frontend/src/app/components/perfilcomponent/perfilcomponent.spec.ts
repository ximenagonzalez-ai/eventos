import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Perfilcomponent } from './perfilcomponent';

describe('Perfilcomponent', () => {
  let component: Perfilcomponent;
  let fixture: ComponentFixture<Perfilcomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Perfilcomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Perfilcomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

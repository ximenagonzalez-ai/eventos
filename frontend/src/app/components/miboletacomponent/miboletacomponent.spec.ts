import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Miboletacomponent } from './miboletacomponent';

describe('Miboletacomponent', () => {
  let component: Miboletacomponent;
  let fixture: ComponentFixture<Miboletacomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Miboletacomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Miboletacomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

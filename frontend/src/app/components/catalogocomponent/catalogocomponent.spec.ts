import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Catalogocomponent } from './catalogocomponent';

describe('Catalogocomponent', () => {
  let component: Catalogocomponent;
  let fixture: ComponentFixture<Catalogocomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Catalogocomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Catalogocomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

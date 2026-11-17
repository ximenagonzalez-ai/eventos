import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Olvidepasswordcomponent } from './olvidepasswordcomponent';

describe('Olvidepasswordcomponent', () => {
  let component: Olvidepasswordcomponent;
  let fixture: ComponentFixture<Olvidepasswordcomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Olvidepasswordcomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Olvidepasswordcomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

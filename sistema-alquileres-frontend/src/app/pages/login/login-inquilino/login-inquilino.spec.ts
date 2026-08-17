import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginInquilino } from './login-inquilino';

describe('LoginInquilino', () => {
  let component: LoginInquilino;
  let fixture: ComponentFixture<LoginInquilino>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginInquilino],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginInquilino);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

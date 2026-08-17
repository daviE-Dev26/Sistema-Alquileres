import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginPropietario } from './login-propietario';

describe('LoginPropietario', () => {
  let component: LoginPropietario;
  let fixture: ComponentFixture<LoginPropietario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginPropietario],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginPropietario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

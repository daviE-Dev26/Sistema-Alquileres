import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Documento } from './documento';

describe('Documento', () => {
  let component: Documento;
  let fixture: ComponentFixture<Documento>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Documento],
    }).compileComponents();

    fixture = TestBed.createComponent(Documento);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalMetricas } from './modal-metricas';

describe('ModalMetricas', () => {
  let component: ModalMetricas;
  let fixture: ComponentFixture<ModalMetricas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalMetricas],
    }).compileComponents();

    fixture = TestBed.createComponent(ModalMetricas);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

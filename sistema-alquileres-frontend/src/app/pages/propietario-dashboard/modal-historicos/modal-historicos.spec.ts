import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalHistoricos } from './modal-historicos';

describe('ModalHistoricos', () => {
  let component: ModalHistoricos;
  let fixture: ComponentFixture<ModalHistoricos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalHistoricos],
    }).compileComponents();

    fixture = TestBed.createComponent(ModalHistoricos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

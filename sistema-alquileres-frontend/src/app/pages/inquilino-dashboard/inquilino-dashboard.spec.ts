import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InquilinoDashboard } from './inquilino-dashboard';

describe('InquilinoDashboard', () => {
  let component: InquilinoDashboard;
  let fixture: ComponentFixture<InquilinoDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InquilinoDashboard],
    }).compileComponents();

    fixture = TestBed.createComponent(InquilinoDashboard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

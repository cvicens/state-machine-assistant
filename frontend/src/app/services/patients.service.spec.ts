import { TestBed } from '@angular/core/testing';

import { PatientsService } from './patients.service';

describe('PatientsServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: PatientsService = TestBed.get(PatientsService);
    expect(service).toBeTruthy();
  });
});

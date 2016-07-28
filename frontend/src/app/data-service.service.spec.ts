/* tslint:disable:no-unused-variable */

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';
import { DataService } from './data-service.service';

describe('DataService Service', () => {
  beforeEachProviders(() => [DataService]);

  it('should ...',
      inject([DataService], (service: DataService) => {
    expect(service).toBeTruthy();
  }));
});

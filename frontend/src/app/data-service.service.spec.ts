/* tslint:disable:no-unused-variable */

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';
import { DataServiceService } from './data-service.service';

describe('DataService Service', () => {
  beforeEachProviders(() => [DataServiceService]);

  it('should ...',
      inject([DataServiceService], (service: DataServiceService) => {
    expect(service).toBeTruthy();
  }));
});

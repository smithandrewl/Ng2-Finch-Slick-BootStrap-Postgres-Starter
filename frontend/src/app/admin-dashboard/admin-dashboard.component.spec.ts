/* tslint:disable:no-unused-variable */

import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';

import { AdminDashboardComponent } from './admin-dashboard.component';

describe('Component: AdminDashboard', () => {
  it('should create an instance', () => {
    let component = new AdminDashboardComponent();
    expect(component).toBeTruthy();
  });
});

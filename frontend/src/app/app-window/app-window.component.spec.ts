/* tslint:disable:no-unused-variable */

import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';

import { AppWindowComponent } from './app-window.component';

describe('Component: AppWindow', () => {
  it('should create an instance', () => {
    let component = new AppWindowComponent();
    expect(component).toBeTruthy();
  });
});

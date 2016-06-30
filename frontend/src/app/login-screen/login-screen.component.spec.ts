/* tslint:disable:no-unused-variable */

import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';

import { LoginScreenComponent } from './login-screen.component';

describe('Component: LoginScreen', () => {
  it('should create an instance', () => {
    let component = new LoginScreenComponent();
    expect(component).toBeTruthy();
  });
});

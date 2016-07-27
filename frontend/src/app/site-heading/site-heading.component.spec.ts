/* tslint:disable:no-unused-variable */

import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';

import { SiteHeadingComponent } from './site-heading.component';

describe('Component: SiteHeading', () => {
  it('should create an instance', () => {
    let component = new SiteHeadingComponent();
    expect(component).toBeTruthy();
  });
});

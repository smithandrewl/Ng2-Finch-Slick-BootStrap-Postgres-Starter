import {
  beforeEachProviders,
  describe,
  expect,
  it,
  inject
} from '@angular/core/testing';
import { FrontendAppComponent } from '../app/frontend.component';

beforeEachProviders(() => [FrontendAppComponent]);

describe('App: Frontend', () => {
  it('should create the app',
      inject([FrontendAppComponent], (app: FrontendAppComponent) => {
    expect(app).toBeTruthy();
  }));

  it('should have as title \'frontend works!\'',
      inject([FrontendAppComponent], (app: FrontendAppComponent) => {
    expect(app.title).toEqual('frontend works!');
  }));
});

import {
  beforeEachProviders,
  describe,
  expect,
  it,
  inject
} from '@angular/core/testing';
import { LoginAppComponent } from './login.component';

beforeEachProviders(() => [LoginAppComponent]);

describe('App: Frontend', () => {
  it('should create the app',
      inject([LoginAppComponent], (app: LoginAppComponent) => {
    expect(app).toBeTruthy();
  }));

  it('should have as title \'frontend works!\'',
      inject([LoginAppComponent], (app: LoginAppComponent) => {
    expect(app.title).toEqual('frontend works!');
  }));
});

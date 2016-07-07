import { bootstrap } from '@angular/platform-browser-dynamic';
import { enableProdMode } from '@angular/core';
import { LoginAppComponent, environment } from './app/';
import { LoginScreenComponent} from './app/login-screen/';
import {HTTP_PROVIDERS, Http} from '@angular/http';
import { provideRouter } from '@angular/router';
import { AppRoutes } from './app/app.routes';

if (environment.production) {
  enableProdMode();
}

bootstrap(LoginScreenComponent, [
  provideRouter(AppRoutes),
  HTTP_PROVIDERS
]);


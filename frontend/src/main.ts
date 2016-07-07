import { bootstrap } from '@angular/platform-browser-dynamic';
import { enableProdMode } from '@angular/core';
import { LoginAppComponent, environment } from './app/';
import { LoginScreenComponent} from './app/login-screen/';
import {HTTP_PROVIDERS, Http} from '@angular/http';
import { provideRouter } from '@angular/router';
import { AppRoutes } from './app/app.routes';
import {AppWindowComponent} from './app/app-window';

if (environment.production) {
  enableProdMode();
}

bootstrap(AppWindowComponent, [
  provideRouter(AppRoutes),
  HTTP_PROVIDERS
]);


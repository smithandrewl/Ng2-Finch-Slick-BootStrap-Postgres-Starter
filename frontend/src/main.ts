import { bootstrap } from '@angular/platform-browser-dynamic';
import { enableProdMode } from '@angular/core';
import { LoginAppComponent, environment } from './app/';
import { LoginScreenComponent} from './app/login-screen/';
import { AdminDashboardComponent} from './app/admin-dashboard';
import {HTTP_PROVIDERS, Http} from '@angular/http';

if (environment.production) {
  enableProdMode();
}

bootstrap(AdminDashboardComponent, [HTTP_PROVIDERS]);


import { bootstrap } from '@angular/platform-browser-dynamic';
import { enableProdMode } from '@angular/core';
import { LoginAppComponent, environment } from './app/';
import {HTTP_PROVIDERS, Http} from '@angular/http';

if (environment.production) {
  enableProdMode();
}

bootstrap(LoginAppComponent, [HTTP_PROVIDERS]);


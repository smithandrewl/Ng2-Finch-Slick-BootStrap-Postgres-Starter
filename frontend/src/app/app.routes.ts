import { provideRouter, RouterConfig } from '@angular/router';
import AdminDashboardComponent from './admin-dashboard';

export const routes: RouterConfig = [
    { path: 'admin-dash', component: AdminDashboardComponent },
];

export const APP_ROUTER_PROVIDERS = [
    provideRouter(routes)
];

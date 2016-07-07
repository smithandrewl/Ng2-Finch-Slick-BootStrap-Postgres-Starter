import { LoginScreenComponent} from './login-screen';
import { AdminDashboardComponent} from './admin-dashboard';

export const AppRoutes = [
    { path: '', component: LoginScreenComponent },
    { path: 'admin', component: AdminDashboardComponent }
];
import { LoginScreenComponent} from './login-screen';
import { AdminDashboardComponent} from './admin-dashboard';
import {HomeComponent} from "./home/home.component";
import {AdminCreateUserComponent} from "./admin-create-user/admin-create-user.component";

export const AppRoutes = [
    { path: '', component: LoginScreenComponent },
    { path: 'admin', component: AdminDashboardComponent },
    { path: 'home', component: HomeComponent },
    { path: 'create-user', component: AdminCreateUserComponent}
];
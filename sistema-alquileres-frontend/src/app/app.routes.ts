import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { LoginInquilino } from './pages/login/login-inquilino/login-inquilino';
import { LoginPropietario } from './pages/login/login-propietario/login-propietario';
import { LoginAdmin } from './pages/login/login-admin/login-admin';
import { AdminDashboard } from './pages/admin-dashboard/admin-dashboard';
import { PropietarioDashboard } from './pages/propietario-dashboard/propietario-dashboard';
import { RegistroPropietario } from './pages/registro/registro-propietario/registro-propietario';
import { InquilinoDashboard } from './pages/inquilino-dashboard/inquilino-dashboard';

export const routes: Routes = [
    { path:'', component:Home},
    { path: 'login/propietario', component: LoginPropietario },
    { path: 'login/inquilino', component: LoginInquilino },
    { path: 'login/admin', component: LoginAdmin },
    { path: 'admin-dashboard', component: AdminDashboard },
    { path: 'propietario-dashboard', component:PropietarioDashboard},
    {path:'registro/propietario',component:RegistroPropietario},
    { path: 'inquilino-dashboard', component: InquilinoDashboard }]
    

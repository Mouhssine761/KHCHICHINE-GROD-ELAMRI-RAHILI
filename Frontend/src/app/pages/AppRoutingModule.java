import { NgModule }            from '@angular/core';
import { RouterModule, Routes }from '@angular/router';
import { HomeComponent }       from './pages/home/home.component';
import { LoginComponent }      from './pages/login/login.component';
import { RegisterComponent }   from './pages/register/register.component';

const routes: Routes = [
  { path: '',         component: HomeComponent },       // “/”
  { path: 'login',    component: LoginComponent },      // “/login”
  { path: 'register', component: RegisterComponent },   // “/register”
  { path: '**',       redirectTo: '' }                  // fallback
];

@NgModule({
  imports:  [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}

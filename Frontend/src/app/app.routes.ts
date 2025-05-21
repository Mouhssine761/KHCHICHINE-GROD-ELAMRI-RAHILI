import { Routes } from '@angular/router';
import {LoginComponent} from './pages/login/login.component';
import {RegisterComponent} from './pages/register/register.component';
import {HistoryComponent} from './pages/history/history.component';
import {HomeComponent} from './pages/home/home.component';
import {DecisionmakingComponent} from './pages/decisionmaking/decisionmaking.component';
import { AuthGuard }           from './auth.guard';


export const routes: Routes = [
  {
    path:'',
    redirectTo:'home',
    pathMatch:'full'
  },
  {
    path:'login',
    component:LoginComponent
  },
  {
    path:'home',
    component:HomeComponent
  },
  {
    path:'register',
    component:RegisterComponent
  },
  {
    path:'history',
    component:HistoryComponent,
    canActivate: [AuthGuard]
  },
  {
    path:'decisionmaking',
    component:DecisionmakingComponent,
    canActivate: [AuthGuard]
  },
];

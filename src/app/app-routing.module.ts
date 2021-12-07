import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {RegisterComponent} from "./register/register.component";
import {ActivateComponent} from "./activate/activate.component";

const routes: Routes = [
  {path: 'account/signup', component: RegisterComponent},
  {path: 'account/activate', component: ActivateComponent},
  {path: 'account/login', component: LoginComponent},
  {path: 'account/logout', component: LoginComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {

}


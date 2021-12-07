import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { UserComponent } from './user/user.component';
import {UserService} from "./user/user.service";
import { LoginComponent } from './login/login.component';
import {ReactiveFormsModule} from "@angular/forms";
import {LoginService} from "./login/login.service";
import { RegisterComponent } from './register/register.component';
import {RegisterService} from "./register/register.service";
import {HttpClientModule} from "@angular/common/http";
import { ActivateComponent } from './activate/activate.component';
import {ActivateService} from "./activate/activate.service";

@NgModule({
  declarations: [
    AppComponent,
    UserComponent,
    LoginComponent,
    RegisterComponent,
    ActivateComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [
    UserService,
    LoginService,
    RegisterService,
    ActivateService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

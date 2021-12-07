import { Component, OnInit } from '@angular/core';
import {LoginService} from "./login.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  submitted = false;
  constructor(private loginService: LoginService) { }

  ngOnInit(): void {
  }
  public submit(): void {
    this.submitted = true;
    this.loginService.doLogin();
  }

}

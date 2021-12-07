import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms'
import {RegisterService} from "./register.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm: any;
  public success = false;

  constructor(private formBuilder: FormBuilder, private registerService: RegisterService, private router: Router) {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(50), Validators.email]],
      password: ['',[Validators.required, Validators.minLength(4), Validators.maxLength(50)]]
    });
  }

  ngOnInit(): void {
  }

  register(): void{
    this.registerForm.langkey= 'en';
    this.registerService.processRegistration(JSON.stringify(this.registerForm.value))
      .then(response => {
        console.log(response);
      }).catch(error => {
        console.log(error);
    });
  }
}


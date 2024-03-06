import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit{

  constructor (private fb:FormBuilder){}
  private authSvc = inject(AuthService);
  loginForm!:FormGroup;

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: this.fb.control<string>('', [Validators.required, Validators.email]),
      password: this.fb.control<string>('', [Validators.required])
    })
  }

  processLogin(){
    if (this.loginForm.valid){
      // console.log(this.loginForm.value.email, this.loginForm.value.password);
      this.authSvc.processLogin(this.loginForm.value.email, this.loginForm.value.password)
    }
    
  }


}

import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit{

  loginForm!:FormGroup;
  constructor (private fb:FormBuilder, private titleService: Title){}
  passwordVisible: boolean = false;

  private authSvc = inject(AuthService);
 
  ngOnInit(): void {
    this.titleService.setTitle('Study Trek | Login');
    this.loginForm = this.fb.group({
      email: this.fb.control<string>('', [Validators.required, Validators.email]),
      password: this.fb.control<string>('', [Validators.required])
    })
  }

  processLogin(){
    if (this.loginForm.valid){
      this.authSvc.processLogin(this.loginForm.value.email, this.loginForm.value.password)
    }
  }

  getLoginFailed(): boolean {
    return this.authSvc.loginFailed;
  }

  getLoginAttempted(): boolean {
    return this.authSvc.loginAttempted;
  }

  togglePasswordVisibility(): void {
    this.passwordVisible = !this.passwordVisible;
  }

}

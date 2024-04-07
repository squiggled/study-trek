import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CommonUtilsService } from '../../services/common.utils.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit{

  registerForm!:FormGroup;
  constructor (private fb:FormBuilder, private titleService: Title){}

  private authSvc = inject(AuthService);
  private utilsSvc = inject(CommonUtilsService);

  ngOnInit(): void {
    this.titleService.setTitle('Study Trek | Sign Up');
    this.registerForm = this.fb.group({
      firstName: this.fb.control<string>('', [Validators.required]),
      lastName: this.fb.control<string>('', [Validators.required]),
      email: this.fb.control<string>('', [Validators.required, Validators.email]),
      password: this.fb.control<string>('', [Validators.required, this.utilsSvc.passwordValidator()]),
      confirmPassword: this.fb.control<string>('', [Validators.required])
    }, { validators: this.checkPasswordsEqual });
  }

  checkPasswordsEqual(group: FormGroup) { 
    let pass = group.get('password')?.value;
    let confirmPass = group.get('confirmPassword')?.value;
    return pass === confirmPass ? null : { passwordMismatch: true } //If pass match, returns null (no error) / if fail, return an object
    //use this obj to conditionally render html
  }

  processRegistration(){
    if (this.registerForm.valid){
      let formData = this.registerForm.value;
      // console.log(formData);
      let firstName:string = formData.firstName;
      let lastName:string = formData.lastName;
      let email:string = formData.email;
      let password:string = formData.password;
      this.authSvc.processRegister(firstName, lastName, email, password);
    }
  }

}

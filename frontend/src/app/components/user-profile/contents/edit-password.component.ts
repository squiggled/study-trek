import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { CommonUtilsService } from '../../../services/common.utils.service';
import { UserSessionStore } from '../../../stores/user.store';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-edit-password',
  templateUrl: './edit-password.component.html',
  styleUrl: './edit-password.component.css'
})
export class EditPasswordComponent implements OnInit{

  passwordForm!: FormGroup;
  constructor(private fb: FormBuilder, private titleService: Title) {}
  private authSvc = inject(AuthService)
  private utilsSvc = inject(CommonUtilsService)
  private userSessionStore = inject(UserSessionStore)
  showSuccessNotification: boolean = false
  showGenericErrorNotification: boolean = false
  showWrongPwNotification: boolean = false
  currentPasswordVisible = false;
  newPasswordVisible = false; 
  confirmPasswordVisible = false; 
  
  ngOnInit(): void {
    this.titleService.setTitle('Study Trek | Change Password');
    this.passwordForm = this.fb.group({
      currentPassword: this.fb.control<string>('', Validators.required),
      newPassword: this.fb.control<string>('', [Validators.required, this.utilsSvc.passwordValidator()]),
      confirmPassword: this.fb.control<string>('', [Validators.required])
    }, { validators: this.checkPasswordsEqual });
  }

  onSubmit(){
    if (this.passwordForm.valid){
      this.authSvc.changePassword(this.passwordForm.value.currentPassword, this.passwordForm.value.newPassword)
      .subscribe({
        next: (newHashedPw) => {
          this.userSessionStore.updatePasswordHash(newHashedPw);
          this.showSuccessfulEditNotification();
          window.location.reload();
        },
        error: (error) => {
          console.error(error);
          let errorMessage = 'An unexpected error occurred'; 
          if (error.error.notFound) {
            errorMessage = 'User not found'; 
            this.showErrorNotification();
          } else if (error.error.error) {
            errorMessage = 'Current passwords do not match';
            this.showWrongPasswordNotification()
          }
        }
      });
    }
  }
  showSuccessfulEditNotification() {
    this.showSuccessNotification = true;
    setTimeout(() => {
      this.showSuccessNotification = false;
    }, 4000);
  }

  showErrorNotification(){
    this.showGenericErrorNotification = true;
    setTimeout(() => {
      this.showGenericErrorNotification = false;
    }, 4000);
  }

  showWrongPasswordNotification(){
    this.showWrongPwNotification = true;
    setTimeout(() => {
      this.showWrongPwNotification = false;
    }, 4000);
  }

  //toggle show/hide pw
  toggleCurrentPasswordVisibility() {
    this.currentPasswordVisible = !this.currentPasswordVisible;
  }
  toggleNewPasswordVisibility() {
    this.newPasswordVisible = !this.newPasswordVisible;
  }
  toggleConfirmPasswordVisibility() {
    this.confirmPasswordVisible = !this.confirmPasswordVisible;
  }
 
  checkPasswordsEqual(group: FormGroup) { 
    let pass = group.get('newPassword')?.value;
    let confirmPass = group.get('confirmPassword')?.value;
    return pass === confirmPass ? null : { passwordMismatch: true } 
  }

}



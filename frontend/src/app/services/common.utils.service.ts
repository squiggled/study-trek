import { Injectable } from '@angular/core';
import { Platform } from '../models';
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

@Injectable()
export class CommonUtilsService {
  convertPlatformToStringFormatter(platform: Platform) {
    return (
      platform.toString().charAt(0).toUpperCase() +
      platform.toString().slice(1).toLowerCase()
    );
  }

  displayPlatformLogo(platform: Platform): string {
    // console.log("platform ", platform);
    switch (platform) {
      case Platform.UDEMY:
        return 'logo-udemy.png';
      case Platform.EDX:
        return 'logo-edX.png';
      case Platform.COURSERA:
        return 'logo-coursera.png';
      default:
        return '';
    }
  }

  passwordValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) {
        return null;
      }
      const hasNumber = /[0-9]/.test(value);
      const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(value);
      const valid = hasNumber && hasSpecialChar;
      return !valid ? { invalidPassword: true } : null;
    };
  }
}

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

  toPlatformEnum(platformStr: string): Platform {
    return Platform[platformStr as keyof typeof Platform] ?? Platform.OTHER;
  }

  capitaliseFirstLetterFormatter(s: string): string {
    return s.charAt(0).toUpperCase() + s.slice(1);
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

  isTokenExpired(): boolean {
    const expiration = localStorage.getItem('expiration');
    const expiresAt = expiration ? parseInt(expiration, 10) : 0;
    return Date.now() >= expiresAt * 1000;
  }

  formatResponse(response: string): string {
    const formattedResponse = response.replace(/(?<=^|\s)\d+\./g, (match: any) => `\n${match}`);
    const courseRegex = /"([^"]+)"/g; 
    const linkFormattedResponse = formattedResponse.replace(courseRegex, (match, courseName) => {
      const encodedCourseName = encodeURIComponent(courseName);
      const searchUrl = `/#/courses/search?query=${encodedCourseName}&page=1`;
      return `<a href="${searchUrl}" target="_blank" class="text-white underline">${courseName}</a>`;
    });
    return linkFormattedResponse;
  }
  
}

import { Injectable } from "@angular/core";
import { Platform } from "../models";

@Injectable()
export class CommonUtilsService{
    
    convertPlatformToStringFormatter(platform: Platform){
        return platform.toString().charAt(0).toUpperCase() + platform.toString().slice(1).toLowerCase()
    }

    displayPlatformLogo(platform: Platform): string {
        // console.log("platform ", platform);
        switch(platform) {
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
}
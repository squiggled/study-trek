import { Component, ElementRef, OnInit, ViewChild, inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ImageCroppedEvent } from 'ngx-image-cropper';
import { UserService } from '../../../services/user.service';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { UserSessionStore } from '../../../stores/user.store';
import { Title } from '@angular/platform-browser';


@Component({
  selector: 'app-photo',
  templateUrl: './photo.component.html',
  styleUrl: './photo.component.css'
})
export class PhotoComponent implements OnInit{
  
  profilePicUrl!:string;
  imageChangedEvent: any = '';
  croppedImage: any = '';
  fileName: string = 'No file selected';
  croppedBlob: Blob | null = null;
  showSuccessNotification: boolean = false

  private userSvc = inject(UserService);
  private userSessionStore = inject(UserSessionStore)
  constructor(private titleService: Title) { }

  ngOnInit(): void {
    this.titleService.setTitle('Study Trek | Change Photo');
    this.userSessionStore.profilePic$.subscribe(url => {
      this.profilePicUrl = url; 
    });
  }
  
  fileChangeEvent(event: any): void {
    console.log('File change event triggered');
    this.imageChangedEvent = event;
    this.fileName = event.target.files[0].name;
  }
  
  imageCropped(event: ImageCroppedEvent) {
    console.log('Image cropped event:', event);
    if (event.blob) {
      this.croppedImage = URL.createObjectURL(event.blob);
      console.log('Cropped Image URL:', this.croppedImage);
      this.croppedBlob = event.blob;
    }
  }

  uploadImage() {
    console.log('Image data to upload:', this.croppedImage);
  }

  saveImage() {
    console.log('Saving cropped image...');
    this.showNotification();
    const userId = localStorage.getItem('userId');
    if (!userId) {
      console.error('UserId not found in local storage.');
      return;
    }
    if (this.croppedImage) {
      if (this.croppedBlob) {
        this.userSvc.uploadPicture(userId, this.croppedBlob).subscribe({
          next: () => {
            console.log('Profile picture updated successfully.');
            window.location.reload();
          },
          error: (error) => console.error('Error updating profile picture:', error),
        });
      } else {
        console.error('Failed to convert cropped image to Blob');
      }
    }
  }

  triggerFileInput() {
    document.getElementById('fileInput')?.click();
  }

  showNotification() {
    this.showSuccessNotification = true;
    //hide the notification after 3 seconds
    setTimeout(() => {
      this.showSuccessNotification = false;
    }, 3000);
  }

}

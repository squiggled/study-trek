import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-edit-password',
  templateUrl: './edit-password.component.html',
  styleUrl: './edit-password.component.css'
})
export class EditPasswordComponent implements OnInit{

  passwordForm!: FormGroup;
  constructor(private fb: FormBuilder) {}

  private authSvc = inject(AuthService)
  
  ngOnInit(): void {
    this.passwordForm = this.fb.group({
      firstName: [''],
      lastName: [''],
      interests: ['']
    });
  }

}

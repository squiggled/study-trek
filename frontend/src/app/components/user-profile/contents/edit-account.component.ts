import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../../services/user.service';
import { UserSessionStore } from '../../../stores/user.store';

@Component({
  selector: 'app-edit-account',
  templateUrl: './edit-account.component.html',
  styleUrl: './edit-account.component.css'
})
export class EditAccountComponent {
  showSuccessNotification: boolean = false
  profileForm!: FormGroup;
  private userSvc = inject(UserService);
  private userSessionStore = inject(UserSessionStore)

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.profileForm = this.fb.group({
      firstName: this.fb.control<string>('', Validators.required),
      lastName: this.fb.control<string>('', Validators.required),
      interests: ['']
    });

    // initial profile data
    this.userSessionStore.select(state => state.accountDetails).subscribe(details => {
      this.profileForm.patchValue({
        firstName: details.firstName,
        lastName: details.lastName,
        interests: details.interests.join(', ')
      });
    });
  }

  onSubmit(): void {
    if (this.profileForm.valid) {
      const formValue = this.profileForm.value;
      const updatedDetails = {
        ...formValue,
        interests: formValue.interests.split(',').map((interest: string) => interest.trim()) // string to array
      };
      console.log("updated details ", updatedDetails);
      this.userSvc.updateUserProfile(updatedDetails).subscribe(() => {
        this.showSuccessfulEditNotification();
        // Optionally refresh or update the store with new details
      });
    }
  }

  showSuccessfulEditNotification() {
    this.showSuccessNotification = true;
    setTimeout(() => {
      this.showSuccessNotification = false;
    }, 3000);
  }

}

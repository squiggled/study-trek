import { Component, Inject, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CalendarService } from '../../services/calendar.service';

@Component({
  selector: 'app-create-event',
  templateUrl: './create-event.component.html',
  styleUrl: './create-event.component.css'
})
export class CreateEventComponent implements OnInit{

  calendarForm!:FormGroup;
  private calendarSvc = inject(CalendarService);

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<CreateEventComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ){}
  
  ngOnInit(): void {
    const userId = localStorage.getItem('userId');
    console.log('Dialog data:', this.data);
     this.calendarForm = this.fb.group({
      title: this.fb.control<string>('', [Validators.required]),
      text: this.fb.control<string>('', [Validators.required]),
      date: [{value: this.data.date, disabled: true}, Validators.required],
      hour: [{value: this.data.hour, disabled: true}, Validators.required]
    });
  }

  submitForm(): void {
    if (this.calendarForm.valid) {
      const formRawValue = this.calendarForm.getRawValue(); 
      this.dialogRef.close(formRawValue);
    }
  }

}

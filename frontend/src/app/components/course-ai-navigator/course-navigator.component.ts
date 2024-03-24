import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OpenAIService } from '../../services/openai.service';
import { finalize, pipe } from 'rxjs';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-course-navigator',
  templateUrl: './course-navigator.component.html',
  styleUrl: './course-navigator.component.css'
})
export class CourseNavigatorComponent implements OnInit{

  openAIForm!:FormGroup;
  constructor (private fb:FormBuilder){}
  private openAISvc = inject(OpenAIService);
  private sanitiser = inject(DomSanitizer);
  response$ = this.openAISvc.response$;
  isLoading:boolean = false;

  ngOnInit(): void {
    this.openAIForm = this.fb.group({
      query: this.fb.control<string>('', [Validators.required]),
      number: this.fb.control<number>(1, [Validators.required, Validators.min(1), Validators.max(10)])
    })
  }

  processSubmit(): void {
    this.isLoading = true; 
    if (this.openAIForm.valid) {
      this.openAISvc.processSubmit(this.openAIForm.value.query, this.openAIForm.value.number)
      .pipe(
        finalize(() => this.isLoading = false) 
      )
      .subscribe();
    }
  }

  safeHTML(htmlString: string) {
    return this.sanitiser.bypassSecurityTrustHtml(htmlString);
  }

}

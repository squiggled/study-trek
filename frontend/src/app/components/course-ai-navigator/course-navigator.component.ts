import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OpenAIService } from '../../services/openai.service';
import { finalize, map, pipe } from 'rxjs';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { CommonUtilsService } from '../../services/common.utils.service';

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
  private utilsSvc = inject(CommonUtilsService)
  response$ = this.openAISvc.response$;
  isLoading:boolean = false;
  messages: { text: string; html?: SafeHtml; user:boolean }[] = [
    { text: 'Hello, can I help you find a course?', user: false }
  ];
  testHtml: SafeHtml = this.sanitiser.bypassSecurityTrustHtml('<a href="https://example.com">Test Link</a>');


  ngOnInit(): void {
    this.openAIForm = this.fb.group({
      query: this.fb.control<string>('', [Validators.required]),
      number: this.fb.control<number>(1, [Validators.required, Validators.min(1), Validators.max(10)])
    })
  }

  // processSubmit(): void {
  //   this.isLoading = true; 
  //   if (this.openAIForm.valid) {
  //     const userMessage = `I am looking for ${this.openAIForm.value.number} course(s) on ${this.openAIForm.value.query}.`;
  //     this.messages.push({ text: userMessage, user:true});
  //     this.openAISvc.processSubmit(this.openAIForm.value.query, this.openAIForm.value.number)
  //     .pipe(
  //       finalize(() => this.isLoading = false) 
  //     )
  //     .subscribe((response) => {
  //       console.log("Raw backend response:", response);
  //       const safeResponse = this.safeHTML(response); 
  //       console.log("Sanitized response:", safeResponse);
  //       this.messages.push({ text: response, html: safeResponse, user: false }); // Assuming `html` is of type `SafeHtml`
  //     });
  //   }
  // }

  processSubmit(): void {
    this.isLoading = true; 
    if (this.openAIForm.valid) {
      // User message
      const userMessage = `I am looking for ${this.openAIForm.value.number} course(s) on ${this.openAIForm.value.query}.`;
      this.messages.push({ text: userMessage, user: true });
  
      // Call to service
      this.openAISvc.processSubmit(this.openAIForm.value.query, this.openAIForm.value.number).pipe(
        map(response => this.utilsSvc.formatResponse(response)),
        finalize(() => this.isLoading = false)
      ).subscribe(response => {
        // System message
        const safeHtml = this.safeHTML(response); 
        this.messages.push({ text: '', html: safeHtml, user: false }); // Include an empty 'text' field
      });
    }
  }
  

  safeHTML(htmlString: string): SafeHtml {
    return this.sanitiser.bypassSecurityTrustHtml(htmlString);
  }

}

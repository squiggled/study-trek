import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ForumStore } from '../../stores/forum.store';
import { ForumService } from '../../services/forum.service';
import { tap } from 'rxjs/operators';
import { of } from 'rxjs';
import { Title } from '@angular/platform-browser';


@Component({
  selector: 'app-forum',
  templateUrl: './forum.component.html',
  styleUrls: ['./forum.component.css']
})
export class ForumComponent implements OnInit{
  createThreadForm!: FormGroup;
  private forumSvc = inject(ForumService);
  private forumStore = inject(ForumStore)
  private router = inject(Router)
  threads$ = this.forumStore.threads$;

  constructor(private fb: FormBuilder, private titleService: Title) {}
    
  ngOnInit(): void {
    this.titleService.setTitle('Study Trek | Forum');
    this.createThreadForm = this.fb.group({
      title: ['', Validators.required],
      content: ['', Validators.required],
    });
    this.forumStore.loadThreads(of());
  }

  createThread() {
    if (this.createThreadForm.valid) {
      const thread = { ...this.createThreadForm.value, messages: [], createdDate: new Date() };
      this.forumSvc.createThread(thread).pipe(
        tap(createdThread => this.forumStore.addThread(createdThread)) // Update the store with the new thread
      ).subscribe(() => {
        console.log('Thread creation successful.');
        this.router.navigate(['/forum']);
      }, (error: any) => {
        console.error('Error creating thread:', error);
      });
    }
  }

  goToCreateThread() {
    this.router.navigate(['/forum/create-topic']);
  }
}

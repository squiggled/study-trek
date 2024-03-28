import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ForumService } from '../../../services/forum.service';
import { Subscription, switchMap } from 'rxjs';
import { UserSessionStore } from '../../../stores/user.store';
import { ForumThread } from '../../../models';
import { combineLatest, map } from 'rxjs';
import { ForumStore } from '../../../stores/forum.store';


@Component({
  selector: 'app-create-thread',
  templateUrl: './create-thread.component.html',
  styleUrl: './create-thread.component.css',
})
export class CreateThreadComponent implements OnInit {

  private router = inject(Router);
  private forumSvc = inject(ForumService)
  private userSessionStore = inject(UserSessionStore);
  private forumStore = inject(ForumStore);
  private subscription: Subscription = new Subscription();
  createThreadForm!: FormGroup;
  constructor(private fb: FormBuilder) {}
  isSubmitting = false; 

  ngOnInit(): void {
    this.createThreadForm = this.fb.group({
      title: this.fb.control<string>('', [Validators.required]),
      content: this.fb.control<string>('', [Validators.required]),
    });
  }

  createThread() {
    if (this.createThreadForm.valid) {
      this.isSubmitting = true;
      const userId = localStorage.getItem('userId');
      if (!userId) {
        console.error('UserId not found in local storage.');
        return;
      }
      
      this.subscription.add(
        combineLatest([
          this.userSessionStore.firstName$,
          this.userSessionStore.profilePic$
        ]).pipe(
          map(([firstName, userProfilePic]) => ({
            userId: userId,
            firstName: firstName,
            userProfilePic: userProfilePic,
            createdDate: new Date(),
            title: this.createThreadForm.value.title,
            content: this.createThreadForm.value.content,
            messages: []
          })),
          switchMap(thread => this.forumSvc.createThread(thread))
        ).subscribe({
          next: (createdThread) => {
            console.log('Thread creation successful:', createdThread);
            this.forumStore.addThread(createdThread);
            this.router.navigate(['/forum']);
          },
          error: (error) => {
            console.error('Error creating thread:', error);
          }
        })
      );
    }
  }

  ngOnDestroy() {
    this.subscription.unsubscribe(); 
  }

  goBack() {
    if (this.createThreadForm.dirty) {
      const confirmation = confirm(
        'You have entered text on this page. Would you still want to leave?'
      );
      if (!confirmation) {
        return;
      }
    }
    this.router.navigate(['/forum']);
  }
}

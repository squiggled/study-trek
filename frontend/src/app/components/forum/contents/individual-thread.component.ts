import { Component, OnInit, inject } from '@angular/core';
import { Observable , combineLatest} from 'rxjs';
import { switchMap, take, tap } from 'rxjs/operators';
import { ForumThread, ThreadMessage } from '../../../models';
import { ForumStore } from '../../../stores/forum.store';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ForumService } from '../../../services/forum.service';
import { UserSessionStore } from '../../../stores/user.store';

@Component({
  selector: 'app-individual-thread',
  templateUrl: './individual-thread.component.html',
  styleUrl: './individual-thread.component.css'
})
export class IndividualThreadComponent implements OnInit{

  thread$!: Observable<ForumThread | undefined>;
  private forumStore = inject(ForumStore);
  private userSessionStore = inject(UserSessionStore);
  private forumSvc = inject(ForumService);
  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);
  isReplyFormVisible = false;
  replyThreadForm!: FormGroup;
  threadId!:string;

  constructor(private fb: FormBuilder) {}
 

  ngOnInit(): void {
    const idParam = this.activatedRoute.snapshot.paramMap.get('threadId');
    if (idParam){
     this.threadId=idParam;
    }
    this.thread$ = this.forumStore.getThreadById(this.threadId);
    this.replyThreadForm = this.fb.group({
      content: this.fb.control<string>('', [Validators.required]),
    });
  }

  toggleReplyForm() {
    this.isReplyFormVisible = !this.isReplyFormVisible;
  }

  submitReply() {
    if (this.replyThreadForm.valid && this.threadId) {
      const userId = localStorage.getItem('userId');
      if (!userId) return; 
      
      combineLatest([
        this.userSessionStore.firstName$,
        this.userSessionStore.profilePic$
      ]).pipe(
        take(1), 
        switchMap(([firstName, userProfilePic]) => {
          const message: ThreadMessage = {
            userId,
            firstName,
            userProfilePic,
            postedDate: new Date(),
            content: this.replyThreadForm.get('content')?.value,
          };
          return this.forumSvc.replyToThread(this.threadId, message);
        }),
        tap(updatedThread => this.forumStore.updateThread(updatedThread))
      ).subscribe({
        next: () => {
          console.log('Reply added successfully.');
          this.isReplyFormVisible = false;
          this.replyThreadForm.reset();
        },
        error: (error) => console.error('Failed to add reply', error)
      });
    }
  }
  
  goBack() {
    if (this.replyThreadForm.dirty) {
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

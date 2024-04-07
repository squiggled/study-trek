import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../../services/user.service';
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { Observable, Subscription } from 'rxjs';
import { AccountDetails, FriendInfo, FriendRequest } from '../../../models';
import { UserSessionStore } from '../../../stores/user.store';
import { FriendListStore } from '../../../stores/friends.store';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrl: './friends.component.css',
  animations: [
    trigger('popUpAnimation', [
      state(
        'closed',
        style({
          transform: 'translateY(100%)',
        })
      ),
      state(
        'open',
        style({
          transform: 'translateY(0)',
        })
      ),
      transition('closed <=> open', animate('300ms ease-out')),
    ]),
  ],
})
export class FriendsComponent implements OnInit {
  constructor(private fb: FormBuilder, private titleService: Title) {}

  private userSessionStore = inject(UserSessionStore);
  private userSvc = inject(UserService);
  friendStore = inject(FriendListStore);
  friendSearchForm!: FormGroup;
  showSearchPopUp = false;
  foundFriend$ = this.userSvc.foundFriend$;
  accountDetails$!: Observable<AccountDetails>;
  userId!: string;
  private subscription: Subscription = new Subscription();

  ngOnInit(): void {
    this.titleService.setTitle('Study Trek | My Friends');
    this.accountDetails$ = this.userSessionStore.select(
      (state) => state.accountDetails
    );
    this.subscription.add(
      this.userSessionStore.userId$.subscribe((id) => {
        this.userId = id;
      })
    );
    this.friendSearchForm = this.fb.group({
      friendEmail: this.fb.control<string>('', [Validators.required]),
    });
    const userId = localStorage.getItem('userId');
    if (userId) {
        this.friendStore.loadFriends(userId);
    } else {
        console.error('No userId found in localStorage');
  }
  }

  // ngOnDestroy(): void {
  //   this.subscription.unsubscribe();
  // }

  toggleSearchPopUp(): void {
    this.showSearchPopUp = !this.showSearchPopUp;
    this.friendSearchForm.reset(['']);
  }

  search(event: Event): void {
    event.preventDefault();
    if (this.friendSearchForm.valid) {
      const email = this.friendSearchForm.get('friendEmail')!.value;
      this.userSvc.searchForFriendByEmail(this.userId, email);
    }
  }

  addFriend(friendId: string) {
    const friendReq: FriendRequest = {
      requestId: '',
      senderId: this.userId, // id of the current user sending the friend request
      receiverId: friendId,
      status: 'PENDING', 
    };
    this.userSvc.addFriend(friendReq).subscribe({
      next: (response: any) => {
        console.log('Friend request made successfully', response);
        this.userSvc.updateFoundFriendStatus(friendId, 'PENDING');
      },
      error: (error: any) => {
        console.error('Error making friend request', error);
      },
    });
  }
}

import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../../services/user.service';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { Observable, Subscription } from 'rxjs';
import { AccountDetails, FriendInfo } from '../../../models';
import { UserSessionStore } from '../../../stores/user.store';

@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrl: './friends.component.css',
  animations: [
    trigger('popUpAnimation', [
      state('closed', style({
        transform: 'translateY(100%)',
      })),
      state('open', style({
        transform: 'translateY(0)',
      })),
      transition('closed <=> open', animate('300ms ease-out')),
    ]),
  ],
})
export class FriendsComponent implements OnInit, OnDestroy{
  
  constructor (private fb: FormBuilder){}
  
  private userSessionStore = inject(UserSessionStore);
  private userSvc = inject(UserService);
  friendSearchForm!: FormGroup;
  showSearchPopUp = false;
  foundFriend$ = this.userSvc.foundFriend$;
  accountDetails$!: Observable<AccountDetails>;
  userId!: string; 
  private subscription: Subscription = new Subscription();


  ngOnInit(): void {
    this.accountDetails$ = this.userSessionStore.select(state => state.accountDetails);
    this.subscription.add(
      this.userSessionStore.userId$.subscribe((id) => {
        this.userId = id;
      })
    );
    this.friendSearchForm=this.fb.group({
      friendEmail: this.fb.control<string>('', [Validators.required])
    })
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  toggleSearchPopUp(): void {
    this.showSearchPopUp = !this.showSearchPopUp;
  }

  search(event: Event): void {
    event.preventDefault();
    if (this.friendSearchForm.valid) {
      const email = this.friendSearchForm.get('friendEmail')!.value;
      this.userSvc.searchForFriendByEmail(this.userId, email); // Replace 'userId' with actual user ID
    }
  }

  addFriend(friendUserId: string){

  }
}

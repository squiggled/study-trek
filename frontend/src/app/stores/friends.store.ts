import { Injectable, inject } from '@angular/core';
import { ComponentStore, tapResponse } from '@ngrx/component-store';
import { FriendInfo, FriendListSlice } from '../models';
import { Observable, filter, switchMap, of } from 'rxjs';
import { FriendService } from '../services/friend.service';

const INIT_STATE: FriendListSlice = {
    friendList: []
};
@Injectable()
export class FriendListStore extends ComponentStore<FriendListSlice> {
    constructor() {
        super(INIT_STATE)
        this.loadFriendsEffect();
    }
    private friendSvc = inject(FriendService);

    readonly setFriendList = this.updater((state, friendList: FriendInfo[]) => ({
        ...state,
        friendList
    }));

    private loadFriendsEffect() {
      this.effect(() =>
        of(localStorage.getItem('userId')).pipe(
          switchMap((userId) => {
            if (!userId) {
              console.error('No user ID found in localStorage');
              return of([]); 
            }
            return this.friendSvc.getFriends(userId);
          }),
          tapResponse(
            (friends) => this.setFriendList(friends),
            (error) => console.error('Error loading friends:', error)
          )
        )
      );
    }
   

    loadFriends(userId: string) {
      this.friendSvc.getFriends(userId).subscribe(
          friends => this.setFriendList(friends),
          error => console.error('Error loading friends:', error)
      );
  }
    //selector to get the friend list from the state
    readonly friendList$: Observable<FriendInfo[]> = this.select(state => state.friendList);
}



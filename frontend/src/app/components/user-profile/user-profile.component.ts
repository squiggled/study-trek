import { Component, OnInit, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { AccountDetails } from '../../models';
import { UserSessionStore } from '../../stores/user.store';
import { Title } from '@angular/platform-browser';


@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})
export class UserProfileComponent implements OnInit{
  accountDetails$!: Observable<AccountDetails>;
  private userSessionStore = inject(UserSessionStore);
  constructor(private titleService: Title) { }

  ngOnInit(): void {
    this.titleService.setTitle('Study Trek | My Profile');
    this.accountDetails$ = this.userSessionStore.select(state => state.accountDetails);
  }
}

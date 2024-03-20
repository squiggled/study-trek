import { Component, OnInit, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { AccountDetails } from '../../models';
import { UserSessionStore } from '../../stores/user.store';

@Component({
  selector: 'app-profile-nav',
  templateUrl: './profile-nav.component.html',
  styleUrl: './profile-nav.component.css'
})
export class ProfileNavComponent implements OnInit {
  
  private userSessionStore = inject(UserSessionStore);
  accountDetails$!: Observable<AccountDetails>;

  ngOnInit(): void {
    this.accountDetails$ = this.userSessionStore.select(state => state.accountDetails);
  }
}

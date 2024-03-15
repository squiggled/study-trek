import { Component, OnInit, inject } from '@angular/core';
import { AccountDetails } from '../../models';
import { Observable } from 'rxjs';
import { UserSessionStore } from '../../stores/user.store';

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrl: './courses.component.css'
})
export class MyCoursesComponent implements OnInit{
  accountDetails$!: Observable<AccountDetails>;
  private userSessionStore = inject(UserSessionStore);


  ngOnInit(): void {
    this.accountDetails$ = this.userSessionStore.select(state => state.accountDetails);
  }
}

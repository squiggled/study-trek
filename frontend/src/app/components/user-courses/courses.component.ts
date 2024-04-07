import { Component, OnInit, inject } from '@angular/core';
import { AccountDetails, Platform } from '../../models';
import { Observable } from 'rxjs';
import { UserSessionStore } from '../../stores/user.store';
import { SearchService } from '../../services/search.service';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrl: './courses.component.css'
})
export class MyCoursesComponent implements OnInit{
  accountDetails$!: Observable<AccountDetails>;
  private userSessionStore = inject(UserSessionStore);
  private searchSvc = inject(SearchService)
  private router = inject(Router)
  constructor(private titleService: Title) { }

  ngOnInit(): void {
    this.titleService.setTitle('Study Trek | My Courses');
    this.accountDetails$ = this.userSessionStore.select(state => state.accountDetails);
  }
  
  getCourseById(courseId: string, platform: Platform){
    this.searchSvc.getCourseById(courseId, platform);
    this.router.navigate([
      '/course',
      platform.toString().toLowerCase(),
      courseId,
    ]);
  }
}

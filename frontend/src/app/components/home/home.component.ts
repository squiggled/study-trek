import { Component, OnInit, inject } from '@angular/core';
import { Observable, filter } from 'rxjs';
import { AccountDetails, CourseDetails, CourseSearch, Platform } from '../../models';
import { UserSessionStore } from '../../stores/user.store';
import { SearchComponent } from '../navbar/search.component';
import { SearchService } from '../../services/search.service';
import { NavigationEnd, Router } from '@angular/router';
import { HomePageCourseListingStore } from '../../stores/course-homepage.store';
import { NotificationService } from '../../services/notification.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit{
  accountDetails$!: Observable<AccountDetails>;
  private userSessionStore = inject(UserSessionStore);
  private searchSvc = inject(SearchService);
  private router = inject(Router);
  private notificationSvc = inject(NotificationService);
  private homepageCourseListingStore = inject(HomePageCourseListingStore);
  constructor(private titleService: Title) { }
  
  showScheduleCard = true;
  categories = ['Business', 'Coding', 'Marketing', 'Design', 'Video'];
  selectedCategory: string = 'Business';
  homepageCourses$!: Observable<CourseSearch[]>;
  userId!:string;
  isLoggedIn$!: Observable<boolean>;
  isLoading: boolean = true;

  ngOnInit(): void {
    this.titleService.setTitle('Study Trek | Learn Anything Online');

    this.accountDetails$ = this.userSessionStore.select(state => state.accountDetails);
    this.loadHomepageCourses();
    this.homepageCourses$ = this.homepageCourseListingStore.select(state => state.homePageCourseListing);
    const userId = localStorage.getItem('userId');
    if (userId) {
      console.log("found user id " + userId);
      this.userId = userId;
    }
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.notificationSvc.fetchAndSetNotifications();
    });
    this.isLoggedIn$ = this.userSessionStore.isLoggedIn$;
  }

  dismissScheduleCard() {
    this.showScheduleCard = false; //hide schedule card when button is clicked
  }

 
  loadHomepageCourses(): void {
    this.searchSvc.loadHomepageCourses().subscribe({
      next: (courses) => {
        //update store with the loaded courses
        this.homepageCourseListingStore.setHomePageCourseListing(courses);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading homepage courses:', error)
        this.isLoading = false;
      }
    });
  }


  getCourseById(courseId: string, platform: Platform){
    this.searchSvc.getCourseById(courseId, platform);
    this.router.navigate([
      '/course',
      platform.toString().toLowerCase(),
      courseId,
    ]);
  }

  getCourseDetails(courseId: string, platform: Platform) {
    console.log('course id in result comp ', courseId);

    this.searchSvc.getCourseById(courseId, platform);
    this.router.navigate([
      '/course',
      platform.toString().toLowerCase(),
      courseId,
    ]);
  }

  selectCategory(category: string): void {
    this.selectedCategory = category;
    this.homepageCourseListingStore.filterHomePageCourseListing(category.toLowerCase());
  }

  resetFilters(): void {
    this.homepageCourseListingStore.resetFilter();
  }
  testClick() {
    console.log('Click event triggered');
  }

}

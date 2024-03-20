import { Component, OnInit, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { AccountDetails, CourseDetails, CourseSearch, Platform } from '../../models';
import { UserSessionStore } from '../../stores/user.store';
import { SearchComponent } from '../navbar/search.component';
import { SearchService } from '../../services/search.service';
import { Router } from '@angular/router';
import { HomePageCourseListingStore } from '../../stores/course-homepage.store';

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
  private homepageCourseListingStore = inject(HomePageCourseListingStore);
  
  showScheduleCard = true;
  categories = ['Business', 'Coding', 'Marketing', 'Design', 'Video'];
  selectedCategory: string = 'Business';
  homepageCourses$!: Observable<CourseSearch[]>;


  ngOnInit(): void {
    this.accountDetails$ = this.userSessionStore.select(state => state.accountDetails);
    this.loadHomepageCourses();
    this.homepageCourses$ = this.homepageCourseListingStore.select(state => state.homePageCourseListing);
    // this.homepageCourses$ = this.homepageCourseListingStore.select(state => state.homePageCourseListing);
  }

  dismissScheduleCard() {
    this.showScheduleCard = false; //hide schedule card when button is clicked
  }


  loadHomepageCourses(): void {
    this.searchSvc.loadHomepageCourses().subscribe({
      next: (courses) => {
        //update store with the loaded courses
        this.homepageCourseListingStore.setHomePageCourseListing(courses);
      },
      error: (error) => console.error('Error loading homepage courses:', error)
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

}

import { Component, OnInit, inject } from '@angular/core';
import { Observable, take, withLatestFrom } from 'rxjs';
import { CourseDetails, Platform } from '../../models';
import { ActivatedRoute } from '@angular/router';
import { SearchService } from '../../services/search.service';
import { CourseDetailsStore } from '../../stores/course-details.store';

@Component({
  selector: 'app-course-details',
  templateUrl: './course-details.component.html',
  styleUrl: './course-details.component.css'
})
export class CourseDetailsComponent implements OnInit{
  
  courseDetails$!: Observable<CourseDetails>;
  private activatedRoute = inject(ActivatedRoute);
  private searchSvc =  inject(SearchService);
  private courseDetailsStore =  inject(CourseDetailsStore);
  
  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const platform = params['platform'];
      const courseId = +params['courseId']; // Ensure courseId is a number
  
      //check if the requested course is already being viewed
      this.courseDetailsStore.getCurrentCourseId.pipe(
        take(1), //take the current value and unsubscribe
        withLatestFrom(this.courseDetailsStore.getCurrentPlatform.pipe(take(1)))
      ).subscribe(([currentCourseId, currentPlatform]) => {
        if (currentCourseId !== courseId || currentPlatform !== platform) {
          //if requested course differs from the current one fetch new details
          this.searchSvc.getCourseById(courseId, platform);
          //update the store with the new current course ID and platform
          this.courseDetailsStore.updateCurrentCourse({ courseId, platform });
        }
        //Subscribe to the course details observable
        this.courseDetails$ = this.courseDetailsStore.getCourseDetails;
      });
    });
  }

  getPlatformLogo(platform: Platform): string {
    console.log("platform ", platform);
    
    switch(platform) {
      case Platform.UDEMY:
        return 'logo-udemy.png'; 
      case Platform.EDX:
        return 'logo-edX.png'; 
        case Platform.COURSERA:
        return 'logo-coursera.png';
      default:
        return ''; 
    }
  }
}

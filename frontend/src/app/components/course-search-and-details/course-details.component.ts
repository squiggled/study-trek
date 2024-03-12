import { Component, OnInit, inject } from '@angular/core';
import { Observable, take, withLatestFrom } from 'rxjs';
import { CourseDetails, Platform } from '../../models';
import { ActivatedRoute } from '@angular/router';
import { SearchService } from '../../services/search.service';
import { CourseDetailsStore } from '../../stores/course-details.store';
import { CommonUtilsService } from '../../services/common.utils.service';
import { ViewportScroller } from '@angular/common';

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
  private utilsSvc = inject(CommonUtilsService)
  
  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const platform = params['platform'];
      const courseId = params['courseId'];
  
      //check if the requested course is already being viewed
      this.courseDetailsStore.getCurrentCourseId.pipe(
        take(1), //take the current value and unsubscribe
        withLatestFrom(this.courseDetailsStore.getCurrentPlatform.pipe(take(1)))
      ).subscribe(([currentCourseId, currentPlatform]) => {
        if (currentCourseId !== courseId || currentPlatform !== platform) {
          //if requested course differs from the current one, fetch new details
          this.searchSvc.getCourseById(courseId, platform);
          //update store with the new current course id and platform
          this.courseDetailsStore.updateCurrentCourse({ courseId, platform });
        }
        //sub to the course details obs
        this.courseDetails$ = this.courseDetailsStore.getCourseDetails;
      });
    });
  }

  getPlatformLogo(platform: Platform): string {
    return this.utilsSvc.displayPlatformLogo(platform);
  }

  displayPlatformName(platform: Platform){
    return this.utilsSvc.convertPlatformToStringFormatter(platform);
  }

  visitExternalUrl(url: string): void {
    if(url) {
      window.location.href = url;
    }
  }
}

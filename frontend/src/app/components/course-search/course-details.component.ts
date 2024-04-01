import { Component, HostListener, OnInit, inject } from '@angular/core';
import { Observable, take, withLatestFrom } from 'rxjs';
import { CourseDetails, CourseNote, Platform } from '../../models';
import { ActivatedRoute } from '@angular/router';
import { SearchService } from '../../services/search.service';
import { CourseDetailsStore } from '../../stores/course-details.store';
import { CommonUtilsService } from '../../services/common.utils.service';
import { UserSessionStore } from '../../stores/user.store';
import { UserService } from '../../services/user.service';
import { CourseService } from '../../services/course.service';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-course-details',
  templateUrl: './course-details.component.html',
  styleUrl: './course-details.component.css'
})
export class CourseDetailsComponent implements OnInit{
  
  userId!: string;
  courseId!:string;
  newNote: string = '';

  showSuccessNotification:boolean = false;
  showAddFailNotification:boolean = false;
  isScrolled = false;

  isUserEnrolled$!: Observable<boolean>;
  courseDetails$!: Observable<CourseDetails>;
  courseNotes$!: Observable<CourseNote[]>;

  private activatedRoute = inject(ActivatedRoute);
  private searchSvc =  inject(SearchService);
  private courseDetailsStore =  inject(CourseDetailsStore);
  private utilsSvc = inject(CommonUtilsService)
  private userSessionStore = inject(UserSessionStore)
  private userSvc = inject(UserService);
  private courseSvc = inject(CourseService);

  courseForm!:FormGroup
  constructor (private fb:FormBuilder){}
  
  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const platform = params['platform'];
      const courseId = params['courseId'];
      this.courseId=courseId;
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
    this.userSessionStore.userId$.subscribe(userId => {
      this.userId = userId;
      console.log("User ID from store:", this.userId);
    });
    this.courseForm = this.fb.group({
      newNote: ['']
      // other form controls
    });
    this.isUserEnrolled$ = this.userSessionStore.isEnrolledInCourse(this.courseId);
    
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

  addCourseToList(courseDetails: CourseDetails) {
    let isCoursePresent = this.userSessionStore.courseExists(courseDetails);
    if (isCoursePresent){
      this.showAddFailNotificationMethod()
      console.log("course already exists"); 
    } else {
      this.userSvc.addRegisteredCourseToUser(this.userId, courseDetails).subscribe({
        next: (response:any) => {
          this.userSessionStore.addCourseToUser(response);
          this.showSuccessNotificationMethod();
        },
        error: (error:any) => {
          console.error('Failed to add the course', error);
        }
      });
    }
  }
  fetchNotes() {
    this.courseNotes$ = this.courseSvc.fetchNotes(this.courseId, this.userId);
  }

  saveNote(): void {
    if (this.courseForm.value.newNote.trim()) {
      // Call service to save note, then clear the form field
      this.courseForm.reset();
    }
  }

  showSuccessNotificationMethod() {
    this.showSuccessNotification = true;
    setTimeout(() => {
      this.showSuccessNotification = false;
    }, 3000);
  }

  showAddFailNotificationMethod(){
    this.showAddFailNotification = true;
    setTimeout(() => {
      this.showAddFailNotification = false;
    }, 3000);
  }

  @HostListener('window:scroll', ['$event'])
  onWindowScroll() {
    const threshold = 100;
    this.isScrolled = window.scrollY > threshold;
    console.log("is scrolled " + this.isScrolled);
    
  }
}

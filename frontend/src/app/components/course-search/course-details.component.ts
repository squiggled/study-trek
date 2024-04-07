import { Component, HostListener, OnInit, inject } from '@angular/core';
import { Observable, EMPTY, of, from, forkJoin } from 'rxjs';
import { map, take, switchMap, catchError, tap } from 'rxjs/operators';
import { CourseDetails, CourseNote, Platform, defaultCourseDetails } from '../../models';
import { ActivatedRoute } from '@angular/router';
import { SearchService } from '../../services/search.service';
import { CourseDetailsStore } from '../../stores/course-details.store';
import { CommonUtilsService } from '../../services/common.utils.service';
import { UserSessionStore } from '../../stores/user.store';
import { UserService } from '../../services/user.service';
import { CourseService } from '../../services/course.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-course-details',
  templateUrl: './course-details.component.html',
  styleUrl: './course-details.component.css'
})
export class CourseDetailsComponent implements OnInit{
  
  userId!: string;
  courseId!:number;
  platformId!:string;
  newNote: string = '';
  selectedNoteId = 0;
  platform!:string;

  showSuccessSaveNoteNotification:boolean = false;
  showSuccessAddCourseNotification:boolean=false
  showAddFailNotification:boolean = false;
  isScrolled = false;
  isLoggedIn: boolean = localStorage.getItem('isLoggedIn') === 'true';

  isUserEnrolled$!: Observable<boolean>;
  courseDetails$!: Observable<CourseDetails>;
  courseNote$!: Observable<CourseNote | undefined>;
  isUserEnrolledLS!:boolean;

  private activatedRoute = inject(ActivatedRoute);
  private searchSvc =  inject(SearchService);
  private courseDetailsStore =  inject(CourseDetailsStore);
  private utilsSvc = inject(CommonUtilsService)
  private userSessionStore = inject(UserSessionStore)
  private userSvc = inject(UserService);
  private courseSvc = inject(CourseService);

  courseForm!:FormGroup
  constructor (private fb:FormBuilder, private titleService: Title){}
  ngOnInit(): void {
    this.titleService.setTitle('Study Trek | Search');

    this.activatedRoute.params.pipe(
      take(1),
      switchMap(params => {
        const platformId = params['courseId'];
        const platform = params['platform'];
        this.platform = platform;
        console.log("this.platformId ", platformId);
        
        this.platformId = platformId;
        this.initialiseForm(this.platformId);
  
        // Check if user is logged in by checking local storage
        const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'; // Or check for accountDetails existence

        
        if (isLoggedIn) {
          // Try to fetch account details from local storage
          this.isLoggedIn=isLoggedIn;
          console.log("islogged in ", isLoggedIn);
          
          const accountDetails = JSON.parse(localStorage.getItem('accountDetails') || '{}');
          
          // Find the course in registeredCourses by platformId
          const course = accountDetails.registeredCourses?.find((c:any) => c.platformId === platformId);
          this.isUserEnrolledLS = this.isUserEnrolled(platformId);
          console.log('isUserEnrolled:', this.isUserEnrolledLS);
  
          if (course) {
            this.courseId = course.courseId;
            console.log("courseId HERE", this.courseId);
            
            // Populate course notes if they exist for this course
            this.courseNote$ = of(course.notes);
            return of(course);
          } else {
            // If course is not found in local storage, fetch from backend
            return this.fetchCourseDetailsFromBackend(platformId, platform);
          }
        } else {
          // User is not logged in, fetch course details from backend
          return this.fetchCourseDetailsFromBackend(platformId, platform);
        }
      })
    ).subscribe(courseDetails => {
      this.courseDetails$ = of(courseDetails);
      this.refreshFormData(this.courseId);

    });
  }
  
  refreshFormData(courseId:number): void {
    this.courseSvc.getLatestCourseNoteByCourseId(courseId).subscribe(note => {
      if (note) {
        this.courseForm.patchValue({
          newNote: note.text
        });
      } else {
        this.courseForm.reset({
          newNote: ''
        });
      }
    }, error => {
      console.error('Failed to fetch the latest course note', error);
      // Handle error state appropriately
    });
  }
  
  // Helper method to encapsulate backend fetching logic.
  private fetchCourseDetailsFromBackend(platformId: string, platform:string) {
    return from(this.searchSvc.getCourseById(platformId, this.utilsSvc.toPlatformEnum(platform.toUpperCase()))).pipe(
      map(courseDetailsFromBackend => courseDetailsFromBackend ?? defaultCourseDetails),
      catchError(error => {
        console.error('Error fetching course details from backend:', error);
        return of(defaultCourseDetails); // Fallback on error.
      })
    );
  }
  
  initialiseForm(courseId: string): void {
    this.userSessionStore.getCourseNoteForCourse(this.selectedNoteId).subscribe(note => {
      if (note) {
        this.selectedNoteId = note.noteId; 
        this.courseForm.patchValue({
          newNote: note.text
        });
      } else {
        this.selectedNoteId = 0; 
        this.courseForm = this.fb.group({
          newNote: ['']
        });
      }
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

  addCourseToList(courseDetails: CourseDetails) {
    let isCoursePresent = this.userSessionStore.courseExists(courseDetails);
    if (isCoursePresent){
      this.showAddFailNotificationMethod()
      console.log("course already exists"); 
    } else {
      const userId = localStorage.getItem('userId');
      if (userId)
      this.userSvc.addRegisteredCourseToUser(userId, courseDetails).subscribe({
        next: (response:any) => {
          this.userSessionStore.addCourseToUser(response);
          this.showSuccessAddCourseNotificationMethod();
          this.enrollUserInCourse(courseDetails.platformId)
        },
        error: (error:any) => {
          console.error('Failed to add the course', error);
        }
      });
    }
  }

  enrollUserInCourse(platformId:string ) {
    const enrolledCourses = JSON.parse(localStorage.getItem('enrolledCourses') || '[]');
    if (!enrolledCourses.includes(platformId)) {
      enrolledCourses.push(platformId);
      localStorage.setItem('enrolledCourses', JSON.stringify(enrolledCourses));
      this.updateEnrollmentStatus();
    }
  }

  isUserEnrolled(platformId: string): boolean {
    const enrolledCourses = JSON.parse(localStorage.getItem('enrolledCourses') || '[]');
    return enrolledCourses.includes(platformId);
  }

  updateEnrollmentStatus(): void {
    const platformId = this.platformId; // Ensure this is set correctly in context
    this.isUserEnrolledLS = this.isUserEnrolled(platformId);
    console.log('Updated isUserEnrolledLS:', this.isUserEnrolledLS);
  }

  saveNote(): void {
    const noteText = this.courseForm.value.newNote.trim();
    const userId = localStorage.getItem('userId');
    if (userId)
    if (noteText) {
      const note: CourseNote = {
        noteId: this.selectedNoteId, 
        courseId: this.courseId,
        userId: userId,
        text: noteText
      };
      console.log("courseId " + note.courseId);
      
      this.courseSvc.saveNote(note).subscribe({
        next: (savedNote) => {
          this.userSessionStore.updateCourseNote(savedNote);
          this.selectedNoteId = savedNote.noteId;
          this.showSuccessSaveNoteNotificationMethod();
        },
        error: (error) => {
          console.error('Error saving note', error)
          this.showAddFailNotificationMethod();
        },
      });
    }
  }

  //success - save note
  showSuccessSaveNoteNotificationMethod() {
    this.showSuccessSaveNoteNotification = true;
    setTimeout(() => {
      this.showSuccessSaveNoteNotification = false;
    }, 3000);
  }

   //success - add course
  showSuccessAddCourseNotificationMethod(){
    this.showSuccessAddCourseNotification = true;
    setTimeout(() => {
      this.showSuccessAddCourseNotification = false;
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

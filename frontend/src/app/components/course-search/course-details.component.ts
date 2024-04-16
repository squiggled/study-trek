import { Component, HostListener, OnInit, inject } from '@angular/core';
import { Observable, of, from, combineLatest } from 'rxjs';
import {
  map,
  take,
  switchMap,
  catchError,
  tap,
  finalize,
} from 'rxjs/operators';
import {
  CourseDetails,
  CourseNote,
  Curriculum,
  Platform,
  defaultCourseDetails,
} from '../../models';
import { ActivatedRoute } from '@angular/router';
import { SearchService } from '../../services/search.service';
import { CourseDetailsStore } from '../../stores/course-details.store';
import { CommonUtilsService } from '../../services/common.utils.service';
import { UserSessionStore } from '../../stores/user.store';
import { UserService } from '../../services/user.service';
import { CourseService } from '../../services/course.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { ThemeService } from '../../services/theme.service';
import { CurriculumStore } from '../../stores/curriculum.store';

@Component({
  selector: 'app-course-details',
  templateUrl: './course-details.component.html',
  styleUrl: './course-details.component.css',
})
export class CourseDetailsComponent implements OnInit {
  userId!: string;
  courseId!: number | undefined;
  platformId!: string;
  newNote: string = '';
  selectedNoteId = 0;
  platform!: string;
  isLoading: boolean = true;

  showSuccessSaveNoteNotification: boolean = false;
  showSuccessAddCourseNotification: boolean = false;
  showAddFailNotification: boolean = false;
  isScrolled = false;
  isLoggedIn: boolean = localStorage.getItem('isLoggedIn') === 'true';
  isUserEnrolledLS!: boolean;
  isDarkMode: boolean = false;

  isUserEnrolled$!: Observable<boolean>;
  courseDetails$!: Observable<CourseDetails>;
  courseNote$!: Observable<CourseNote | undefined>;
  curriculumItems$!: Observable<Curriculum[]>;
  curriculumDisplayItems$!: Observable<any[]>;
  year = new Date().getFullYear();

  //services
  private activatedRoute = inject(ActivatedRoute);
  private searchSvc = inject(SearchService);
  private themeSvc = inject(ThemeService);
  private utilsSvc = inject(CommonUtilsService);
  private userSvc = inject(UserService);
  private courseSvc = inject(CourseService);

  //stores
  private courseDetailsStore = inject(CourseDetailsStore);
  private userSessionStore = inject(UserSessionStore);
  private curriculumStore = inject(CurriculumStore);

  courseForm!: FormGroup;

  constructor(private fb: FormBuilder, private titleService: Title) {}

  // ngOnInit(): void {
  //   this.isDarkMode = this.themeSvc.isDarkMode();
  //   this.titleService.setTitle('Study Trek | Search');

  //   this.activatedRoute.params.pipe(
  //     take(1),
  //     switchMap(params => {
  //       const platformId = params['courseId'];
  //       const platform = params['platform'];
  //       this.platform = platform;
  //       console.log("this.platformId ", platformId);

  //       this.platformId = platformId;
  //       this.initialiseForm(this.platformId);

  //       const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';

  //       if (isLoggedIn) {
  //         this.isLoggedIn=isLoggedIn;
  //         console.log("islogged in ", isLoggedIn);

  //         const accountDetails = JSON.parse(localStorage.getItem('accountDetails') || '{}');
  //         this.userId=accountDetails.userId;
  //         console.log("UserId in component ", this.userId);

  //         // find the course in registeredCourses by platformId
  //         const course = accountDetails.registeredCourses?.find((c:any) => c.platformId === platformId);
  //         console.log("this.platformId line 91", platformId);
  //         this.isUserEnrolledLS = this.isUserEnrolled(this.platformId);
  //         console.log('isUserEnrolled:', this.isUserEnrolledLS);
  //         console.log("all enrolled courses ", accountDetails.registeredCourses);

  //         if (course) {
  //           this.courseId = course.courseId;
  //           // console.log("courseId HERE", this.courseId);
  //           // populate course notes if they exist for this course
  //           this.courseNote$ = of(course.notes);
  //           return of(course);
  //         } else {
  //           return this.fetchCourseDetailsFromBackend(platformId, platform);
  //         }
  //       } else {
  //         // if user is not logged in, fetch course details from backend
  //         return this.fetchCourseDetailsFromBackend(platformId, platform);
  //       }
  //     }),
  //     //for curr
  //     tap(course => {
  //       if (course) {
  //         this.courseId = course.courseId;
  //         // console.log(`Initializing curriculum data load for course ID: ${this.courseId}`);
  //         this.curriculumStore.loadCurriculum(this.courseId, this.userId);
  //       }
  //     }),
  //     finalize(() => {
  //       this.isLoading = false;
  //       // console.log('After finalize, isLoading should be false:', this.isLoading); //should log false
  //     })
  //   ).subscribe(courseDetails => {
  //     this.courseDetails$ = of(courseDetails);
  //     this.refreshFormData(this.courseId);

  //   });
  //   this.curriculumDisplayItems$ = combineLatest([
  //     this.curriculumStore.curriculumItems,
  //     this.courseDetails$
  //   ]).pipe(
  //     map(([curriculumItems, courseDetails]) => {
  //       return curriculumItems.map(item => ({
  //         ...item,
  //         name: courseDetails.curriculum.find(detail => detail.curriculumId === item.curriculumId)?.title || 'Unknown',
  //         lectureNumber: item.lectureNumber
  //       }));
  //     })
  //   );
  //   this.isUserEnrolled$ = this.userSessionStore.isEnrolledInCourse(this.platformId);
  // this.isUserEnrolled$.subscribe(isEnrolled => {
  //   this.isUserEnrolledLS = isEnrolled;
  //   console.log('Enrollment status updated:', isEnrolled);
  // });
  // }
  ngOnInit(): void {
    this.isDarkMode = this.themeSvc.isDarkMode();
    this.titleService.setTitle('Study Trek | Search');

    this.activatedRoute.params
      .pipe(
        take(1),
        switchMap((params) => {
          this.platformId = params['courseId'];
          this.platform = params['platform'];
          this.initialiseForm(this.platformId);

          return this.checkLoginAndFetchCourseDetails();
        }),
        tap((course) => {
          if (course) {
            this.courseId = course.courseId;
            if (this.courseId !== undefined) {
              this.curriculumStore.loadCurriculum(this.courseId, this.userId);
            }
          }
        }),
        finalize(() => (this.isLoading = false))
      )
      .subscribe((courseDetails) => {
        this.courseDetails$ = of(courseDetails);
        if (this.courseId !== undefined) {
          this.refreshFormData(this.courseId);
        }
      });

    this.setupEnrollmentAndCurriculumDisplay();
  }

  private checkLoginAndFetchCourseDetails(): Observable<CourseDetails> {
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
    if (isLoggedIn) {
      const accountDetails = JSON.parse(
        localStorage.getItem('accountDetails') || '{}'
      );
      this.userId = accountDetails.userId;
      const course = accountDetails.registeredCourses?.find(
        (c: { platformId: string }) => c.platformId === this.platformId
      );
      if (course) return of(course);
    }
    return this.fetchCourseDetailsFromBackend(this.platformId, this.platform);
  }

  private setupEnrollmentAndCurriculumDisplay() {
    this.isUserEnrolled$ = this.userSessionStore.isEnrolledInCourse(
      this.platformId
    );
    this.curriculumDisplayItems$ = combineLatest([
      this.curriculumStore.curriculumItems,
      this.courseDetails$,
    ]).pipe(
      map(([curriculumItems, courseDetails]) =>
        curriculumItems.map((item) => ({
          ...item,
          name:
            courseDetails.curriculum.find(
              (detail) => detail.curriculumId === item.curriculumId
            )?.title || 'Unknown',
          lectureNumber: item.lectureNumber,
        }))
      )
    );
  }

  refreshFormData(courseId: number): void {
    this.courseSvc.getLatestCourseNoteByCourseId(courseId).subscribe(
      (note) => {
        if (note) {
          this.courseForm.patchValue({
            newNote: note.text,
          });
        } else {
          this.courseForm.reset({
            newNote: '',
          });
        }
      },
      (error) => {
        console.error('Failed to fetch the latest course note', error);
      }
    );
  }

  private fetchCourseDetailsFromBackend(platformId: string, platform: string) {
    return from(
      this.searchSvc.getCourseById(
        platformId,
        this.utilsSvc.toPlatformEnum(platform.toUpperCase())
      )
    ).pipe(
      map(
        (courseDetailsFromBackend) =>
          courseDetailsFromBackend ?? defaultCourseDetails
      ),
      catchError((error) => {
        console.error('Error fetching course details from backend:', error);
        return of(defaultCourseDetails);
      })
    );
  }

  initialiseForm(courseId: string): void {
    this.userSessionStore
      .getCourseNoteForCourse(this.selectedNoteId)
      .subscribe((note) => {
        if (note) {
          this.selectedNoteId = note.noteId;
          this.courseForm.patchValue({
            newNote: note.text,
          });
        } else {
          this.selectedNoteId = 0;
          this.courseForm = this.fb.group({
            newNote: [''],
          });
        }
      });
  }

  getPlatformLogo(platform: Platform): string {
    return this.utilsSvc.displayPlatformLogo(platform);
  }

  displayPlatformName(platform: Platform) {
    return this.utilsSvc.convertPlatformToStringFormatter(platform);
  }

  visitExternalUrl(url: string): void {
    if (url) {
      window.location.href = url;
    }
  }

  addCourseToList(courseDetails: CourseDetails) {
    console.log('coursedetails in component ', courseDetails);

    let isCoursePresent = this.userSessionStore.courseExists(courseDetails);
    if (isCoursePresent) {
      this.showAddFailNotificationMethod();
      console.log('course already exists');
    } else {
      const userId = localStorage.getItem('userId');
      if (userId)
        this.userSvc
          .addRegisteredCourseToUser(userId, courseDetails)
          .subscribe({
            next: (response: any) => {
              this.userSessionStore.addCourseToUser(response);
              this.showSuccessAddCourseNotificationMethod();
              // this.enrollUserInCourse(courseDetails)
            },
            error: (error: any) => {
              console.error('Failed to add the course', error);
            },
          });
    }
  }

  isUserEnrolled(platformId: string): boolean {
    // const enrolledCourses = JSON.parse(localStorage.getItem('enrolledCourses') || '[]');
    // return enrolledCourses.includes(platformId);
    console.log('platformId in method isUserEnroled ', platformId);

    const enrolledCourses: { platformId: string }[] = JSON.parse(
      localStorage.getItem('enrolledCourses') || '[]'
    );
    return enrolledCourses.some(
      (course) => course && course.platformId === platformId
    );
  }

  updateEnrollmentStatus(): void {
    const platformId = this.platformId;
    if (this.platformId) {
      this.isUserEnrolledLS = this.isUserEnrolled(this.platformId);
      // this.isUserEnrolledLS = this.isUserEnrolled(platformId);
      console.log('Updated isUserEnrolledLS:', this.isUserEnrolledLS);
    } else {
      console.error('platformId is not defined');
    }
  }

  saveNote(): void {
    const noteText = this.courseForm.value.newNote.trim();
    const userId = localStorage.getItem('userId');
    if (userId)
      if (noteText) {
        if (this.courseId !== undefined) {
          const note: CourseNote = {
            noteId: this.selectedNoteId,
            courseId: this.courseId,
            userId: userId,
            text: noteText,
          };
          console.log('courseId ' + note.courseId);

          this.courseSvc.saveNote(note).subscribe({
            next: (savedNote) => {
              this.userSessionStore.updateCourseNote(savedNote);
              this.selectedNoteId = savedNote.noteId;
              this.showSuccessSaveNoteNotificationMethod();
            },
            error: (error) => {
              console.error('Error saving note', error);
              this.showAddFailNotificationMethod();
            },
          });
        }
      }
  }

  //for curr toggling
  toggleCompletion(curriculumId: number, completed: boolean): void {
    // this.curriculumStore.updateCurriculumItem(this.userId, curriculumId, !completed);
    this.curriculumStore.updateCurriculumItemOptimistic(
      this.userId,
      curriculumId,
      !completed
    );

    // Perform the actual backend request
    this.curriculumStore
      .updateCurriculumItem(this.userId, curriculumId, !completed)
      .subscribe({
        error: (error: any) => {
          // Revert to the original state in case of an error
          console.error(
            'Failed to update curriculum item, reverting changes',
            error
          );
          this.curriculumStore.updateCurriculumItemOptimistic(
            this.userId,
            curriculumId,
            completed
          );
        },
      });
  }

  //success - save note
  showSuccessSaveNoteNotificationMethod() {
    this.showSuccessSaveNoteNotification = true;
    setTimeout(() => {
      this.showSuccessSaveNoteNotification = false;
    }, 3000);
  }

  //success - add course
  showSuccessAddCourseNotificationMethod() {
    this.showSuccessAddCourseNotification = true;
    setTimeout(() => {
      this.showSuccessAddCourseNotification = false;
    }, 3000);
  }

  showAddFailNotificationMethod() {
    this.showAddFailNotification = true;
    setTimeout(() => {
      this.showAddFailNotification = false;
    }, 3000);
  }

  @HostListener('window:scroll', ['$event'])
  onWindowScroll() {
    const threshold = 100;
    this.isScrolled = window.scrollY > threshold;
    console.log('is scrolled ' + this.isScrolled);
  }
}

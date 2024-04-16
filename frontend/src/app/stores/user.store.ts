import { Injectable } from '@angular/core';
import {
  AccountDetails,
  CourseDetails,
  CourseNote,
  UserPartialUpdate,
  UserSessionSlice,
  defaultAccountDetails,
} from '../models';
import { ComponentStore } from '@ngrx/component-store';
import { Observable, map } from 'rxjs';

const INIT_STATE: UserSessionSlice = {
  accountDetails: defaultAccountDetails,
  isAuthenticated: false,
};
@Injectable()
export class UserSessionStore extends ComponentStore<UserSessionSlice> {
  constructor() {
    super(INIT_STATE);
  }

  readonly isLoggedIn$ = this.select((state) => state.isAuthenticated);
  readonly firstName$ = this.select((state) => state.accountDetails.firstName);
  readonly userId$ = this.select((state) => state.accountDetails.userId);
  readonly email$ = this.select((state) => state.accountDetails.email);
  readonly profilePic$ = this.select(
    (state) => state.accountDetails.profilePicUrl
  );
  isEnrolledInCourse(courseId: string): Observable<boolean> {
    return this.select(state =>
      state.accountDetails.registeredCourses.some(course => course.platformId === courseId)
    );
  }

  readonly loginSuccess = this.updater(
    (state, { accountDetails, isAuthenticated }: UserSessionSlice) => ({
      ...state,
      accountDetails,
      isAuthenticated,
    })
  );

  readonly updateUserDetails = this.updater(
    (state, accountDetails: AccountDetails) => ({
      ...state,
      accountDetails,
    })
  );

  readonly updateUserPartialDetails = this.updater(
    (state, update: UserPartialUpdate) => ({
      ...state,
      accountDetails: {
        ...state.accountDetails,
        ...update,
      },
    })
  );

  resetState(): void {
    this.setState({
      accountDetails: defaultAccountDetails,
      isAuthenticated: false,
    });
  }

  //profile pic
  readonly updateProfilePicUrl = this.updater(
    (state, profilePicUrl: string) => ({
      ...state,
      accountDetails: {
        ...state.accountDetails,
        profilePicUrl: profilePicUrl,
      },
    })
  );

  //update pw hash
  readonly updatePasswordHash = this.updater(
    (state, newPasswordHash: string) => ({
      ...state,
      accountDetails: {
        ...state.accountDetails,
        passwordHash: newPasswordHash,
      },
    })
  );
  
  //adding courses
  readonly addCourseToUser = this.updater((state, newCourse: CourseDetails) => {
    const newState = {
      ...state,
      accountDetails: {
        ...state.accountDetails,
        registeredCourses: [...state.accountDetails.registeredCourses, newCourse],
      },
    };
    localStorage.setItem('enrolledCourses', JSON.stringify(newState.accountDetails.registeredCourses))
    localStorage.setItem('accountDetails', JSON.stringify(newState.accountDetails));
    return newState;
  });

  //checking if course exists
  courseExists(newCourse: CourseDetails): boolean {
    return this.get((state) =>
      state.accountDetails.registeredCourses.some(
        (course) => course.platformId === newCourse.platformId
      )
    );
  }
  //course notes
  readonly getCourseNoteForCourse = (courseId: number): Observable<CourseNote | undefined> => {
    return this.select(state =>
      state.accountDetails.registeredCourses
        .find(course => course.courseId === courseId)?.notes
    );
  }

  readonly updateCourseNote = this.updater((state, updatedNote: CourseNote) => {
    const updatedCourses = state.accountDetails.registeredCourses.map(course => {
      if (course.courseId === updatedNote.courseId) {
        return { ...course, notes: updatedNote };
      }
      return course;
    });
    return { ...state, accountDetails: {
        ...state.accountDetails, registeredCourses: updatedCourses,
      },
    };
  });

  readonly getCourseDetailsByPlatformId = (platformId: string): Observable<CourseDetails | undefined> => {
    return this.select(state =>
      state.accountDetails.registeredCourses.find(course => course.platformId === platformId)
    );
  };
  
  // readonly getCourseIdByPlatformId = (platformId: string): Observable<number | undefined> => {
  //   return this.select(state =>
  //     state.accountDetails.registeredCourses.find(course => course.platformId === platformId)?.courseId
  //   );
  // };
  readonly getCourseIdByPlatformId = (platformId: string): Observable<number> => {
    return this.select(state => {
      const course = state.accountDetails.registeredCourses.find(course => course.platformId === platformId);
      if (course) {
        return course.courseId;
      } else {
        throw new Error(`Course with platform ID ${platformId} not found.`);
      }
    }) as Observable<number>;
  };
  
}
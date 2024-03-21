import { Injectable } from '@angular/core';
import {
  AccountDetails,
  CourseDetails,
  UserSessionSlice,
  defaultAccountDetails,
} from '../models';
import { ComponentStore } from '@ngrx/component-store';

const INIT_STATE: UserSessionSlice = {
  accountDetails: defaultAccountDetails,
  isAuthenticated: false,
};
@Injectable()
export class UserSessionStore extends ComponentStore<UserSessionSlice> {
  constructor() {
    super(INIT_STATE);
  }

  //updater
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

  readonly isLoggedIn$ = this.select((state) => state.isAuthenticated);
  readonly firstName$ = this.select((state) => state.accountDetails.firstName);
  readonly userId$ = this.select((state) => state.accountDetails.userId);

  readonly addCourseToUser = this.updater((state, newCourse: CourseDetails) => {
    return {
      ...state,
      accountDetails: {
        ...state.accountDetails,
        registeredCourses: [
          ...state.accountDetails.registeredCourses,
          newCourse,
        ],
      },
    };
  });

  courseExists(newCourse: CourseDetails): boolean {
    return this.get((state) =>
      state.accountDetails.registeredCourses.some(
        (course) => course.platformId === newCourse.platformId
      )
    );
  }

  resetState(): void {
    this.setState({
      accountDetails: defaultAccountDetails, 
      isAuthenticated: false,
    });
  }
}

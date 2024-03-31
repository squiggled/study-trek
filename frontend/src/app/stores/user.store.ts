import { Injectable } from '@angular/core';
import {
  AccountDetails,
  CourseDetails,
  UserPartialUpdate,
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

  readonly isLoggedIn$ = this.select((state) => state.isAuthenticated);
  readonly firstName$ = this.select((state) => state.accountDetails.firstName);
  readonly userId$ = this.select((state) => state.accountDetails.userId);
  readonly email$ = this.select((state) => state.accountDetails.email);
  readonly profilePic$ = this.select(
    (state) => state.accountDetails.profilePicUrl
  );

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
}

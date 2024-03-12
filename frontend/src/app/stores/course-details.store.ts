import { Injectable } from "@angular/core";
import { CourseDetails, CourseDetailsSlice, Platform, defaultCourseDetails } from "../models";
import { ComponentStore } from "@ngrx/component-store";

const INIT_STATE: CourseDetailsSlice = {
    courseDetails: defaultCourseDetails,
    currentCourseId: "-1",
    currentPlatform: Platform.OTHER,
}

@Injectable()
export class CourseDetailsStore extends ComponentStore<CourseDetailsSlice>{

    constructor(){super(INIT_STATE)}

    storeCourseDetails(foundCourse: CourseDetails) {
        this.storeCourseDetailsResult(foundCourse); 
    }

    readonly storeCourseDetailsResult = this.updater<CourseDetails>(
        (_state: CourseDetailsSlice, newCourse: CourseDetails): CourseDetailsSlice => ({
          courseDetails: newCourse,
          currentCourseId: newCourse.platformId,
          currentPlatform: newCourse.platform
        })
      );

    readonly getCourseDetails = this.select<CourseDetails>( 
      (slice: CourseDetailsSlice) => slice.courseDetails
    );

    readonly updateCurrentCourse = this.updater((state, { courseId, platform }: { courseId: string; platform: Platform }) => ({
      ...state,
      currentCourseId: courseId,
      currentPlatform: platform,
    }));
  
    readonly getCurrentCourseId = this.select(state => state.currentCourseId);
    readonly getCurrentPlatform = this.select(state => state.currentPlatform);
  
    
}
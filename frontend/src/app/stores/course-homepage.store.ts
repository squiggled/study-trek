import { ComponentStore } from "@ngrx/component-store";
import { CourseSearch, HomePageCourseListingSlice } from "../models";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

const INIT_STATE: HomePageCourseListingSlice = {
    originalHomePageCourseListing: [],
    homePageCourseListing: []
}
@Injectable()
export class HomePageCourseListingStore extends ComponentStore<HomePageCourseListingSlice>{
    
    constructor(){super(INIT_STATE)}

    //selector to get courselisting
    readonly homePageCourseListing$: Observable<CourseSearch[]> = this.select(
        state => state.homePageCourseListing
    );

    //selector for OG unfiltered list
    readonly originalHomePageCourseListing$: Observable<CourseSearch[]> = this.select(
        state => state.originalHomePageCourseListing
    );


    //updater to set the course listings
    readonly setHomePageCourseListing = this.updater((state, courses: CourseSearch[]) => ({
        originalHomePageCourseListing: courses,
        homePageCourseListing: [...courses] //shallow copy for filterable list
    }));

    
    //filter course by category
    readonly filterCoursesByCategory = this.updater((state, category: string) => {
        const filteredCourses = state.homePageCourseListing.filter(course => course.category === category);
        return {
            ...state,
            homePageCourseListing: filteredCourses
        };
    });

     //filter for  course listing
     readonly filterHomePageCourseListing = this.updater((state, category: string) => ({
        ...state,
        homePageCourseListing: state.originalHomePageCourseListing.filter(course => course.category === category)
    }));

    // to reset the filter and use the original course listing
    readonly resetFilter = this.updater((state) => ({
        ...state,
        homePageCourseListing: [...state.originalHomePageCourseListing] 
    }));


}
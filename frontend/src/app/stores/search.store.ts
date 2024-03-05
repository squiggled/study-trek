import { Injectable } from "@angular/core";
import { CourseSearch, CourseSearchSlice } from "../models";
import { ComponentStore } from "@ngrx/component-store";

const INIT_STATE: CourseSearchSlice = {
    courseListing: []
}

@Injectable()
export class CourseSearchStore extends ComponentStore<CourseSearchSlice>{
    constructor(){super(INIT_STATE)}

    storeCourses(foundCourses: CourseSearch[]) {
        this.storeCourseSearchResults(foundCourses); 
    }

    readonly storeCourseSearchResults = this.updater<CourseSearch[]>( 
        (state: CourseSearchSlice, searchResults: CourseSearch[]) => {
            const newSlice: CourseSearchSlice = {
                courseListing: [...searchResults]
            };
        return newSlice;
    }) 

    readonly getCourseSearchResults = this.select<CourseSearch[]>( 
        (slice: CourseSearchSlice) => slice.courseListing
    )
    
}

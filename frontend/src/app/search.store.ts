import { Injectable } from "@angular/core";
import { CourseSearchSlice } from "./models";

@Injectable()
export class CourseSearchStore {


    const INIT_STATE: CourseSearchSlice = {
        courseListing: []
    }
constructor(){super(INIT_STATE)}



    storeCourses(foundCourses: import("./models").CourseSearch[]) {
        throw new Error("Method not implemented.");
    }
    
}

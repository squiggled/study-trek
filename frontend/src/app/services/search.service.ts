import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { CourseSearchStore } from "../stores/search.store";
import { CourseDetails, CourseSearch, Platform } from "../models";
import { Router } from "@angular/router";
import { CourseDetailsStore } from "../stores/course-details.store";

@Injectable()
export class SearchService{
    private httpClient = inject(HttpClient);
    private searchStore = inject(CourseSearchStore);
    private courseDetailsStore = inject(CourseDetailsStore);
    private router = inject(Router);

    currentPage:number = 0;
    query!:string;


    querySearch(query: string, page?: number, platform?: Platform, byRating?:string){
        this.query=query;
        let params = new HttpParams().set('query', query);
        if (page !== undefined) {
            this.currentPage = page;
            params = params.append('page', this.currentPage.toString());
        }
        if (platform) {
            params = params.append('platform', platform.toString());
        }
        if (byRating){
            params = params.append('byRating', true);
        }
        firstValueFrom(this.httpClient.get<CourseSearch[]>('/api/courses/search', {params}))
            .then(response => {
                this.searchStore.storeCourses(response);
                this.router.navigate(['/courses/search'], { queryParams: { query: query, page: this.currentPage } })
            })
            .catch(error => {
                console.error('Error fetching courses:', error);
            });
    }

    getCourseById(courseId: string, platform:Platform):void{
        console.log("courseid ", courseId);
        
        let params = new HttpParams().set("courseId", courseId).append("platform", platform.toString());
        firstValueFrom(this.httpClient.get<CourseDetails>('/api/course', {params}))
            .then (response => {
                this.courseDetailsStore.storeCourseDetails(response);
            })
            .catch(error => {
                console.error('Error fetching course details:', error);
            });

    }
    

}
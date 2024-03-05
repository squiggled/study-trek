import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { CourseSearchStore } from "../search.store";
import { CourseSearch, Platform } from "../models";
import { Router } from "@angular/router";

@Injectable()
export class SearchService{
    private httpClient = inject(HttpClient);
    private searchStore = inject(CourseSearchStore);
    private router = inject(Router);

    currentPage:number = 0;
    query!:string;


    querySearch(query: string, page?: number){
        this.query=query;
        let params = new HttpParams().set('query', query);
        if (page !== undefined) {
            this.currentPage = page;
            params = params.append('page', this.currentPage.toString());
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

    getCourseById(courseId: string, platform:Platform){

    }
    

}
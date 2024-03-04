import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { SearchStore } from "../search.store";
import { SearchResult } from "../models";

@Injectable()
export class SearchService{
    private httpClient = inject(HttpClient);
    private searchStore = inject(SearchStore);

    nextPageUrl!: string;
    prevPageUrl!: string;

    querySearch(query: string){
        const params = new HttpParams().set('query', query);
        firstValueFrom(this.httpClient.get<SearchResult>('/api/courses/search', {params}))
            .then(response => {
                // Process the response here
                this.prevPageUrl = response.prevPageUrl;
                this.nextPageUrl = response.nextPageUrl;
                console.log('Next Page URL:', this.nextPageUrl);
                this.searchStore.storeCourses(response.foundCourses);
            })
            .catch(error => {
                console.error('Error fetching courses:', error);
            });
    }

}
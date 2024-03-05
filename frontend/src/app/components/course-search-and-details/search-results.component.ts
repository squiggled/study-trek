import { Component, OnInit, inject } from '@angular/core';
import { CourseSearchStore } from '../../stores/search.store';
import { Observable } from 'rxjs';
import { CourseSearch, Platform } from '../../models';
import { ActivatedRoute, Router } from '@angular/router';
import { SearchService } from '../../services/search.service';

@Component({
  selector: 'app-search-results',
  templateUrl: './search-results.component.html',
  styleUrl: './search-results.component.css'
})

export class SearchResultsComponent implements OnInit {
  
  courseSearchResults$!: Observable<CourseSearch[]>;
  private searchStore = inject(CourseSearchStore);
  private activatedRoute = inject(ActivatedRoute);
  private searchSvc =  inject(SearchService);
  private router =  inject(Router);

  private lastQuery: string | null = null;
  private lastPage: number | null = null;

  ngOnInit(): void {
    this.courseSearchResults$ = this.searchStore.getCourseSearchResults;
    this.activatedRoute.queryParams.subscribe(params => {
      const query = params['query'];
      const page = +params['page'] || 1; //convert to number and default page 1 

      //search only if the params have changed
      if (query !== this.lastQuery || page !== this.lastPage) {
        this.performSearch(query, page);

        this.lastQuery = query;
        this.lastPage = page;
      }
    });

  }
  performSearch(query: string, page: number): void {
    this.searchSvc.querySearch(query, page);
  }

  getPlatformLogo(platform: Platform): string {
    console.log("platform ", platform);
    
    switch(platform) {
      case Platform.UDEMY:
        return 'logo-udemy.png'; 
      case Platform.EDX:
        return 'logo-edX.png'; 
        case Platform.COURSERA:
        return 'logo-coursera.png';
      default:
        return ''; 
    }
  }
  getCourseDetails(courseId: number, platform:Platform){
    this.searchSvc.getCourseById(courseId, platform);
    this.router.navigate(['/course', platform.toString().toLowerCase(), courseId]);
  }


}

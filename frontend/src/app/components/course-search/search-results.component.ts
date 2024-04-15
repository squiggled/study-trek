import { Component, OnInit, inject } from '@angular/core';
import { CourseSearchStore } from '../../stores/search.store';
import { Observable, finalize } from 'rxjs';
import { CourseSearch, Platform } from '../../models';
import { ActivatedRoute, Router } from '@angular/router';
import { SearchService } from '../../services/search.service';
import { CommonUtilsService } from '../../services/common.utils.service';
import { Title } from '@angular/platform-browser';


@Component({
  selector: 'app-search-results',
  templateUrl: './search-results.component.html',
  styleUrl: './search-results.component.css',
})
export class SearchResultsComponent implements OnInit {
  courseSearchResults$!: Observable<CourseSearch[]>;
  private searchStore = inject(CourseSearchStore);
  private activatedRoute = inject(ActivatedRoute);
  private searchSvc = inject(SearchService);
  private utilsSvc = inject(CommonUtilsService);
  private router = inject(Router);

  lastQuery: string | null = null;
  private lastPage: number | null = null;
  currentPage: number = 1;
  totalPages: number = 100;
  currentSort: string = 'Most relevant';
  isLoading:boolean = false;

  constructor(private titleService: Title) { }


  ngOnInit(): void {
    this.titleService.setTitle('Study Trek | Search Result');
    this.courseSearchResults$ = this.searchStore.getCourseSearchResults;
    this.activatedRoute.queryParams.subscribe((params) => {
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
  performSearch(
    query: string,
    page: number,
    platform?: Platform,
    byRating?: string
  ): void {
    this.isLoading = true;
    this.currentPage = page;
    this.searchSvc.querySearch(query, this.currentPage, platform, byRating);
    setTimeout(() => {
      this.isLoading = false; 
    }, 1000);
  }

  filterBy(sortCriteria?: string) {
    if (this.lastQuery) {
      this.lastPage = 1;
      let platform: Platform | undefined;
      let byRating: string | undefined;
      if (sortCriteria === 'mostRelevant') {
        this.performSearch(this.lastQuery, 1);
      } else if (sortCriteria === 'Udemy' || sortCriteria === 'Coursera') {
        platform = sortCriteria as Platform;
        this.currentSort = sortCriteria;
      } else if (sortCriteria === 'rating') {
        byRating = 'byRating';
        this.currentSort = 'Rating';
      }
      this.performSearch(this.lastQuery, 1, platform as Platform, byRating);
    }
  }
  getPlatformLogo(platform: Platform): string {
    return this.utilsSvc.displayPlatformLogo(platform);
  }

  getCourseDetails(courseId: string, platform: Platform) {
    console.log('course id in result comp ', courseId);

    this.searchSvc.getCourseById(courseId, platform);
    this.router.navigate([
      '/course',
      platform.toString().toLowerCase(),
      courseId,
    ]);
  }

  navigateToPage(page: number): void {
    if (page < 1 || page > this.totalPages) return; //make sure no invalid page number
    this.performSearch(this.lastQuery || '', page);
    this.currentPage = page;
    //update URL without navigating
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: { query: this.lastQuery, page: page },
      queryParamsHandling: 'merge',
    });
  }

  nextPage(): void {
    this.navigateToPage(this.currentPage + 1);
  }

  previousPage(): void {
    this.navigateToPage(this.currentPage - 1);
  }
}

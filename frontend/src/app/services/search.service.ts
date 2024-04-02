import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { CourseSearchStore } from '../stores/search.store';
import { CourseDetails, CourseSearch, Platform } from '../models';
import { Router } from '@angular/router';
import { CourseDetailsStore } from '../stores/course-details.store';
import { HomePageCourseListingStore } from '../stores/course-homepage.store';

@Injectable()
export class SearchService {
  private httpClient = inject(HttpClient);
  private searchStore = inject(CourseSearchStore);
  private courseDetailsStore = inject(CourseDetailsStore);
  private homepageCourseStore = inject(HomePageCourseListingStore);
  private router = inject(Router);

  currentPage: number = 0;
  query!: string;

  querySearch(
    query: string,
    page?: number,
    platform?: Platform,
    byRating?: string
  ) {
    this.query = query;
    let params = new HttpParams().set('query', query);
    if (page !== undefined) {
      this.currentPage = page;
      params = params.append('page', this.currentPage.toString());
    }
    if (platform) {
      params = params.append('platform', platform.toString());
    }
    if (byRating) {
      params = params.append('byRating', true);
    }
    firstValueFrom(
      this.httpClient.get<CourseSearch[]>('/api/courses/search', { params })
    )
      .then((response) => {
        this.searchStore.storeCourses(response);
        this.router.navigate(['/courses/search'], {
          queryParams: { query: query, page: this.currentPage },
        });
      })
      .catch((error) => {
        console.error('Error fetching courses:', error);
      });
  }

  async getCourseById(
    courseId: string,
    platform: Platform
  ): Promise<CourseDetails> {
    console.log('courseId ', courseId);

    let params = new HttpParams()
      .set('courseId', courseId)
      .append('platform', platform.toString());

    try {
      const courseDetails: CourseDetails = await firstValueFrom(
        this.httpClient.get<CourseDetails>('/api/course', { params })
      );
      return courseDetails;
    } catch (error) {
      console.error('Error fetching course details:', error);
      throw error;
    }
  }

  loadHomepageCourses() {
    return this.httpClient.get<CourseSearch[]>('/api/courses/loadhomepage');
  }
}

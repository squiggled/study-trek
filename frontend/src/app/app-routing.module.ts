import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SearchResultsComponent } from './components/course-search-and-details/search-results.component';
import { CourseDetailsComponent } from './components/course-search-and-details/course-details.component';

const routes: Routes = [
  {path: 'course/:platform/:courseId', component: CourseDetailsComponent},
  {path: 'courses/search', component: SearchResultsComponent},
  { path: '**', redirectTo: '/', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

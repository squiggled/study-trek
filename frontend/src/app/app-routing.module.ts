import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SearchResultsComponent } from './components/course-search-and-details/search-results.component';
import { CourseDetailsComponent } from './components/course-search-and-details/course-details.component';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { RegisterComponent } from './components/register/register.component';
import { MyCoursesComponent } from './components/user-session/courses.component';

const routes: Routes = [
  { path: '', component: HomeComponent},
  { path: 'course/:platform/:courseId', component: CourseDetailsComponent},
  { path: 'courses/search', component: SearchResultsComponent},
  { path: 'join/login', component: LoginComponent},
  { path: 'join/register', component: RegisterComponent},
  { path: 'home/my-courses', component: MyCoursesComponent},
  { path: '**', redirectTo: '/', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'top' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SearchComponent } from './components/navbar/search.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { SearchService } from './services/search.service';
import { NavbarComponent } from './components/navbar/navbar.component';
import { SearchResultsComponent } from './components/course-search-and-details/search-results.component';
import { CourseSearchStore } from './stores/search.store';
import { CourseDetailsComponent } from './components/course-search-and-details/course-details.component';
import { CourseDetailsStore } from './stores/course-details.store';
import { CommonUtilsService } from './services/common.utils.service';
import { LoginComponent } from './components/login/login.component';
import { AuthService } from './services/auth.service';
import { HomeComponent } from './components/home/home.component';
import { RegisterComponent } from './components/register/register.component';
import { UserSessionStore } from './stores/user.store';
import { UserService } from './services/user.service';
import { AuthInterceptor } from './services/auth.interceptor';
import { MyCoursesComponent } from './components/user-session/courses.component';

@NgModule({
  declarations: [
    AppComponent,
    SearchComponent,
    NavbarComponent,
    SearchResultsComponent,
    CourseDetailsComponent,
    LoginComponent,
    HomeComponent,
    RegisterComponent,
    MyCoursesComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    SearchService, 
    CourseSearchStore, 
    CourseDetailsStore, 
    CommonUtilsService,
    AuthService,
    UserSessionStore,
    UserService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

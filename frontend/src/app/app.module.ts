import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SearchComponent } from './components/navbar/search.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SearchService } from './services/search.service';
import { NavbarComponent } from './components/navbar/navbar.component';
import { SearchResultsComponent } from './components/course-search/search-results.component';
import { CourseSearchStore } from './stores/search.store';
import { CourseDetailsComponent } from './components/course-search/course-details.component';
import { CourseDetailsStore } from './stores/course-details.store';
import { CommonUtilsService } from './services/common.utils.service';
import { LoginComponent } from './components/login/login.component';
import { AuthService } from './services/auth.service';
import { HomeComponent } from './components/home/home.component';
import { RegisterComponent } from './components/register/register.component';
import { UserSessionStore } from './stores/user.store';
import { UserService } from './services/user.service';
import { AuthInterceptor } from './services/auth.interceptor';
import { MyCoursesComponent } from './components/user-courses/courses.component';
import { HomePageCourseListingStore } from './stores/course-homepage.store';
import { UserProfileComponent } from './components/user-profile/user-profile.component';
import { ProfileNavComponent } from './components/user-profile/profile-nav.component';
import { ProfileContentBaseComponent } from './components/user-profile/profile-content-base.component';
import { EditAccountComponent } from './components/user-profile/contents/edit-account.component';
import { FriendsComponent } from './components/user-profile/contents/friends.component';
import { PhotoComponent } from './components/user-profile/contents/photo.component';
import { SubscriptionComponent } from './components/user-profile/contents/subscription.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NotificationService } from './services/notification.service';
import { FriendListStore } from './stores/friends.store';
import { FriendService } from './services/friend.service';
import { CourseNavigatorComponent } from './components/course-ai-navigator/course-navigator.component';
import { OpenAIService } from './services/openai.service';
import { SubscribeComponent } from './components/subscribe/subscribe.component';
import { SubscriptionService } from './services/subscription.service';
import { SubscribeSuccessComponent } from './components/subscribe/success.component';
import { SubscribeFailComponent } from './components/subscribe/fail.component';
import { ServiceWorkerModule } from '@angular/service-worker';
import { ForumService } from './services/forum.service';
import { ForumComponent } from './components/forum/forum.component';
import { CreateThreadComponent } from './components/forum/contents/create-thread.component';
import { IndividualThreadComponent } from './components/forum/contents/individual-thread.component';
import { ImageCropperModule } from 'ngx-image-cropper';
import { EditPasswordComponent } from './components/user-profile/contents/edit-password.component';
import { CourseService } from './services/course.service';
import { CalendarComponent } from './components/calendar/calendar.component';
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { CommonModule } from '@angular/common';
import { CalendarService } from './services/calendar.service';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CalendarStore } from './stores/calendar.store';
import { CreateEventComponent } from './components/calendar/create-event.component';
import { CurriculumService } from './services/curriculum.service';
import { CurriculumStore } from './stores/curriculum.store';
import { TelegramService } from './services/telegram.service';
import { TelegramComponent } from './components/telegram/telegram.component';

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
    MyCoursesComponent,
    UserProfileComponent,
    ProfileNavComponent,
    ProfileContentBaseComponent,
    EditAccountComponent,
    FriendsComponent,
    PhotoComponent,
    SubscriptionComponent,
    CourseNavigatorComponent,
    SubscribeComponent,
    SubscribeSuccessComponent,
    SubscribeFailComponent,
    ForumComponent,
    CreateThreadComponent,
    IndividualThreadComponent,
    EditPasswordComponent,
    CalendarComponent,
    CreateEventComponent,
    TelegramComponent,
  ],
  imports: [
    CommonModule,
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    ImageCropperModule,
    FormsModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: !isDevMode(),
      registrationStrategy: 'registerWhenStable:30000'
    }),
    CalendarModule.forRoot({
      provide: DateAdapter,
      useFactory: adapterFactory,
    }),
    MatTooltipModule

  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    CommonUtilsService,
    SearchService,
    CourseSearchStore, 
    CourseDetailsStore, 
    CourseService,
    HomePageCourseListingStore,
    AuthService,
    UserService,
    UserSessionStore,
    OpenAIService,
    NotificationService,
    FriendService,
    FriendListStore,
    SubscriptionService,
    ForumService,
    CalendarService,
    CalendarStore,
    CurriculumService,
    CurriculumStore,
    TelegramService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

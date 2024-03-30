import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SearchComponent } from './components/navbar/search.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
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
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    ImageCropperModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: !isDevMode(),
      registrationStrategy: 'registerWhenStable:30000'
    })
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    CommonUtilsService,
    SearchService,
    CourseSearchStore, 
    CourseDetailsStore, 
    HomePageCourseListingStore,
    AuthService,
    UserService,
    UserSessionStore,
    OpenAIService,
    NotificationService,
    FriendService,
    FriendListStore,
    SubscriptionService,
    ForumService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

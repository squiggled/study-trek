import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SearchResultsComponent } from './components/course-search/search-results.component';
import { CourseDetailsComponent } from './components/course-search/course-details.component';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { RegisterComponent } from './components/register/register.component';
import { MyCoursesComponent } from './components/user-courses/courses.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';
import { EditAccountComponent } from './components/user-profile/contents/edit-account.component';
import { FriendsComponent } from './components/user-profile/contents/friends.component';
import { isLoggedIn, isSubscriber } from './guards/guards';
import { PhotoComponent } from './components/user-profile/contents/photo.component';
import { SubscriptionComponent } from './components/user-profile/contents/subscription.component';
import { CourseNavigatorComponent } from './components/course-ai-navigator/course-navigator.component';
import { SubscribeComponent } from './components/subscribe/subscribe.component';
import { SubscribeFailComponent } from './components/subscribe/fail.component';
import { SubscribeSuccessComponent } from './components/subscribe/success.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'course/:platform/:courseId', component: CourseDetailsComponent },
  { path: 'courses/search', component: SearchResultsComponent },
  { path: 'join/login', component: LoginComponent },
  { path: 'join/register', component: RegisterComponent },
  { path: 'join/subscribe',  //for the page that actually lets you subscribe
    component: SubscribeComponent, 
    canActivate: [ isLoggedIn ], 
  },
  { path: 'join/subscribe/success',  //sub status
    component: SubscribeSuccessComponent, 
    canActivate: [ isLoggedIn ], 
  },
  { path: 'join/subscribe/fail',  
    component: SubscribeFailComponent, 
    canActivate: [ isLoggedIn ], 
  },
  { path: 'home/my-courses', 
    component: MyCoursesComponent, 
    canActivate: [ isLoggedIn ], 
  },
  {
    path: 'home/profile',
    component: UserProfileComponent,
    canActivate: [ isLoggedIn ],
    children: [
      { path: 'edit-account', component: EditAccountComponent },
      { path: 'friends', component: FriendsComponent },
      { path: 'photo', component: PhotoComponent },
      { path: 'subscription', component: SubscriptionComponent }, //this page just gives sub status + links to subscribecomponent
  
    ]
  },
  { path: 'course-navigator', 
    component: CourseNavigatorComponent, 
    canActivate: [ isSubscriber ], 
    //canActivate: [ isSubscriber ]
  },
  { path: '**', redirectTo: '/', pathMatch: 'full' },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      scrollPositionRestoration: 'top',
      useHash: true,
    }),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}

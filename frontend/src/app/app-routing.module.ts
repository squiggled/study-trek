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
import { canLeaveCreateThread, isLoggedIn, isSubscriber } from './guards/guards';
import { PhotoComponent } from './components/user-profile/contents/photo.component';
import { SubscriptionComponent } from './components/user-profile/contents/subscription.component';
import { CourseNavigatorComponent } from './components/course-ai-navigator/course-navigator.component';
import { SubscribeComponent } from './components/subscribe/subscribe.component';
import { SubscribeFailComponent } from './components/subscribe/fail.component';
import { SubscribeSuccessComponent } from './components/subscribe/success.component';
import { ForumComponent } from './components/forum/forum.component';
import { CreateThreadComponent } from './components/forum/contents/create-thread.component';
import { IndividualThreadComponent } from './components/forum/contents/individual-thread.component';
import { EditPasswordComponent } from './components/user-profile/contents/edit-password.component';
import { CalendarComponent } from './components/calendar/calendar.component';
import { TelegramComponent } from './components/telegram/telegram.component';

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
      { path: 'change-password', component: EditPasswordComponent },
      { path: 'friends', component: FriendsComponent },
      { path: 'photo', component: PhotoComponent },
      { path: 'subscription', component: SubscriptionComponent }, //this page just gives sub status + links to subscribecomponent
  
    ]
  },
  { path: 'course-navigator', 
    component: CourseNavigatorComponent, 
    canActivate: [ isSubscriber ]
  },
  { path: 'forum', 
    component: ForumComponent,
    canActivate: [ isLoggedIn ],
  },
  { path: 'forum/create-topic', component:  CreateThreadComponent,
    canActivate: [ isLoggedIn ],
    canDeactivate: [ canLeaveCreateThread ]
  },
  { path: 'forum/topic/:threadId', component: IndividualThreadComponent,
    canActivate: [ isLoggedIn ]
  },
  {path: 'calendar', component:CalendarComponent,
    canActivate: [ isLoggedIn ]
  },
  {path: 'telegram', component:TelegramComponent,
    canActivate: [ isSubscriber ]
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

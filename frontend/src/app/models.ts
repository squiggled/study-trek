export enum Platform {
  UDEMY = 'UDEMY',
  EDX = 'EDX',
  COURSERA = 'COURSERA',
  OTHER = 'OTHER',
}

export interface CourseSearch {
  platform: Platform;
  platformId: string;
  title: string;
  headline: string;
  imageUrl: string;
  price: number;
  instructor: string;
  rating?: number;
  category?: string;
}

export interface CourseDetails {
  courseId?:number,
  userId?:string,
  platform: Platform;
  platformId: string;
  title: string;
  headline: string;
  category: string;
  urlToCourse: string;
  imageUrl: string;
  isPaid: boolean;
  price: number;
  instructor: string;
  curriculum: Curriculum[];
  notes?: CourseNote;
}

export const defaultCourseDetails: CourseDetails = {
  platform: Platform.OTHER,
  platformId: '-1',
  title: 'Loading course details',
  headline: '',
  category: '',
  urlToCourse: '',
  imageUrl: '',
  isPaid: false,
  price: 0,
  instructor: '',
  curriculum: [],
};

export interface Curriculum {
  lectureNumber: number;
  title: string;
  curriculumId: number;
  courseId:number;
  completed?:boolean
}

export interface CourseNote {
  noteId: number; 
  courseId:number;
  userId:string
  text: string;
}

export interface AccountDetails {
  userId: string;
  email: string;
  passwordHash: string;
  lastPasswordResetDate?: Date;

  firstName: string;
  lastName: string;
  profilePicUrl: string;

  telegramChatId?:number;

  interests: string[];
  courseNotes: string[];
  registeredCourses: CourseDetails[];
  friendIds: string[];

  role: string;
}

export const defaultAccountDetails: AccountDetails = {
    userId: '',
    email: '',
    passwordHash: '',
  
    firstName: '',
    lastName: '',
    profilePicUrl: '',
  
    interests: [],
    courseNotes: [],
    registeredCourses: [],
    friendIds: [],
  
    role: ''
};

export interface UserPartialUpdate {
  firstName: string;
  lastName: string;
  interests: string[];
}

export interface CourseSearchSlice {
  courseListing: CourseSearch[];
}

export interface HomePageCourseListingSlice {
  originalHomePageCourseListing: CourseSearch[];
  homePageCourseListing: CourseSearch[];
}

export interface CourseDetailsSlice {
  courseDetails: CourseDetails;
  currentCourseId: string;
  currentPlatform: Platform;
}

export interface UserSessionSlice {
  accountDetails: AccountDetails;
  isAuthenticated: boolean;
}

export interface FriendInfo{
  userId: string;
  email: string;

  firstName: string;
  lastName: string;
  profilePicUrl: string;

  interests: string[];
  registeredCourses: CourseDetails[];
  isFriend: boolean;
  status?: string
}

export interface FriendListSlice{
  friendList: FriendInfo[]
}

export interface FriendRequest{
  requestId: string;
  senderId: string;
  receiverId: string;
  status: string;
}

export interface Notification {
  id: string;
  type: string;
  message: string;
  relatedId: string;
  read: boolean;
  timestamp: Date;
  friendRequest?: FriendRequest;
}

export interface ThreadMessage{
  userId:string,
  firstName: string,
  userProfilePic: string;
  postedDate: Date;
  content: string
}

export interface ForumThread{
  id?:string,
  userId: string,
  firstName: string,
  userProfilePic: string,
  createdDate: Date;
  title: string,
  content: string,
  messages: ThreadMessage[]
}

export interface ForumSlice{
  threads: ForumThread[]
}

export interface MyCalendarEvent{
  calendarId?: number,
  userId: string,
  title: string,
  text: string,
  selectedHour: number,
  date: Date
}

export interface CalenderEventsSlice{
  events: MyCalendarEvent[]
}

export interface HourSegmentClickedEvent {
  date: Date;
sourceEvent: MouseEvent;
}


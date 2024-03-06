export enum Platform {
    UDEMY = 'UDEMY',
    EDX = 'EDX',
    COURSERA = 'COURSERA',
    OTHER = 'OTHER'
}

export interface CourseSearch {
    platform: Platform,
    id: number,
    title: string,
    headline: string,
    imageUrl: string,
    price: number,
    instructor: string
}

export interface CourseDetails {
    platform: Platform,
    id: number,
    title: string,
    headline: string,
    urlToCourse: string, 
    imageUrl: string,
    isPaid: boolean,
    price: number,
    instructor: string,
    curriculum: Curriculum[];

}

export const defaultCourseDetails: CourseDetails = {
    platform: Platform.OTHER,
    id: -1, 
    title: 'Loading course details',
    headline: '',
    urlToCourse: '',
    imageUrl: '',
    isPaid: false,
    price: 0,
    instructor: '',
    curriculum:[]

  };

export interface Curriculum {
    lectureNumber: number,
    title: string,
    completed: boolean
}

export interface AccountDetails {
    userId: string,
    email: string, 
    passwordHash: string,
    lastPasswordResetDate: Date,
   
    firstName: string,
    lastName: string, 
    profilePicUrl: string,

    interests: string[],
    courseNotes: string[],
    registeredCourses: CourseDetails[],
    friendIds: string[]
    
    roles: string[],
}

export interface CourseSearchSlice{
	courseListing:CourseSearch[]
}

export interface CourseDetailsSlice{
    courseDetails: CourseDetails;
    currentCourseId: number,
    currentPlatform: Platform,
}

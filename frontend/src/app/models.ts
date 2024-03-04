enum Platform {
    edX,
    Udemy,
    Coursera
  }

export interface SearchResult {
    foundCourses: CourseSearch[];
    prevPageUrl: string,
    nextPageUrl: string
}
export interface CourseSearch {
    platform: Platform,
    id: number,
    title: string,
    headline: string,
    price: number,
    instructor: string
}

export interface CourseDetails {
    platform: Platform,
    id: number,
    title: string,
    headline: string,
    url: string, 
    isPaid: boolean,
    price: number,
    instructor: string,
    curriculum: Curriculum[];

}

export interface Curriculum {
    lectureNumber: number,
    title: string,
    completed: boolean
}

export interface CourseSearchSlice{
	courseListing:CourseSearchSlice[]
}


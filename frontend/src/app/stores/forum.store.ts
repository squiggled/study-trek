import { Injectable } from '@angular/core';
import { ComponentStore, tapResponse } from '@ngrx/component-store';
import { switchMap, Observable, of, map } from 'rxjs';
import { ForumService } from '../services/forum.service';
import { ForumThread, ThreadMessage } from '../models';

export interface ForumState {
  threads: ForumThread[];
}

const INIT_STATE: ForumState = {
  threads: []
};

@Injectable({
  providedIn: 'root'
})
export class ForumStore extends ComponentStore<ForumState> {
  
  constructor(private forumSvc: ForumService) {
    super(INIT_STATE);
    this.loadThreads();
  }

  // selector for threads
  readonly threads$ = this.select(state => state.threads);

  // updater to set threads
  private readonly setThreads = this.updater((state, threads: ForumThread[]) => ({
    ...state,
    threads,
  }));

  //get 1 thread by id
  getThreadById(threadId: string): Observable<ForumThread | undefined> {
    return this.threads$.pipe(
      map(threads => threads.find(thread => thread.id === threadId))
    );
  }
  
  // to load threads from the backend
  readonly loadThreads = this.effect((trigger$: Observable<void>) =>
    trigger$.pipe(
      switchMap(() =>
        this.forumSvc.getAllThreads().pipe(
          tapResponse(
            (threads) => this.setThreads(threads),
            (error) => console.error('Error fetching threads:', error)
          )
        )
      )
    )
  );

  //add a thread to the store
  readonly addThread = this.updater((state, newThread: ForumThread) => ({
    ...state,
    threads: [...state.threads, newThread],
  }));

  updateThread(updatedThread: ForumThread) {
    this.patchState(state => {
      const threads = state.threads.map(t => t.id === updatedThread.id ? updatedThread : t);
      return { threads };
    });
  }
}

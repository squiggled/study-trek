import { Injectable } from "@angular/core";
import {  AccountDetails, UserSessionSlice, defaultAccountDetails } from "../models";
import { ComponentStore } from "@ngrx/component-store";

const INIT_STATE: UserSessionSlice = {
    accountDetails: defaultAccountDetails,
    isAuthenticated: false
}
@Injectable()
export class UserSessionStore extends ComponentStore<UserSessionSlice>{
    setAuthenticated(arg0: boolean) {
      throw new Error('Method not implemented.');
    }
    constructor(){super(INIT_STATE)}

    //updater
    readonly loginSuccess = this.updater((state, { accountDetails, isAuthenticated }: UserSessionSlice) => ({
        ...state,
        accountDetails,
        isAuthenticated
    }));

    readonly updateUserDetails = this.updater((state, accountDetails: AccountDetails) => ({
        ...state,
        accountDetails
    }));

    readonly isLoggedIn$ = this.select(state => state.isAuthenticated);

}
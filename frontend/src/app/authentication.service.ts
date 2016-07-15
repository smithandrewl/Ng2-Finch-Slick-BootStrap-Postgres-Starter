import { Injectable } from '@angular/core';
import {JwtHelper} from "angular2-jwt";

@Injectable()
export class AuthenticationService {

  private getJwt(): any {
    var jwtHelper = new JwtHelper();
    var encodedToken = window.localStorage.getItem('jwt');
    var jwt = jwtHelper.decodeToken(encodedToken);

    return jwt;
  }

  // login

  logout() {
    window.localStorage.removeItem('jwt');
  }

  isAdmin() {
    if(this.isLoggedIn()) {
      var token = this.getJwt();

      return token.isAdmin;
    } else {
      return false;
    }
  }

  isLoggedIn(): boolean {
    return !(window.localStorage.getItem('jwt') === null);
  }

  constructor() {}

}

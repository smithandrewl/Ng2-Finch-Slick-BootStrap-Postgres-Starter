import { Injectable } from '@angular/core';
import {Http, Headers, Response} from "@angular/http";
import {Observable} from "rxjs/Rx";

@Injectable()
export class DataService {

  constructor(public http: Http) {}

  getEvents(): Observable<Response> {
    return this.http.get("/api/events", {headers: this.getHeaders()});
  }

  private getHeaders() {
      var headers = new Headers();
      headers.append('Authorization', window.localStorage.getItem('jwt'));
      return headers;
  }

  getUsers(): Observable<Response> {
    return this.http.get("/api/users", {headers: this.getHeaders()});
    }

    clearEventLogs() {
      return this.http.get("/api/cleareventlog", {headers: this.getHeaders()});
  }

  deleteUser(id: number){
    return this.http.get("/api/deleteuser/" + id, {headers: this.getHeaders()});
  }

  createUser(username: String, password: String, isAdmin: boolean) {
    return this.http.get("/api/createuser/" + username + "/" + password + "/" + isAdmin, {headers: this.getHeaders()});
  }
}
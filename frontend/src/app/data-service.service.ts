import { Injectable } from '@angular/core';
import {Http, Headers, Response} from "@angular/http";
import {Observable} from "rxjs/Rx";

@Injectable()
export class DataService {

  private baseUrl: string;

  constructor(public http: Http) {
    this.baseUrl = "http://" + window.location.hostname + ":8080/";
  }

  private getUrl(endpoint: string): string {
    return this.baseUrl + endpoint;
  }

  getEvents(): Observable<Response> {
    return this.http.get(this.getUrl("events"), {headers: this.getHeaders()});
  }

  private getHeaders() {
      var headers = new Headers();
      headers.append('Authorization', window.localStorage.getItem('jwt'));
      return headers;
  }

  getUsers(): Observable<Response> {
    return this.http.get(this.getUrl("users"), {headers: this.getHeaders()});
    }

    clearEventLogs() {
      return this.http.get(this.getUrl("cleareventlog"), {headers: this.getHeaders()});
  }

  deleteUser(id: number){
    return this.http.get(this.getUrl("deleteuser/" + id), {headers: this.getHeaders()});
  }

  createUser(username: String, password: String, isAdmin: boolean) {
    return this.http.get(this.getUrl("createuser/" + username + "/" + password + "/" + isAdmin), {headers: this.getHeaders()});
  }

  login(username: String, password: String): Observable<Response> {
    return this.http.get(this.getUrl("authenticate/" + username + "/" + password));
  }
}
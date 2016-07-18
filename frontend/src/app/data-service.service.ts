import { Injectable } from '@angular/core';
import {Http, Headers, Response} from "@angular/http";
import {Observable} from "rxjs/Rx";

@Injectable()
export class DataServiceService {

  constructor(public http: Http) {}

  getEvents(): Observable<Response> {
    var headers = new Headers();
    headers.append('Authorization', window.localStorage.getItem('jwt'));
    return this.http.get("http://localhost/api/events", {headers: headers});
  }

  getUsers(): Observable<Response>{
    var headers = new Headers();
    headers.append('Authorization', window.localStorage.getItem('jwt'));
    return this.http.get("http://localhost/api/users", {headers: headers});
  }

}

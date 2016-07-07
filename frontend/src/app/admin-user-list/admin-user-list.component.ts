import { Component, OnInit } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';

@Component({
  moduleId: module.id,
  selector: 'app-admin-user-list',
  templateUrl: 'admin-user-list.component.html',
  styleUrls: ['admin-user-list.component.css']
})
export class AdminUserListComponent implements OnInit {

  private userData: any;
  constructor(public http: Http) {
    this.userData = [];
  }

  ngOnInit() {
    var headers = new Headers();
    headers.append('Authorization', window.localStorage.getItem('jwt'));
    this.http.get("http://localhost:8080/users", {headers: headers}).subscribe(this.users);
  }

  private users = (response: Response) => {
    this.userData = response.json();
  };
}

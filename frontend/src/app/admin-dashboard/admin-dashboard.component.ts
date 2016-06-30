import { Component, OnInit } from '@angular/core';
import { Http, Response } from '@angular/http';

@Component({
  moduleId: module.id,
  selector: 'app-admin-dashboard',
  templateUrl: 'admin-dashboard.component.html',
  styleUrls: ['admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {

  users = [];
  
  constructor(public http:Http) {}

  ngOnInit() {
    this.http.get("http://localhost:8080/users/").subscribe(
        this.loadUsers
    );
  }

  private loadUsers = (response: Response) => {
    this.users = response.json();
  }

}

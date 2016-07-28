import { Component, OnInit } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import {DataService} from "../data-service.service";
import {RoutingService} from "../routing.service";

@Component({
  moduleId: module.id,
  selector: 'app-admin-user-list',
  templateUrl: 'admin-user-list.component.html',
  styleUrls: ['admin-user-list.component.css'],
  providers: [DataService]
})
export class AdminUserListComponent implements OnInit {

  private userData: any;

  constructor(private dataService: DataService, private routingService:RoutingService) {
    this.userData = [];
  }

  ngOnInit() {
    this.dataService.getUsers().subscribe(this.users);
  }

  private users = (response: Response) => {
    this.userData = response.json();
  };

  private onDelete = (resp: Response) => {
    this.userData = [];
    this.dataService.getUsers().subscribe(this.users);
  }

  private delete(userId: number)  {
    this.dataService.deleteUser(userId).subscribe(this.onDelete);
  }

  createUser() {
    this.routingService.changeRoute('/create-user');
  }



}

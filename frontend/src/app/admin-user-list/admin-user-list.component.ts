import { Component, OnInit } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import {DataServiceService} from "../data-service.service";

@Component({
  moduleId: module.id,
  selector: 'app-admin-user-list',
  templateUrl: 'admin-user-list.component.html',
  styleUrls: ['admin-user-list.component.css'],
  providers: [DataServiceService]
})
export class AdminUserListComponent implements OnInit {

  private userData: any;

  constructor(private dataService: DataServiceService) {
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


}

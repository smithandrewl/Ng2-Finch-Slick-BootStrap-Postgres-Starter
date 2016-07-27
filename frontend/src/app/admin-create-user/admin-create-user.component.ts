import { Component, OnInit } from '@angular/core';
import {DataServiceService} from "../data-service.service";
import {RoutingService} from "../routing.service";
import {Response} from "@angular/http";
import {Observable} from "rxjs/Rx";


@Component({
  moduleId: module.id,
  selector: 'app-admin-create-user',
  templateUrl: 'admin-create-user.component.html',
  styleUrls: ['admin-create-user.component.css'],
  providers: [DataServiceService]
})
export class AdminCreateUserComponent implements OnInit {

  constructor(private dataService: DataServiceService, private routingService: RoutingService) {

  }

  private username: string;
  private password: string;
  private isAdmin:  boolean;

  ngOnInit() {
    this.isAdmin = false;
  }

  add() {
    var form: HTMLFormElement = <HTMLFormElement>document.getElementById("form-add-user");
    if(form.checkValidity()) {
      this.dataService.createUser(this.username, this.password, this.isAdmin).subscribe(
          this.userCreated,
          this.userAlreadyExists
      );
    }

  }

  userAlreadyExists = () => {
    alert("Username is already taken");

    this.username = "";
    this.password = "";
    this.isAdmin = false;
  }

  userCreated = (resp: Response) => {


    if(resp.status == 500) {

    } else {

      this.routingService.changeRoute('/admin');
    }
  }

  clicked() {
    this.routingService.changeRoute('/admin');
  }
}
import { Component, OnInit } from '@angular/core';
import {RoutingService} from "../routing.service";
import {LoginCheck} from "../login-check.directive";
import {AuthenticationService} from "../authentication.service";

@Component({
  moduleId: module.id,
  selector: 'app-home',
  templateUrl: 'home.component.html',
  styleUrls: ['home.component.css'],
  directives: [LoginCheck],
  providers: [AuthenticationService]

})
export class HomeComponent implements OnInit {

  constructor(private routingService: RoutingService) {}

  ngOnInit() {
  }
  clicked() {
    window.localStorage.removeItem('jwt');
    alert(window.localStorage.getItem('jwt'));
    this.routingService.changeRoute('');
  }

}

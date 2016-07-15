import { Component, OnInit } from '@angular/core';
import {RoutingService} from "../routing.service";

@Component({
  moduleId: module.id,
  selector: 'app-home',
  templateUrl: 'home.component.html',
  styleUrls: ['home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(private routingService: RoutingService) {}

  ngOnInit() {
  }
  clicked() {
    window.localStorage.removeItem('jwt');
    this.routingService.changeRoute('');
  }

}

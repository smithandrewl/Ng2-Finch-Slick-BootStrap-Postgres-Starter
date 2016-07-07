import { Component, OnInit } from '@angular/core';
import {RoutingService} from '../routing.service';

@Component({
  moduleId: module.id,
  selector: 'app-admin-dashboard',
  templateUrl: 'admin-dashboard.component.html',
  styleUrls: ['admin-dashboard.component.css'],
})
export class AdminDashboardComponent implements OnInit {

  constructor(private routingService: RoutingService) {

  }

  ngOnInit() {
  }

  clicked() {
    this.routingService.changeRoute('');
  }

}

import {Component, OnInit} from '@angular/core';
import {RoutingService} from '../routing.service';
import {AdminUserListComponent} from '../admin-user-list';
import {EventListComponent} from "../event-list/event-list.component";

@Component({
    moduleId: module.id,
    selector: 'app-admin-dashboard',
    templateUrl: 'admin-dashboard.component.html',
    styleUrls: ['admin-dashboard.component.css'],
    directives: [AdminUserListComponent, EventListComponent]
})
export class AdminDashboardComponent implements OnInit {

    constructor(private routingService:RoutingService) {

    }

    ngOnInit() {
    }

    clicked() {
        window.localStorage.removeItem('jwt');
        this.routingService.changeRoute('');
    }

}

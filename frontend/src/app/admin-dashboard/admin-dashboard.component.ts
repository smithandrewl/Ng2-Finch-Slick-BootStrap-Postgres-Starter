import {Component, OnInit} from '@angular/core';
import {RoutingService} from '../routing.service';
import {AdminUserListComponent} from '../admin-user-list';
import {EventListComponent} from "../event-list/event-list.component";
import {LoginCheck} from "../login-check.directive";
import {AdminCheck} from "../admin-check.directive";
import {AuthenticationService} from "../authentication.service";
import {DataService} from "../data-service.service";
import {SiteHeadingComponent} from "../site-heading/site-heading.component";

@Component({
    moduleId: module.id,
    selector: 'app-admin-dashboard',
    templateUrl: 'admin-dashboard.component.html',
    styleUrls: ['admin-dashboard.component.css'],
    directives: [AdminUserListComponent, EventListComponent, LoginCheck, AdminCheck, SiteHeadingComponent],
    providers: [AuthenticationService, DataService]
})
export class AdminDashboardComponent implements OnInit {

    constructor(private dataService: DataService, private routingService: RoutingService) {

    }

    ngOnInit() {
    }

    clicked() {
        window.localStorage.removeItem('jwt');
        this.routingService.changeRoute('');
    }

}

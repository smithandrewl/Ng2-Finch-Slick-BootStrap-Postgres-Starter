import {Component, OnInit} from '@angular/core';
import {RoutingService} from '../routing.service';
import {AdminUserListComponent} from '../admin-user-list';

@Component({
    moduleId: module.id,
    selector: 'app-admin-dashboard',
    templateUrl: 'admin-dashboard.component.html',
    styleUrls: ['admin-dashboard.component.css'],
    directives: [AdminUserListComponent]
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

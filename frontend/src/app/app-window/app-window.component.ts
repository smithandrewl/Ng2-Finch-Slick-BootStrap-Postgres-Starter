import { Component, OnInit } from '@angular/core';
import { ROUTER_DIRECTIVES, Router } from '@angular/router';
import { RoutingService} from '../routing.service';

@Component({
  moduleId: module.id,
  selector: 'app-app-window',
  templateUrl: 'app-window.component.html',
  styleUrls: ['app-window.component.css'],
  directives: [ROUTER_DIRECTIVES],
  providers: [RoutingService]
})
export class AppWindowComponent implements OnInit {

  constructor(private _router:Router, private routingService: RoutingService) {
    routingService.routeChanged$.subscribe(
        route => {
            this._router.navigate([route]);
        }
    );
  }

  ngOnInit() {
    if(window.localStorage.getItem('jwt')) {
      this._router.navigate(['/admin']);
    }
  }

}

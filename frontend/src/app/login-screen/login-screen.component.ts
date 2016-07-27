import { ViewChild, Component, OnInit } from '@angular/core';
import {LoginAppComponent} from '../login.component';
import {RoutingService} from '../routing.service';
import {SiteHeadingComponent} from "../site-heading/site-heading.component";

@Component({
  moduleId: module.id,
  selector: 'app-login-screen',
  templateUrl: 'login-screen.component.html',
  styleUrls: ['login-screen.component.css'],
  directives: [LoginAppComponent, SiteHeadingComponent]
})
export class LoginScreenComponent implements OnInit {

  constructor(private routingService: RoutingService) {}

  ngOnInit() {
    
  }

  @ViewChild('frontend-app')
  login: LoginAppComponent;

}

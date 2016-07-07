import { ViewChild, Component, OnInit } from '@angular/core';
import {LoginAppComponent} from '../login.component';

@Component({
  moduleId: module.id,
  selector: 'app-login-screen',
  templateUrl: 'login-screen.component.html',
  styleUrls: ['login-screen.component.css'],
  directives: [LoginAppComponent]
})
export class LoginScreenComponent implements OnInit {

  constructor() {}

  ngOnInit() {
    
  }

  @ViewChild('frontend-app')
  login: LoginAppComponent;

}

import { Component } from '@angular/core';
import { Http, Response } from '@angular/http';
import 'rxjs/add/operator/map';
import {RoutingService} from './routing.service';
import {JwtHelper} from 'angular2-jwt/';

@Component({
  moduleId: module.id,
  selector: 'frontend-app',
  templateUrl: 'login.component.html',
  styleUrls: ['login.component.css']
})
export class LoginAppComponent {
  username = '';
  password = '';
  response = '';
  wasError = 'hidden';
  isHidden = '';
  constructor(private routingService: RoutingService, public http: Http) {}

  onClickSubmit() {
    this.http.get("http://localhost:8080/authenticate/" + this.username + "/" + this.password).subscribe(this.auth);
  };

  private auth = (resp:Response) => {
    this.response = resp.text();

    if(this.response != "No such user or incorrect password") {
      this.wasError = 'hidden';
      this.isHidden='hidden';
      window.localStorage.setItem('jwt',this.response);

      var jwtHelper = new JwtHelper();

      var token = jwtHelper.decodeToken(window.localStorage.getItem('jwt'));

      if(token.isAdmin) {

        this.routingService.changeRoute('/admin');
      } else {
        this.routingService.changeRoute('/home');
      }

    } else {
      this.wasError = '';
    }
  };
}

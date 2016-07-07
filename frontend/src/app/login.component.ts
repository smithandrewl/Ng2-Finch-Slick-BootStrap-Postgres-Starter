import { Component } from '@angular/core';
import { Http, Response } from '@angular/http';
import 'rxjs/add/operator/map';
import {RoutingService} from './routing.service';

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
      this.routingService.changeRoute('');
    } else {
      this.wasError = '';
    }
  };
}

import { Component } from '@angular/core';
import { Http, Response } from '@angular/http';
import 'rxjs/add/operator/map';

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

  constructor(public http: Http) {}

  onClickSubmit() {
   this.http.get("http://localhost:8080/authenticate/" + this.username + "/" + this.password).subscribe(this.auth);
  };

  private auth(resp:Response) {
    console.log("Auth response: " + resp.text());
    this.response = resp.text();
  }

}

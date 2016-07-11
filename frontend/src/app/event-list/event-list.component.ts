import { Component, OnInit } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';

@Component({
  moduleId: module.id,
  selector: 'app-event-list',
  templateUrl: 'event-list.component.html',
  styleUrls: ['event-list.component.css']
})
export class EventListComponent implements OnInit {

  private eventData: any;
  constructor(public http: Http) {
    this.eventData = [];
  }

  ngOnInit() {
    var headers = new Headers();
    headers.append('Authorization', window.localStorage.getItem('jwt'));
    this.http.get("http://localhost:8080/events", {headers: headers}).subscribe(this.events);
  }

  private events = (response: Response) => {
    this.eventData = response.json();
  };
}

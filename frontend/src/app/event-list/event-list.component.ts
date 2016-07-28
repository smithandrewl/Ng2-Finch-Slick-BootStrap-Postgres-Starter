import { Component, OnInit } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import {DataService} from "../data-service.service";

@Component({
  moduleId: module.id,
  selector: 'app-event-list',
  templateUrl: 'event-list.component.html',
  styleUrls: ['event-list.component.css'],
  providers: [DataService]
})
export class EventListComponent implements OnInit {

  private eventData: any;
  constructor(private dataService: DataService) {
    this.eventData = [];
  }

  ngOnInit() {
    this.dataService.getEvents().subscribe(this.events);
  }

  private events = (response: Response) => {
    this.eventData = response.json();
  };

  clear() {
    this.dataService.clearEventLogs().subscribe(this.cleared);
  }

  private cleared = (response: Response) => {
    this.dataService.getEvents().subscribe(this.events);
  }
}

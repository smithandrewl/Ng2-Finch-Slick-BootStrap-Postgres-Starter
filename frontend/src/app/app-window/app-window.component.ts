import { Component, OnInit } from '@angular/core';
import { ROUTER_DIRECTIVES } from '@angular/router';

@Component({
  moduleId: module.id,
  selector: 'app-app-window',
  templateUrl: 'app-window.component.html',
  styleUrls: ['app-window.component.css'],
  directives: [ROUTER_DIRECTIVES]
})
export class AppWindowComponent implements OnInit {

  constructor() {}

  ngOnInit() {
  }

}

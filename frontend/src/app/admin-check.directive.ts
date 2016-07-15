import { Directive } from '@angular/core';
import {RoutingService} from "./routing.service";
import {AuthenticationService} from "./authentication.service";

@Directive({
  selector: '[admin-check]',
  providers: [AuthenticationService]
})
export class AdminCheck {

  constructor(private authService: AuthenticationService, private routingService: RoutingService) {
    if(!authService.isAdmin()) {
      routingService.changeRoute('');
    }
  }
}

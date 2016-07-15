import {Directive, OnDestroy} from '@angular/core';
import {AuthenticationService} from "./authentication.service";
import {RoutingService} from "./routing.service";

@Directive({
  selector: '[login-check]',
})
export class LoginCheck implements OnDestroy {

  constructor(private authService: AuthenticationService, private routingService: RoutingService) {
    if(!authService.isLoggedIn()) {
      routingService.changeRoute('');
    }
  }

  ngOnDestroy() {
  }

}

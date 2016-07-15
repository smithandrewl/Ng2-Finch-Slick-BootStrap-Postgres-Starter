import { Injectable } from '@angular/core';
import { Subject }    from 'rxjs/Subject';

@Injectable()
export class RoutingService {

  // Observable string sources
  private currentRoute = new Subject<string>();

  // Observable string streams
  routeChanged$ = this.currentRoute.asObservable();

  // Service message commands
  changeRoute(route: string) {
    this.currentRoute.next(route);
  }
}

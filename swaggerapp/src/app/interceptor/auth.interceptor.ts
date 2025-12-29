import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserService } from '../service/user.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private userService: UserService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (request.url.includes(`${this.userService.apiUrl}/user/login`)) {
      return next.handle(request);
    }
    const requestClone = request.clone({ setHeaders: { Authorization: `Bearer ${this.userService.getTokenFromCache()}`} })
    return next.handle(requestClone);
  }
}

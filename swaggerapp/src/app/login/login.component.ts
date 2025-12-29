import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SubSink } from 'subsink';
import { User } from '../model/user';
import { UserService } from '../service/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  showLoading: boolean;
  private subscriptions = new SubSink();

  constructor(private router: Router, private userService: UserService) { }

  ngOnInit(): void {
    if (this.userService.isUserLoggedIn()) {
      this.router.navigateByUrl('/user/home');
    } else {
      this.router.navigateByUrl('/login');
    }
  }

  onLogin(user: User): void {
    this.showLoading = true;
    this.subscriptions.add(
    this.userService.login(user).subscribe(
      (response) => {
        this.userService.addTokenToCache(response.headers.get('Jwt-Token'));
        this.userService.addUserToCache(response.body);
        this.router.navigateByUrl('/user/home');
        this.showLoading = false;
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
        this.showLoading = false;
      }
    ));
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

}

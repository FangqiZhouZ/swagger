import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { JwtHelperService } from "@auth0/angular-jwt";
import { environment } from 'src/environments/environment';
import { User } from '../model/user';

@Injectable({providedIn: 'root'})
export class UserService {
  public apiUrl = environment.apiUrl;
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient) {}

  login(user: User): Observable<HttpResponse<User>> {
    return this.http.post<User>(`${this.apiUrl}/user/login`, user, { observe: 'response'});
  }

  updateUser(formData: FormData): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/user/update`, formData);
  }

  deleteUser(username: string): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/user/delete/${username}`);
  }

  addUserToCache(user: User): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  getUserFromCache(): User {
    return JSON.parse(localStorage.getItem('user'));
  }

  addTokenToCache(token: string): void {
    localStorage.setItem('token', token);
  }

  getTokenFromCache(): string {
    return localStorage.getItem('token');
  }

  logOut(): void {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  }

  getTokenExpirationDate(): Date | null {
    return this.jwtHelper.getTokenExpirationDate(this.getTokenFromCache());
  }

  isUserLoggedIn(): boolean {
    if (this.getTokenFromCache() && this.getUserFromCache() && 
        this.jwtHelper.decodeToken(this.getTokenFromCache()).sub && 
        !this.jwtHelper.isTokenExpired(this.getTokenFromCache())) {
          return true;
    } else {
      this.logOut();
      return false;
    }
  }

  createUserFormData(currentUsername: string, user: User): FormData {
    const formData = new FormData();
    formData.append('currentUsername', currentUsername);
    formData.append('firstName', user.firstName);
    formData.append('lastName', user.lastName);
    formData.append('username', user.username);
    formData.append('email', user.email);
    formData.append('role', user.role);
    formData.append('isActive', JSON.stringify(user.active));
    formData.append('isNonLocked', JSON.stringify(user.notLocked));
    return formData;
  }
}

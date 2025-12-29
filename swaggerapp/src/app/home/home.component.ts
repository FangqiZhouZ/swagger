import { HttpErrorResponse } from '@angular/common/http';
import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import * as L from 'leaflet';
import { SubSink } from 'subsink';
import { User } from '../model/user';
import { UserService } from '../service/user.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, AfterViewInit, OnDestroy {
  private subscriptions = new SubSink();
  user: User;
  token: string;
  tokenExpirationDate: Date;
  buttonText = 'Show Token';
  showLoading: boolean;
  smallIcon = new L.Icon({
    iconUrl: 'https://www.flaticon.com/svg/static/icons/svg/3603/3603850.svg',
    iconSize:    [32, 41],
    iconAnchor:  [12, 41],
    popupAnchor: [1, -34],
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    shadowSize:  [41, 41]
  });

  constructor(private router: Router, private userService: UserService) { }

  ngOnInit(): void {
    this.user = this.userService.getUserFromCache();
    this.token = this.userService.getTokenFromCache();
    this.tokenExpirationDate =  this.userService.getTokenExpirationDate();
  }

  ngAfterViewInit(): void {
    this.getCurrentLocation();
  }

  onUpdateUser(): void {
    this.showLoading = true;
    const currentUsername = this.userService.getUserFromCache().username;
    const formData = this.userService.createUserFormData(currentUsername, this.user);
    this.subscriptions.add(
      this.userService.updateUser(formData).subscribe(
        (user: User) => {
          this.showLoading = false;
          this.userService.addUserToCache(user);
        },
        (error: HttpErrorResponse) => {
          alert(error.message);
          this.showLoading = false;
        }
      )
    );
  }

  onDeleteUser(username: string): void {
    this.subscriptions.add(
      this.userService.deleteUser(username).subscribe(
        (response) => {
          this.logOut();
        },
        (error: HttpErrorResponse) => {
          alert(error.message);
        }
      )
    );
  }

  logOut(): void {
    this.userService.logOut();
    this.router.navigateByUrl('/login');
  }

  setButtonText(): void {
    if (this.buttonText === 'Show Token') {
      this.buttonText = 'Hide Token';
    } else {
      this.buttonText = 'Show Token';
    }
  }

  copyTokenToClipboard(): void {
    // method 1
    const range = document.createRange();
    range.selectNode(document.getElementById('token-container'));
    window.getSelection().removeAllRanges();
    window.getSelection().addRange(range);
    document.execCommand('copy');
    document.getElementById('clipboard-modal').click();
    // window.getSelection().removeAllRanges();

    // method 2
    // const textarea = document.createElement('textarea');
    // const tokenDiv = document.getElementById('token-container');
    // textarea.value = tokenDiv.textContent;
    // document.body.appendChild(textarea);
    // textarea.select();
    // document.execCommand('copy');
    // document.body.removeChild(textarea);
  }

  private getCurrentLocation(): void {
    navigator.geolocation.getCurrentPosition((position) => {
      const map = L.map('map', {
        center: [position.coords.latitude, position.coords.longitude],
        zoom: 8
      });
      const mainLayer = L.tileLayer('https://api.maptiler.com/maps/outdoor/{z}/{x}/{y}.png?key=WMiJY3C1dO1Vn1Eot8nF', {
        tileSize: 512,
        zoomOffset: -1,
        minZoom: 5,
        maxZoom: 30,
        crossOrigin: true,
        attribution: '\u003ca href=\"https://www.maptiler.com/copyright/\" target=\"_blank\"\u003e\u0026copy; MapTiler\u003c/a\u003e \u003ca href=\"https://www.openstreetmap.org/copyright\" target=\"_blank\"\u003e\u0026copy; OpenStreetMap contributors\u003c/a\u003e'
      });
      mainLayer.addTo(map);
      const marker = L.marker([position.coords.latitude, position.coords.longitude], { icon: this.smallIcon });
      marker.addTo(map).bindPopup('Session location').openPopup();
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

}

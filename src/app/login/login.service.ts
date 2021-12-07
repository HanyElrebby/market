import {Injectable} from "@angular/core";
import axios from 'axios';

const apiUrl = 'http://localhost:8080/api'
const config = {
  headers: {
    'content-type': 'application/json',
    'Accept': 'application/json'
  }
}
@Injectable({
  providedIn: 'root'
})
export class LoginService {
  public authenticationError = false;

  public email: any;
  public password: any;
  public rememberMe: any;
  constructor() { }
  public doLogin(): void {
    const data = {email: this.email, password: this.password, rememberMe: this.rememberMe};
    axios.post(apiUrl + '/authenticate', data, config).then(result => {
        const bearerToken = result.headers.authorization;
        if (bearerToken && bearerToken.slice(0, 7) === 'Bearer ') {
          const jwt = bearerToken.slice(7, bearerToken.length)
          if (this.rememberMe) {
            localStorage.setItem('jhi-authenticationToken', jwt);
            sessionStorage.removeItem('jhi-authenticationToken');
          } else {
            sessionStorage.setItem('jhi-authenticationToken', jwt);
            localStorage.removeItem('jhi-authenticationToken');
          }
        this.authenticationError = false;
        }
        }).catch();
  }

}

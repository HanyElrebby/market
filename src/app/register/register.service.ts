import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import axios from "axios";
const apiUrl = 'http://localhost:8080';
let config = {
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
  }
};
@Injectable({
  providedIn: 'root'
})
export class RegisterService {
  public processRegistration(account: any): Promise<any> {
    return axios.post(apiUrl + '/api/register', account, config);
  }
}

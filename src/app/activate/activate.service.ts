import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import axios, { AxiosInstance } from 'axios';

@Injectable({
  providedIn: 'root'
})
export class ActivateService {
  private axios: AxiosInstance;

  constructor(private http: HttpClient) {
    this.axios = axios;
  }
  public activateAccount(key: string): Promise<any> {
    return this.axios.get(`api/activate?key=${key}`);
  }
}

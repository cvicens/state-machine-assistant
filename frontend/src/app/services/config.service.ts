import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Config } from '../model/config.model';

const CONFIG_URL = '/server.json';

const CONFIG = {
  API_ENDPOINT : '',
  SSO_ENABLED: false
};

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  // tslint:disable-next-line:variable-name
  private _config: BehaviorSubject<Config> = new BehaviorSubject(null);
  public readonly config: Observable<Config> = this._config.asObservable();

  constructor(private http: HttpClient) {
    console.log('ConfigService constructror');
    this.http.get<Config>(CONFIG_URL)
      .subscribe(
        (config: Config) => {
          this._config.next(config);
        },
        error => {
          console.error(error);
          this._config.next(CONFIG);
        }
      );
  }
}

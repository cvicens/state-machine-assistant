import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Patient } from '../model/patient.model';
import { ConfigService } from './config.service';
import { Config } from '../model/config.model';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PatientsService {
  // tslint:disable-next-line:variable-name
  // private _ready: BehaviorSubject<boolean> = new BehaviorSubject(false);
  // public readonly ready: Observable<boolean> = this._ready.asObservable();

  // tslint:disable-next-line:variable-name
  private _patients: BehaviorSubject<Patient[]> = new BehaviorSubject([]);
  public readonly patients: Observable<Patient[]> = this._patients.asObservable();

  config: Config;
  baseUrl: string;

  constructor(private http: HttpClient, private configService: ConfigService) {
    console.log('PatientsService constructror');
    this.configService.config.subscribe(config => {
      console.log('config', config);
      if (config) {
        this.config = config;
        if (this.init()) {
          this.getPatients();
        }
      }
    });
  }

  init() {
    this.baseUrl = this.config.API_ENDPOINT;
    if (!this.baseUrl) {
      return false;
    }

    return true;
  }

  getPatients() {
    if (this.baseUrl) {
      // return this.http.get<Patient[]>(this.baseUrl + '/api/patients');
      this.http.get<Patient[]>(this.baseUrl + '/api/patients')
      .subscribe(
        (patients: Patient[]) => {
          this._patients.next(patients);
        },
        error => {
          console.error('getPatients ERROR!', JSON.stringify(error));
        }
      );
    } else {
      console.error('PatientsService not ready!');
    }
  }
}

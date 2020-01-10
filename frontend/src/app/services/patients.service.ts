import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Patient } from '../model/patient.model';
import { ConfigService } from './config.service';
import { Config } from '../model/config.model';
import { BehaviorSubject, Observable } from 'rxjs';
import { MatSnackBar } from '@angular/material';
import { GenericService } from './generic.service';

@Injectable({
  providedIn: 'root'
})
export class PatientsService extends GenericService {
  // tslint:disable-next-line:variable-name
  // private _ready: BehaviorSubject<boolean> = new BehaviorSubject(false);
  // public readonly ready: Observable<boolean> = this._ready.asObservable();

  // tslint:disable-next-line:variable-name
  private _patients: BehaviorSubject<Patient[]> = new BehaviorSubject([]);
  public readonly patients: Observable<Patient[]> = this._patients.asObservable();

  config: Config;
  baseUrl: string;

  constructor(private http: HttpClient, private configService: ConfigService, protected snackBar: MatSnackBar) {
    super(snackBar);
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
    if (this.baseUrl == null) {
      return false;
    }

    return true;
  }

  getPatients() {
    if (this.baseUrl != null) {
      // return this.http.get<Patient[]>(this.baseUrl + '/api/patients');
      this.http.get<Patient[]>(this.baseUrl + '/api/patients')
      .subscribe(
        (patients: Patient[]) => {
          this._patients.next(patients);
          this.openSnackBar(`Retrieved #${patients.length} patients`);
        },
        error => {
          this.openSnackBar('Error while moving to retrieveing patients');
          console.error('Error while moving to retrieveing patients', JSON.stringify(error));
        }
      );
    } else {
      this.openSnackBar('Patients Service not ready, try again please');
      console.error('PatientsService not ready!');
    }
  }

  updatePatientStage(patientId: number, patient: Patient) {
    if (this.baseUrl != null) {
      if (!patient) {
        console.error('patient is null at updatePatientStage()');
        return;
      }
      console.log(`patient: ${JSON.stringify(patient)}`);
      this.http.put<Patient>(`${this.baseUrl}/api/patients/${patientId}`, patient)
      .subscribe(
        (result: Patient) => {
          this.openSnackBar(`Patient ${result.patientId} moved to stage ${result.stage}`);
        },
        error => {
          this.openSnackBar(`Error while moving to stage ${patient.stage}`);
          console.error(`Error while moving to stage ${patient.stage}`, JSON.stringify(error));
        }
      );
    } else {
      this.openSnackBar('Patients Service not ready, try again please');
      console.error('PatientsService not ready!');
    }
  }
}

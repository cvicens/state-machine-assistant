import { Component, OnInit } from '@angular/core';
import { Patient } from 'src/app/model/patient.model';
import { PatientsService } from 'src/app/services/patients.service';
import { HttpRuntimeException } from 'src/app/model/http-error.model';

import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-list-patients',
  templateUrl: './list-patients.component.html',
  styleUrls: ['./list-patients.component.scss']
})
export class ListPatientsComponent implements OnInit {
  loading = false;
  refreshStartTime = -1;
  refreshStopTime = -1;
  httpRuntimeException: HttpRuntimeException;

  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;


  patients: Patient[];

  constructor(private patientsService: PatientsService, private formBuilder: FormBuilder) {
    this.patientsService.patients.subscribe(payload => {
      this.loading = false;
      this.refreshStopTime = Date.now();
      console.log('payload', payload);
      if (Array.isArray(payload)) {
        this.patients = payload;
      } else {
        // Show error!
        this.httpRuntimeException = payload as HttpRuntimeException;
        this.patients = [];

        // this.notificationsService.notify(NotificationType.DANGER, 'Error retrieving products', this.httpRuntimeException.error);
      }
    });
  }

  ngOnInit() {
    this.refresh(null);

    this.firstFormGroup = this.formBuilder.group({
      firstCtrl: ['', Validators.required]
    });
    this.secondFormGroup = this.formBuilder.group({
      secondCtrl: ['', Validators.required]
    });
  }

  refresh($event: any) {
    this.loading = true;
    this.refreshStartTime = Date.now();
    this.patientsService.getPatients();
  }
}

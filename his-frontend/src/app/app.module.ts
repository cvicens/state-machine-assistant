import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';

import { AppRoutingModule } from './app-routing.module';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MatButtonModule, MatToolbarModule, MatCheckboxModule, MatListModule, MatIconModule } from '@angular/material';

import { Routes, RouterModule } from '@angular/router';

// Services
import { ConfigService } from './services/config.service';
import { PatientsService } from './services/patients.service';

// Components
import { ListPatientsComponent } from './components/list-patients/list-patients.component';
import { AddPatientComponent } from './components/add-patient/add-patient.component';
import { HttpClientModule } from '@angular/common/http';


const appRoutes: Routes = [
  { path: 'patients', component: ListPatientsComponent },
  { path: '',   redirectTo: '/patients', pathMatch: 'full' },
  // { path: '**', component: PageNotFoundComponent }
];

@NgModule({
  declarations: [
    AppComponent,
    ListPatientsComponent,
    AddPatientComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatButtonModule, MatToolbarModule, MatCheckboxModule, MatListModule, MatIconModule,
    HttpClientModule,
    RouterModule.forRoot(
      appRoutes,
      { enableTracing: true } // <-- debugging purposes only
    ),
  ],
  providers: [PatientsService, ConfigService],
  bootstrap: [AppComponent]
})
export class AppModule { }

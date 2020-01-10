import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material';
import { ComponentType } from '@angular/cdk/portal';

@Injectable({
  providedIn: 'root'
})
export class GenericService {

  constructor(protected snackBar: MatSnackBar) { }

  openSnackBar(message: string, action?: string) {
    this.snackBar.open(message, action, {
      duration: 2000,
    });
  }
}

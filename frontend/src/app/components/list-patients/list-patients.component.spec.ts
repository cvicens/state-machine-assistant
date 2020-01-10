import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ListPatientsComponent } from './list-patients.component';

describe('ListPatientsComponent', () => {
  let component: ListPatientsComponent;
  let fixture: ComponentFixture<ListPatientsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListPatientsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListPatientsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

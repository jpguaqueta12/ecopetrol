import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { VacationRoutingModule } from './vacation-routing.module';
import { VacationComponent } from './pages/vacation/vacation.component';
import { DetailComponent } from './pages/detail/detail.component';


@NgModule({
  declarations: [
    VacationComponent,
    DetailComponent
  ],
  imports: [
    CommonModule,
    VacationRoutingModule
  ]
})
export class VacationModule { }

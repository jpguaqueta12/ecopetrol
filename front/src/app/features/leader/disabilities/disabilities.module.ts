import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DisabilitiesRoutingModule } from './disabilities-routing.module';
import { DisabilitiesComponent } from './pages/disabilities/disabilities.component';
import { DetailComponent } from './pages/detail/detail.component';


@NgModule({
  declarations: [
    DisabilitiesComponent,
    DetailComponent
  ],
  imports: [
    CommonModule,
    DisabilitiesRoutingModule
  ]
})
export class DisabilitiesModule { }

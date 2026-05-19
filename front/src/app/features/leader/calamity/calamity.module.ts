import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CalamityRoutingModule } from './calamity-routing.module';
import { DetailComponent } from './pages/detail/detail.component';
import { CalamityComponent } from './pages/calamity/calamity.component';


@NgModule({
  declarations: [
    DetailComponent,
    CalamityComponent
  ],
  imports: [
    CommonModule,
    CalamityRoutingModule
  ]
})
export class CalamityModule { }

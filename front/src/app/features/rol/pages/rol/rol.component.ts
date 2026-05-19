import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-rol',
  templateUrl: './rol.component.html',
  styleUrls: ['./rol.component.css']
})
export class RolComponent {
  empleadoEnabled = false;
  liderEnabled = false;
  peopleEnabled = false;

  constructor(private router: Router) {
    const userStr = localStorage.getItem('User');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        const role = user.role.toLowerCase();
        if (role === 'empleado') {
          this.empleadoEnabled = true;
        } else if (role === 'lider') {
          this.empleadoEnabled = true;
          this.liderEnabled = true;
        } else if (role === 'people') {
          this.empleadoEnabled = true;
          this.liderEnabled = true;
          this.peopleEnabled = true;
        }
      } catch (e) {
        // Si hay error en el parseo, no habilita nada
        console.error('Error parsing User from localStorage', e);
      }
    }
  }

  selectRole(role: string): void {
    console.log(`Rol seleccionado: ${role}`);
    this.router.navigate(['/' + `${role}`]);
  }
}

import { Component, OnInit } from '@angular/core';
import { PeopleRequestsService } from 'src/app/core/services/people-requests.service';
import { PeopleReportService } from 'src/app/core/services/people-report.service';
/* Eliminado import { saveAs } from 'file-saver'; porque no está disponible y usaremos API nativa */

@Component({
  selector: 'app-people',
  templateUrl: './people.component.html',
  styleUrls: ['./people.component.css']
})
export class PeopleComponent implements OnInit {
  isSidebarClosed: boolean = false;

requests: any[] = [];

  isLoading: boolean = false;

  toggleSidebar(): void {
    this.isSidebarClosed = !this.isSidebarClosed;
  }

  // Quitar los métodos de acciones no requeridas

  constructor(
    private peopleRequestsService: PeopleRequestsService,
    private peopleReportService: PeopleReportService
  ) {}

  ngOnInit(): void {
    this.fetchRequests();
  }

  private fetchRequests(): void {
    this.isLoading = true;
    this.peopleRequestsService.getAllRequests().subscribe({
      next: (data) => {
        this.requests = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al obtener solicitudes', error);
        this.isLoading = false;
      },
    });
  }

  downloadReport(): void {
    this.peopleReportService.downloadPeopleReport('csv').subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'reporte_solicitudes.csv';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Error al descargar el reporte', error);
      },
    });
  }
}

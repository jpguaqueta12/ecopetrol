import { Component, OnInit } from '@angular/core';
import { PeopleRequestsService } from 'src/app/core/services/people-requests.service';
import { PeopleReportService } from 'src/app/core/services/people-report.service';
import { PeopleMonthClosureService } from 'src/app/core/services/people-month-closure.service';
import { SnackbarService } from 'src/app/core/services/snackbar.service';
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

  constructor(
    private peopleRequestsService: PeopleRequestsService,
    private peopleReportService: PeopleReportService,
    private peopleMonthClosureService: PeopleMonthClosureService,
    private snackbar: SnackbarService
  ) {}

  ngOnInit(): void {
    this.fetchRequests();
  }

  toggleSidebar(): void {
    this.isSidebarClosed = !this.isSidebarClosed;
  }

  private fetchRequests(): void {
    this.isLoading = true;
    this.peopleRequestsService.getAllRequests().subscribe({
      next: (data) => {
        this.requests = data;
        this.isLoading = false;
        // Notifica éxito solo si venía una bandera explícita, UX no debe saturar con success para cargas normales de grilla
      },
      error: (error) => {
        console.error('Error al obtener solicitudes', error);
        this.isLoading = false;
        this.snackbar.showError(
          typeof error === 'string'
            ? error
            : 'Hubo un problema al obtener las solicitudes. Intenta nuevamente más tarde.'
        );
      },
    });
  }

  downloadReport(): void {
    this.peopleReportService.downloadPeopleReportCSV().subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'reporte_solicitudes.csv';
        a.click();
        window.URL.revokeObjectURL(url);
        this.snackbar.showSuccess('Reporte descargado correctamente.');
      },
      error: (error) => {
        console.error('Error al descargar el reporte', error);
        this.snackbar.showError(
          typeof error === 'string'
            ? error
            : 'No fue posible descargar el reporte. Por favor intenta de nuevo.'
        );
      },
    });
  }

  closeMonth(): void {
    this.peopleMonthClosureService.closeMonth().subscribe({
      next: () => {
        this.snackbar.showSuccess('Cierre de mes realizado con éxito.');
        this.fetchRequests();
      },
      error: (error) => {
        console.error('Error al realizar cierre de mes', error);
        this.snackbar.showError(
          typeof error === 'string'
            ? error
            : 'Error inesperado al realizar el cierre de mes.'
        );
      }
    });
  }
}

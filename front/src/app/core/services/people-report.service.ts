import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PeopleReportService {

  constructor(private http: HttpClient) {}

  /**
   * Solicita la descarga del reporte global de solicitudes (vacaciones, calamidad, cumpleaños, incapacidad)
   * Retorna el Blob para descargarlo como archivo (CSV, XLSX o PDF según backend)
   */
  downloadPeopleReport(format: string = 'csv'): Observable<Blob> {
    // Suponiendo endpoint /people/report + query param format (csv/xlsx/pdf)
    // Reemplazar con el endpoint real si es distinto.
    const url = `${environment.apiUrl}/people/report?format=${format}`;

    return this.http.get(url, { responseType: 'blob' }).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    const errorMsg = error.error?.message || 'Error de conexión con el backend al descargar el reporte';
    return throwError(() => errorMsg);
  }
}

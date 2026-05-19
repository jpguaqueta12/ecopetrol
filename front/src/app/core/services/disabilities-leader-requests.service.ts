import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError, delay } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { formatDate, mapStatus } from './field-mapper';

@Injectable({
  providedIn: 'root'
})
export class DisabilitiesLeaderRequestsService {
  constructor(private http: HttpClient) { }

  getRequests(): Observable<any[]> {
    if (environment.useMock) {
      return this.http.get<any>(environment.mockUrl.disabilitiesLeader).pipe(delay(600), catchError(this.handleError));
    }
    return this.http.get<any[]>(`${environment.apiUrl}${environment.endpoint.disabilitiesLeader}`).pipe(
      map(items => items.map(i => ({
        id: i.id,
        cedula: i.numeroEmpleado,
        collaborator: i.nombreEmpleado,
        startDate: formatDate(i.fechaInicio),
        endDate: formatDate(i.fechaFin),
        daysRequested: i.totalDias,
        status: mapStatus(i.estado),
        businessUnit: i.unidadNegocio,
        leaderName: i.lider?.nombre || '',
        incapacityType: i.tipoIncapacidad,
        healthEntity: i.entidadSalud,
        category: i.categoria,
        diagnosis: i.diagnostico,
        attachedFileName: i.archivoAdjunto || ''
      }))),
      catchError(this.handleError)
    );
  }

  approve(id: number): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}${environment.endpoint.approveDisability}/${id}?rol=LIDER`, {}).pipe(
      catchError(this.handleError)
    );
  }

  reject(id: number): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}${environment.endpoint.rejectDisability}/${id}?rol=LIDER`, {}).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    const errorMsg = error.error?.message || 'Error de conexión con el servidor backend';
    return throwError(() => errorMsg);
  }
}

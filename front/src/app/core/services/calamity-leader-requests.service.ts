import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError, delay } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { formatDate, mapStatus } from './field-mapper';

@Injectable({
  providedIn: 'root'
})
export class CalamityLeaderRequestsService {
  constructor(private http: HttpClient) { }

  getRequests(): Observable<any[]> {
    if (environment.useMock) {
      return this.http.get<any>(environment.mockUrl.calamityLeader).pipe(delay(600), catchError(this.handleError));
    }
    return this.http.get<any[]>(`${environment.apiUrl}${environment.endpoint.calamityLeader}`).pipe(
      map(items => items.map(c => ({
        id: c.id,
        cedula: c.numeroEmpleado,
        collaborator: c.nombreEmpleado,
        startDate: formatDate(c.fechaInicio),
        endDate: formatDate(c.fechaFin),
        daysRequested: c.totalDias,
        status: mapStatus(c.estado),
        businessUnit: c.unidadNegocio,
        comments: c.comentario,
        leaderName: c.lider?.nombre || '',
        attachedFileName: c.archivoAdjunto || ''
      }))),
      catchError(this.handleError)
    );
  }

  approve(id: number): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}${environment.endpoint.approveCalamity}/${id}?rol=LIDER`, {}).pipe(
      catchError(this.handleError)
    );
  }

  reject(id: number): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}${environment.endpoint.rejectCalamity}/${id}?rol=LIDER`, {}).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    const errorMsg = error.error?.message || 'Error de conexión con el servidor backend';
    return throwError(() => errorMsg);
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError, delay } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { formatDate, mapStatus } from './field-mapper';

@Injectable({
  providedIn: 'root'
})
export class BirthdayLeaderRequestsService {
  constructor(private http: HttpClient) { }

  getRequests(): Observable<any[]> {
    if (environment.useMock) {
      return this.http.get<any>(environment.mockUrl.birthdayLeader).pipe(delay(600), catchError(this.handleError));
    }
    return this.http.get<any[]>(`${environment.apiUrl}${environment.endpoint.birthdayLeader}`).pipe(
      map(items => items.map(d => ({
        id: d.id,
        cedula: d.numeroEmpleado,
        collaborator: d.nombreEmpleado,
        startDate: formatDate(d.fechaCumpleanio),
        endDate: formatDate(d.fechaCumpleanio),
        daysRequested: 1,
        status: mapStatus(d.estado),
        businessUnit: d.unidadNegocio,
        comments: d.comentario,
        leaderName: d.lider?.nombre || ''
      }))),
      catchError(this.handleError)
    );
  }

  approve(id: number): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}${environment.endpoint.approveBirthday}/${id}?rol=LIDER`, {}).pipe(
      catchError(this.handleError)
    );
  }

  reject(id: number): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}${environment.endpoint.rejectBirthday}/${id}?rol=LIDER`, {}).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    const errorMsg = error.error?.message || 'Error de conexión con el servidor backend';
    return throwError(() => errorMsg);
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError, delay } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { formatDate, mapStatus } from './field-mapper';
import { SnackbarService } from './snackbar.service';

@Injectable({
  providedIn: 'root'
})
export class VacationLeaderRequestsService {
  constructor(private http: HttpClient, private snackbar: SnackbarService) { }

  private showError(message: string) {
    this.snackbar.showError(message);
  }

  getRequests(): Observable<any[]> {
    if (environment.useMock) {
      return this.http.get<any>(environment.mockUrl.vacationLeader).pipe(delay(600), catchError(this.handleError));
    }
    return this.http.get<any[]>(`${environment.apiUrl}${environment.endpoint.vacationLeader}`).pipe(
      map(items => items.map(v => ({
        id: v.id,
        cedula: v.numeroEmpleado,
        collaborator: v.nombreEmpleado,
        startDate: formatDate(v.fechaInicio),
        endDate: formatDate(v.fechaFin),
        daysRequested: v.totalDias,
        status: mapStatus(v.estado),
        businessUnit: v.unidadNegocio,
        comments: v.comentario,
        leaderName: v.lider?.nombre || ''
      }))),
      catchError(this.handleError)
    );
  }

  approve(id: number): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}${environment.endpoint.approveVacation}/${id}?rol=LIDER`, {}).pipe(
      map((resp) => resp),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404 && typeof error.error === 'string') {
          this.showError(error.error);
          return throwError(() => error.error);
        }
        if (error.status === 500 && typeof error.error === 'string') {
          this.showError(error.error);
          return throwError(() => error.error);
        }
        const msg = error.error?.message || 'Error de conexión con el servidor backend';
        this.showError(msg);
        return throwError(() => msg);
      })
    );
  }

  reject(id: number): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}${environment.endpoint.rejectVacation}/${id}?rol=LIDER`, {}).pipe(
      map((resp) => resp),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404 && typeof error.error === 'string') {
          this.showError(error.error);
          return throwError(() => error.error);
        }
        if (error.status === 500 && typeof error.error === 'string') {
          this.showError(error.error);
          return throwError(() => error.error);
        }
        const msg = error.error?.message || 'Error de conexión con el servidor backend';
        this.showError(msg);
        return throwError(() => msg);
      })
    );
  }

  private handleError(error: HttpErrorResponse) {
    const errorMsg = error.error?.message || 'Error de conexión con el servidor backend';
    return throwError(() => errorMsg);
  }
}

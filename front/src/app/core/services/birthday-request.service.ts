import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { SnackbarService } from './snackbar.service';

@Injectable({
  providedIn: 'root'
})
export class BirthdayRequestService {

  constructor(private http: HttpClient, private snackbar: SnackbarService) { }

  private showError(message: string) {
    this.snackbar.showError(message);
  }

  createBirthdayRequest(payload: any): Observable<any> {
    const body = {
      numeroEmpleado: payload.employeeNumber,
      nombreEmpleado: payload.employeeName,
      unidadNegocio: payload.businessUnit,
      fechaCumpleanio: payload.startDate || null,
      comentario: payload.comments || null,
      lider: null
    };
    return this.http.post<any>(`${environment.apiUrl}${environment.endpoint.createBirthday}`, body).pipe(
      catchError((error: HttpErrorResponse) => {
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

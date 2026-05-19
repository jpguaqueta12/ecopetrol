import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<any> {
    const body = { usuario: username, contrasena: password };
    return this.http.post<any>(`${environment.apiUrl}${environment.endpoint.login}`, body).pipe(
      map(response => {
        if (response) {
          // Normaliza los campos del backend al formato que espera el front
          const user = {
            ...response,
            role: (response.rol || '').toLowerCase(),
            name: response.nombre,
            username: response.usuario
          };
          localStorage.setItem('User', JSON.stringify(user));
        }
        return response;
      }),
      catchError((error: HttpErrorResponse) => {
        const errorMsg = error.error?.message || 'Credenciales inválidas o error de conexión';
        return throwError(() => errorMsg);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('User');
  }
}

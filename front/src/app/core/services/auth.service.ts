import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

export interface AuthUser {
  id?: number;
  usuario: string;
  rol: string;
  nombre?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly USER_KEY = 'User';
  private readonly SESSION_KEY = 'X-Session-ID';

  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<AuthUser> {
    const body = { usuario: username, contrasena: password };

    return this.http.post<AuthUser>(
      `${environment.apiUrl}${environment.endpoint.login}`,
      body,
      { observe: 'response' }
    ).pipe(
      map((response: HttpResponse<AuthUser>) => {
        const responseBody = response.body;

        if (!responseBody) {
          throw new Error('Respuesta inválida del servidor');
        }

        this.setUser(responseBody);

        const sessionId = response.headers.get(this.SESSION_KEY);
        if (sessionId) {
          localStorage.setItem(this.SESSION_KEY, sessionId);
        }

        return responseBody;
      }),
      catchError((error: HttpErrorResponse) => {
        const errorMsg =
          error.error?.message ||
          error.error?.mensaje ||
          'Credenciales inválidas o error de conexión';
        return throwError(() => errorMsg);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem(this.SESSION_KEY);
  }

  getCurrentUser(): AuthUser | null {
    const user = localStorage.getItem(this.USER_KEY);
    return user ? JSON.parse(user) : null;
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem(this.SESSION_KEY);
  }

  getSessionId(): string | null {
    return localStorage.getItem(this.SESSION_KEY);
  }

  private setUser(user: AuthUser): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }
}

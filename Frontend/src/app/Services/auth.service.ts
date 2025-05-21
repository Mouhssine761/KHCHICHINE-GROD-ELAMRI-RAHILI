import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

interface LoginResponse {
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = '/api/auth';
  private readonly tokenKey = 'jwt';

  constructor(private http: HttpClient) {}


  login(email: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/login`, { username: email, password })
      .pipe(
        tap(res => {
          localStorage.setItem(this.tokenKey, res.token);
        })
      );
  }


  register(email: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, {
      username: email,
      password
    });
  }


  logout(): void {
    localStorage.removeItem(this.tokenKey);
  }


  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }


  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}

// src/app/login/login.component.ts
import { Component } from '@angular/core';
import { AuthService } from '../../Services/auth.service';
import { Router } from '@angular/router';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-login',
  imports:[FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']

})
export class LoginComponent {
  email = '';
  password = '';
  remember = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    this.errorMessage = '';
    this.authService.login(this.email, this.password).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: err => {
        this.errorMessage = err.error || 'Login failed';
      }
    });
  }

  togglePasswordVisibility(fieldId: string) {
    const input = document.getElementById(fieldId) as HTMLInputElement;
    input.type = input.type === 'password' ? 'text' : 'password';
  }
}

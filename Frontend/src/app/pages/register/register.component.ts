// src/app/register/register.component.ts
import { Component } from '@angular/core';
import { AuthService } from '../../Services/auth.service';
import { Router } from '@angular/router';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-register',
  imports:[FormsModule],

  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  name = '';
  email = '';
  password = '';
  confirmPassword = '';
  terms = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    this.errorMessage = '';

    if (this.password !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      return;
    }
    if (!this.terms) {
      this.errorMessage = 'You must agree to the terms';
      return;
    }

    this.authService.register(this.email, this.password)
      .subscribe({
        next: () => {
          this.router.navigate(['/']);
        },
        error: err => {
          this.errorMessage = err.error || 'Registration failed';
        }
      });
  }

  togglePasswordVisibility(fieldId: string) {
    const input = document.getElementById(fieldId) as HTMLInputElement;
    input.type = input.type === 'password' ? 'text' : 'password';
  }
}

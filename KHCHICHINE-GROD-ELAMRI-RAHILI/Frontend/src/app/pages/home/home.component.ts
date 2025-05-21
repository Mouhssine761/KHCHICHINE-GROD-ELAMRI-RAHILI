import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';
import {Router, RouterModule} from '@angular/router';
import {AuthService} from '../../Services/auth.service';
document.addEventListener('DOMContentLoaded', function(): void {
  const navLinks: NodeListOf<HTMLAnchorElement> = document.querySelectorAll('.nav-links a[href^="#"]');

  navLinks.forEach((link: HTMLAnchorElement): void => {
    link.addEventListener('click', function(e: MouseEvent): void {
      e.preventDefault();

      const targetId: string | null = this.getAttribute('href');

      if (!targetId) return;

      const targetSection: HTMLElement | null = document.querySelector(targetId);

      if (targetSection) {
        const header: HTMLElement | null = document.querySelector('header');
        const headerHeight: number = header ? header.offsetHeight : 0;

        const targetPosition: number = targetSection.getBoundingClientRect().top + window.pageYOffset - headerHeight;

        window.scrollTo({
          top: targetPosition,
          behavior: 'smooth'
        });

        // Update active class on navbar links
        const allNavLinks: NodeListOf<HTMLAnchorElement> = document.querySelectorAll('.nav-links a[href^="#"]');
        allNavLinks.forEach((navLink: HTMLAnchorElement): void => navLink.classList.remove('active'));
        this.classList.add('active');

        const mobileMenu: HTMLElement | null = document.querySelector('.nav-links');
        if (mobileMenu && mobileMenu.classList.contains('active')) {
          mobileMenu.classList.remove('active');
        }
      }
    });
  });

  window.addEventListener('scroll', function(): void {
    let current: string = '';
    const sections: NodeListOf<HTMLElement> = document.querySelectorAll('section[id]');
    const header: HTMLElement | null = document.querySelector('header');
    const headerHeight: number = header ? header.offsetHeight : 0;

    sections.forEach((section: HTMLElement): void => {
      const sectionTop: number = section.offsetTop - headerHeight - 100; // Add some offset
      const sectionHeight: number = section.offsetHeight;

      if (window.pageYOffset >= sectionTop && window.pageYOffset < sectionTop + sectionHeight) {
        current = '#' + section.getAttribute('id');
      }
    });

    const navLinks: NodeListOf<HTMLAnchorElement> = document.querySelectorAll('.nav-links a[href^="#"]');
    navLinks.forEach((link: HTMLAnchorElement): void => {
      link.classList.remove('active');
      if (link.getAttribute('href') === current) {
        link.classList.add('active');
      }
    });
  });
});
@Component({
  selector: 'app-home',
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})

export class HomeComponent {
  constructor(
    public auth: AuthService,
    private router: Router
  ) {}

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}

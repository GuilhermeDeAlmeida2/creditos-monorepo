import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService, PingResponse } from './api.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container">
      <div class="card">
        <h1>Sistema de Créditos</h1>
        <p>Teste de integração entre frontend e backend</p>
        
        <button 
          class="btn" 
          (click)="pingApi()" 
          [disabled]="loading">
          {{ loading ? 'Testando...' : 'Ping API' }}
        </button>
        
        <div *ngIf="result" class="result" [class.error]="isError" [class.success]="!isError">
          {{ result }}
        </div>
      </div>
    </div>
  `
})
export class AppComponent {
  loading = false;
  result: string | null = null;
  isError = false;

  constructor(private apiService: ApiService) {}

  pingApi(): void {
    this.loading = true;
    this.result = null;
    this.isError = false;

    this.apiService.ping().subscribe({
      next: (response: PingResponse) => {
        this.result = JSON.stringify(response, null, 2);
        this.isError = false;
        this.loading = false;
      },
      error: (error) => {
        this.result = `Erro: ${error.message || 'Falha na comunicação com a API'}`;
        this.isError = true;
        this.loading = false;
      }
    });
  }
}

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, PingResponse } from './api.service';
import { CreditosComponent } from './creditos.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, CreditosComponent],
  template: `
    <div class="app-container">
      <header class="app-header">
        <div class="header-content">
          <h1>Sistema de Créditos Constituídos</h1>
          <p>Gerenciamento e consulta de créditos por NFS-e</p>
        </div>
        <div class="connection-status">
          <button 
            class="btn btn-small" 
            (click)="pingApi()" 
            [disabled]="loading"
            [class.btn-success]="!isError && result"
            [class.btn-error]="isError">
            {{ loading ? 'Testando...' : (result ? 'Conectado' : 'Testar Conexão') }}
          </button>
        </div>
      </header>

      <main class="app-main">
        <nav class="tab-nav">
          <button 
            class="tab-btn" 
            [class.active]="activeTab === 'creditos'"
            (click)="activeTab = 'creditos'">
            <span class="tab-icon">📋</span>
            Buscar Créditos
          </button>
          <button 
            class="tab-btn" 
            [class.active]="activeTab === 'ping'"
            (click)="activeTab = 'ping'">
            <span class="tab-icon">🔧</span>
            Teste de Conexão
          </button>
        </nav>

        <div class="tab-content">
          <div *ngIf="activeTab === 'creditos'" class="tab-panel">
            <app-creditos></app-creditos>
          </div>
          
          <div *ngIf="activeTab === 'ping'" class="tab-panel">
            <div class="ping-panel">
              <div class="ping-card">
                <h2>Teste de Conectividade</h2>
                <p>Verifique se a API está funcionando corretamente</p>
                
                <button 
                  class="btn btn-primary" 
                  (click)="pingApi()" 
                  [disabled]="loading">
                  {{ loading ? 'Testando...' : 'Ping API' }}
                </button>
                
                <div *ngIf="result" class="result" [class.error]="isError" [class.success]="!isError">
                  <pre>{{ result }}</pre>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    }

    .app-header {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      padding: 20px 0;
      box-shadow: 0 2px 20px rgba(0,0,0,0.1);
    }

    .header-content {
      max-width: 1400px;
      margin: 0 auto;
      padding: 0 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 20px;
    }

    .header-content h1 {
      margin: 0;
      color: #2c3e50;
      font-size: 28px;
      font-weight: 700;
    }

    .header-content p {
      margin: 5px 0 0 0;
      color: #7f8c8d;
      font-size: 16px;
    }

    .connection-status {
      display: flex;
      align-items: center;
    }

    .btn {
      padding: 12px 24px;
      border: none;
      border-radius: 8px;
      font-size: 16px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      text-decoration: none;
      display: inline-block;
      text-align: center;
    }

    .btn:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .btn-small {
      padding: 8px 16px;
      font-size: 14px;
    }

    .btn-primary {
      background: linear-gradient(135deg, #3498db, #2980b9);
      color: white;
    }

    .btn-primary:hover:not(:disabled) {
      background: linear-gradient(135deg, #2980b9, #1f618d);
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
    }

    .btn-success {
      background: #27ae60;
      color: white;
    }

    .btn-error {
      background: #e74c3c;
      color: white;
    }

    .app-main {
      max-width: 1400px;
      margin: 0 auto;
      padding: 30px 20px;
    }

    .tab-nav {
      display: flex;
      background: white;
      border-radius: 12px 12px 0 0;
      box-shadow: 0 -2px 10px rgba(0,0,0,0.1);
      margin-bottom: 0;
    }

    .tab-btn {
      flex: 1;
      padding: 20px;
      background: none;
      border: none;
      font-size: 16px;
      font-weight: 600;
      color: #7f8c8d;
      cursor: pointer;
      transition: all 0.3s ease;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      border-bottom: 3px solid transparent;
    }

    .tab-btn:hover {
      background: #f8f9fa;
      color: #2c3e50;
    }

    .tab-btn.active {
      color: #3498db;
      border-bottom-color: #3498db;
      background: #f8f9fa;
    }

    .tab-icon {
      font-size: 18px;
    }

    .tab-content {
      background: white;
      border-radius: 0 0 12px 12px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      min-height: 400px;
    }

    .tab-panel {
      padding: 0;
    }

    .ping-panel {
      padding: 40px;
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 400px;
    }

    .ping-card {
      text-align: center;
      max-width: 500px;
      padding: 40px;
      background: #f8f9fa;
      border-radius: 12px;
      box-shadow: 0 4px 20px rgba(0,0,0,0.1);
    }

    .ping-card h2 {
      margin: 0 0 10px 0;
      color: #2c3e50;
      font-size: 24px;
    }

    .ping-card p {
      margin: 0 0 30px 0;
      color: #7f8c8d;
      font-size: 16px;
    }

    .result {
      margin-top: 20px;
      padding: 20px;
      border-radius: 8px;
      font-family: 'Courier New', monospace;
      font-size: 14px;
      text-align: left;
      max-height: 300px;
      overflow-y: auto;
    }

    .result.success {
      background: #d4edda;
      color: #155724;
      border: 1px solid #c3e6cb;
    }

    .result.error {
      background: #f8d7da;
      color: #721c24;
      border: 1px solid #f5c6cb;
    }

    .result pre {
      margin: 0;
      white-space: pre-wrap;
      word-wrap: break-word;
    }

    @media (max-width: 768px) {
      .header-content {
        flex-direction: column;
        text-align: center;
      }

      .header-content h1 {
        font-size: 24px;
      }

      .tab-btn {
        padding: 15px 10px;
        font-size: 14px;
      }

      .tab-icon {
        display: none;
      }

      .ping-card {
        margin: 20px;
        padding: 30px 20px;
      }
    }
  `]
})
export class AppComponent {
  activeTab: 'creditos' | 'ping' = 'creditos';
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

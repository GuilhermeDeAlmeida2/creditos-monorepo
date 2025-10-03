import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, PingResponse, Credito } from './api.service';
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
            [class.active]="activeTab === 'buscar-credito'"
            (click)="activeTab = 'buscar-credito'">
            <span class="tab-icon">🔍</span>
            Buscar por Número do Crédito
          </button>
        </nav>

        <div class="tab-content">
          <div *ngIf="activeTab === 'creditos'" class="tab-panel">
            <app-creditos></app-creditos>
          </div>
          
          <div *ngIf="activeTab === 'buscar-credito'" class="tab-panel">
            <div class="buscar-credito-panel">
              <div class="buscar-credito-container">
                <div class="search-section">
                  <h2>Buscar Crédito por Número</h2>
                  <div class="search-form">
                    <input 
                      type="text" 
                      [(ngModel)]="numeroCredito" 
                      placeholder="Digite o número do crédito"
                      class="search-input"
                      (keyup.enter)="buscarCreditoPorNumero()">
                    <button 
                      class="btn btn-primary" 
                      (click)="buscarCreditoPorNumero()" 
                      [disabled]="loadingCredito || !numeroCredito.trim()">
                      {{ loadingCredito ? 'Buscando...' : 'Buscar' }}
                    </button>
                  </div>
                </div>

                <!-- Mensagem de erro -->
                <div *ngIf="errorMessageCredito" class="error-message">
                  {{ errorMessageCredito }}
                </div>

                <!-- Resultado -->
                <div *ngIf="creditoDetalhes && !loadingCredito" class="result-section">
                  <div class="result-header">
                    <h3>Detalhes do Crédito</h3>
                  </div>

                  <div class="credito-details">
                    <div class="detail-grid">
                      <div class="detail-item">
                        <label>ID:</label>
                        <span>{{ creditoDetalhes.id }}</span>
                      </div>
                      <div class="detail-item">
                        <label>Número do Crédito:</label>
                        <span>{{ creditoDetalhes.numeroCredito }}</span>
                      </div>
                      <div class="detail-item">
                        <label>Número da NFS-e:</label>
                        <span>{{ creditoDetalhes.numeroNfse }}</span>
                      </div>
                      <div class="detail-item">
                        <label>Data de Constituição:</label>
                        <span>{{ formatDate(creditoDetalhes.dataConstituicao) }}</span>
                      </div>
                      <div class="detail-item">
                        <label>Valor ISSQN:</label>
                        <span class="currency">{{ formatCurrency(creditoDetalhes.valorIssqn) }}</span>
                      </div>
                      <div class="detail-item">
                        <label>Tipo do Crédito:</label>
                        <span>{{ creditoDetalhes.tipoCredito }}</span>
                      </div>
                      <div class="detail-item">
                        <label>Simples Nacional:</label>
                        <span class="badge" [class.badge-success]="creditoDetalhes.simplesNacional" [class.badge-danger]="!creditoDetalhes.simplesNacional">
                          {{ creditoDetalhes.simplesNacional ? 'Sim' : 'Não' }}
                        </span>
                      </div>
                      <div class="detail-item">
                        <label>Alíquota:</label>
                        <span>{{ creditoDetalhes.aliquota }}%</span>
                      </div>
                      <div class="detail-item">
                        <label>Valor Faturado:</label>
                        <span class="currency">{{ formatCurrency(creditoDetalhes.valorFaturado) }}</span>
                      </div>
                      <div class="detail-item">
                        <label>Valor Dedução:</label>
                        <span class="currency">{{ formatCurrency(creditoDetalhes.valorDeducao) }}</span>
                      </div>
                      <div class="detail-item">
                        <label>Base de Cálculo:</label>
                        <span class="currency">{{ formatCurrency(creditoDetalhes.baseCalculo) }}</span>
                      </div>
                    </div>
                  </div>
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

    /* Estilos para busca por crédito */
    .buscar-credito-panel {
      padding: 30px;
    }

    .buscar-credito-container {
      max-width: 1000px;
      margin: 0 auto;
    }

    .search-section {
      background: #f8f9fa;
      padding: 25px;
      border-radius: 12px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      margin-bottom: 30px;
    }

    .search-section h2 {
      margin: 0 0 20px 0;
      color: #2c3e50;
      font-size: 24px;
      font-weight: 600;
    }

    .search-form {
      display: flex;
      gap: 15px;
      margin-bottom: 20px;
      flex-wrap: wrap;
      align-items: center;
    }

    .search-input {
      flex: 1;
      min-width: 250px;
      padding: 12px 16px;
      border: 2px solid #e1e8ed;
      border-radius: 8px;
      font-size: 16px;
      transition: border-color 0.3s ease;
    }

    .search-input:focus {
      outline: none;
      border-color: #3498db;
      box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
    }


    .error-message {
      background: #e74c3c;
      color: white;
      padding: 15px 20px;
      border-radius: 8px;
      margin-bottom: 20px;
      font-weight: 500;
    }

    .result-section {
      background: #f8f9fa;
      border-radius: 12px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      overflow: hidden;
    }

    .result-header {
      padding: 20px 25px;
      background: white;
      border-bottom: 1px solid #e9ecef;
    }

    .result-header h3 {
      margin: 0;
      color: #2c3e50;
      font-size: 20px;
    }

    .credito-details {
      padding: 25px;
    }

    .detail-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 20px;
    }

    .detail-item {
      display: flex;
      flex-direction: column;
      gap: 5px;
    }

    .detail-item label {
      font-weight: 600;
      color: #555;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .detail-item span {
      font-size: 16px;
      color: #2c3e50;
      padding: 8px 12px;
      background: white;
      border-radius: 6px;
      border-left: 4px solid #3498db;
    }

    .currency {
      text-align: right;
      font-weight: 600;
      color: #27ae60;
    }

    .badge {
      padding: 4px 8px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 600;
      text-transform: uppercase;
      width: fit-content;
    }

    .badge-success {
      background: #d4edda;
      color: #155724;
    }

    .badge-danger {
      background: #f8d7da;
      color: #721c24;
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
  activeTab: 'creditos' | 'buscar-credito' = 'creditos';
  loading = false;
  result: string | null = null;
  isError = false;

  // Propriedades para busca por número do crédito
  numeroCredito: string = '';
  creditoDetalhes: Credito | null = null;
  loadingCredito: boolean = false;
  errorMessageCredito: string = '';

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

  // Métodos para busca por número do crédito
  buscarCreditoPorNumero(): void {
    if (!this.numeroCredito.trim()) {
      this.errorMessageCredito = 'Por favor, digite um número de crédito válido.';
      return;
    }

    this.loadingCredito = true;
    this.errorMessageCredito = '';
    this.creditoDetalhes = null;

    this.apiService.buscarCreditoPorNumero(this.numeroCredito.trim()).subscribe({
      next: (response) => {
        this.creditoDetalhes = response;
        this.loadingCredito = false;
      },
      error: (error) => {
        if (error.status === 404) {
          this.errorMessageCredito = `Nenhum crédito encontrado para o número: ${this.numeroCredito}`;
        } else {
          this.errorMessageCredito = `Erro ao buscar crédito: ${error.message || 'Erro interno do servidor'}`;
        }
        this.loadingCredito = false;
      }
    });
  }


  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('pt-BR');
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }
}

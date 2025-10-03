import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, PingResponse, Credito } from './api.service';
import { CreditosComponent } from './creditos.component';
import { ButtonComponent, ButtonVariant } from './components/ui/button/button.component';
import { InputComponent } from './components/ui/input/input.component';
import { BadgeComponent } from './components/ui/badge/badge.component';
import { ErrorMessageComponent } from './components/ui/error-message/error-message.component';
import { CardComponent } from './components/ui/card/card.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, CreditosComponent, ButtonComponent, InputComponent, BadgeComponent, ErrorMessageComponent, CardComponent],
  template: `
    <div class="app-container">
      <header class="app-header">
        <div class="header-content">
          <h1>Sistema de Créditos Constituídos</h1>
          <p>Gerenciamento e consulta de créditos por NFS-e</p>
        </div>
        <div class="connection-status">
          <app-button 
            [variant]="getConnectionButtonVariant()"
            size="small"
            [loading]="loading"
            (clicked)="pingApi()">
            {{ loading ? 'Testando...' : (result ? 'Conectado' : 'Testar Conexão') }}
          </app-button>
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
                <!-- Card de busca -->
                <app-card 
                  title="Buscar Crédito por Número"
                  variant="elevated"
                  class="search-card">
                  <div class="search-form">
                    <app-input
                      type="text"
                      placeholder="Digite o número do crédito"
                      [(ngModel)]="numeroCredito"
                      (enterKey)="buscarCreditoPorNumero()"
                      [showClearButton]="true">
                    </app-input>
                    <app-button 
                      variant="primary"
                      [disabled]="loadingCredito || !numeroCredito.trim()"
                      [loading]="loadingCredito"
                      (clicked)="buscarCreditoPorNumero()">
                      {{ loadingCredito ? 'Buscando...' : 'Buscar' }}
                    </app-button>
                  </div>
                </app-card>

                <!-- Mensagem de erro -->
                <app-error-message 
                  *ngIf="errorMessageCredito" 
                  type="error"
                  [message]="errorMessageCredito"
                  [dismissible]="true">
                </app-error-message>

                <!-- Card de resultado -->
                <app-card 
                  *ngIf="creditoDetalhes && !loadingCredito"
                  title="Detalhes do Crédito"
                  variant="default"
                  class="result-card">
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
                        <app-badge [variant]="creditoDetalhes.simplesNacional ? 'success' : 'danger'">
                          {{ creditoDetalhes.simplesNacional ? 'Sim' : 'Não' }}
                        </app-badge>
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
                </app-card>
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
      display: flex;
      flex-direction: column;
      gap: 20px;
    }

    .search-card {
      margin-bottom: 20px;
    }

    .result-card {
      margin-top: 20px;
    }

    .search-form {
      display: flex;
      gap: 15px;
      flex-wrap: wrap;
      align-items: center;
    }

    .credito-details {
      padding: 0;
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

  getConnectionButtonVariant(): ButtonVariant {
    if (this.isError) return 'error';
    if (this.result) return 'success';
    return 'primary';
  }
}

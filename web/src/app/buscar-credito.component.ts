import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Credito } from './api.service';

@Component({
  selector: 'app-buscar-credito',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="buscar-credito-container">
      <div class="search-section">
        <h2>Buscar Crédito por Número</h2>
        <div class="search-form">
          <input 
            type="text" 
            [(ngModel)]="numeroCredito" 
            placeholder="Digite o número do crédito"
            class="search-input"
            (keyup.enter)="buscarCredito()">
          <button 
            class="btn btn-primary" 
            (click)="buscarCredito()" 
            [disabled]="loading || !numeroCredito.trim()">
            {{ loading ? 'Buscando...' : 'Buscar' }}
          </button>
          <button 
            class="btn btn-secondary" 
            (click)="abrirEmNovaAba()" 
            [disabled]="loading || !numeroCredito.trim()">
            Buscar em Nova Aba
          </button>
        </div>
      </div>

      <!-- Mensagem de erro -->
      <div *ngIf="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <!-- Resultado -->
      <div *ngIf="credito && !loading" class="result-section">
        <div class="result-header">
          <h3>Detalhes do Crédito</h3>
          <button class="btn btn-info" (click)="abrirEmNovaAba()">
            Abrir em Nova Aba
          </button>
        </div>

        <div class="credito-details">
          <div class="detail-grid">
            <div class="detail-item">
              <label>ID:</label>
              <span>{{ credito.id }}</span>
            </div>
            <div class="detail-item">
              <label>Número do Crédito:</label>
              <span>{{ credito.numeroCredito }}</span>
            </div>
            <div class="detail-item">
              <label>Número da NFS-e:</label>
              <span>{{ credito.numeroNfse }}</span>
            </div>
            <div class="detail-item">
              <label>Data de Constituição:</label>
              <span>{{ formatDate(credito.dataConstituicao) }}</span>
            </div>
            <div class="detail-item">
              <label>Valor ISSQN:</label>
              <span class="currency">{{ formatCurrency(credito.valorIssqn) }}</span>
            </div>
            <div class="detail-item">
              <label>Tipo do Crédito:</label>
              <span>{{ credito.tipoCredito }}</span>
            </div>
            <div class="detail-item">
              <label>Simples Nacional:</label>
              <span class="badge" [class.badge-success]="credito.simplesNacional" [class.badge-danger]="!credito.simplesNacional">
                {{ credito.simplesNacional ? 'Sim' : 'Não' }}
              </span>
            </div>
            <div class="detail-item">
              <label>Alíquota:</label>
              <span>{{ credito.aliquota }}%</span>
            </div>
            <div class="detail-item">
              <label>Valor Faturado:</label>
              <span class="currency">{{ formatCurrency(credito.valorFaturado) }}</span>
            </div>
            <div class="detail-item">
              <label>Valor Dedução:</label>
              <span class="currency">{{ formatCurrency(credito.valorDeducao) }}</span>
            </div>
            <div class="detail-item">
              <label>Base de Cálculo:</label>
              <span class="currency">{{ formatCurrency(credito.baseCalculo) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .buscar-credito-container {
      max-width: 1000px;
      margin: 0 auto;
      padding: 20px;
    }

    .search-section {
      background: white;
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

    .btn-primary {
      background: linear-gradient(135deg, #3498db, #2980b9);
      color: white;
    }

    .btn-primary:hover:not(:disabled) {
      background: linear-gradient(135deg, #2980b9, #1f618d);
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
    }

    .btn-secondary {
      background: #95a5a6;
      color: white;
    }

    .btn-secondary:hover:not(:disabled) {
      background: #7f8c8d;
      transform: translateY(-1px);
    }

    .btn-info {
      background: #17a2b8;
      color: white;
      padding: 8px 16px;
      font-size: 14px;
    }

    .btn-info:hover:not(:disabled) {
      background: #138496;
      transform: translateY(-1px);
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
      background: white;
      border-radius: 12px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      overflow: hidden;
    }

    .result-header {
      padding: 20px 25px;
      background: #f8f9fa;
      border-bottom: 1px solid #e9ecef;
      display: flex;
      justify-content: space-between;
      align-items: center;
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
      background: #f8f9fa;
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
      .search-form {
        flex-direction: column;
        align-items: stretch;
      }

      .search-input {
        min-width: auto;
      }

      .result-header {
        flex-direction: column;
        gap: 15px;
        align-items: stretch;
      }

      .detail-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class BuscarCreditoComponent implements OnInit {
  numeroCredito: string = '';
  credito: Credito | null = null;
  loading: boolean = false;
  errorMessage: string = '';

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {}

  buscarCredito(): void {
    if (!this.numeroCredito.trim()) {
      this.errorMessage = 'Por favor, digite um número de crédito válido.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.credito = null;

    this.apiService.buscarCreditoPorNumero(this.numeroCredito.trim()).subscribe({
      next: (response) => {
        this.credito = response;
        this.loading = false;
      },
      error: (error) => {
        if (error.status === 404) {
          this.errorMessage = `Nenhum crédito encontrado para o número: ${this.numeroCredito}`;
        } else {
          this.errorMessage = `Erro ao buscar crédito: ${error.message || 'Erro interno do servidor'}`;
        }
        this.loading = false;
      }
    });
  }

  abrirEmNovaAba(): void {
    if (!this.numeroCredito.trim()) {
      this.errorMessage = 'Por favor, digite um número de crédito válido.';
      return;
    }

    // Criar URL para nova aba
    const url = this.gerarUrlNovaAba();
    window.open(url, '_blank');
  }

  private gerarUrlNovaAba(): string {
    // Obter a URL base atual
    const baseUrl = window.location.origin + window.location.pathname;
    
    // Criar parâmetros de busca
    const params = new URLSearchParams();
    params.set('numeroCredito', this.numeroCredito.trim());
    params.set('novaAba', 'true');
    
    return `${baseUrl}?${params.toString()}`;
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

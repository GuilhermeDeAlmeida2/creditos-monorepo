import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Credito } from './api.service';
import { CardComponent } from './components/ui/card/card.component';
import { ButtonComponent } from './components/ui/button/button.component';
import { InputComponent } from './components/ui/input/input.component';
import { BadgeComponent } from './components/ui/badge/badge.component';
import { ErrorMessageComponent } from './components/ui/error-message/error-message.component';

@Component({
  selector: 'app-buscar-credito',
  standalone: true,
  imports: [CommonModule, FormsModule, CardComponent, ButtonComponent, InputComponent, BadgeComponent, ErrorMessageComponent],
  template: `
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
            (enterKey)="buscarCredito()">
          </app-input>
          <app-button 
            variant="primary"
            [disabled]="loading || !numeroCredito.trim()"
            [loading]="loading"
            (clicked)="buscarCredito()">
            {{ loading ? 'Buscando...' : 'Buscar' }}
          </app-button>
          <app-button 
            variant="secondary"
            [disabled]="loading || !numeroCredito.trim()"
            (clicked)="abrirEmNovaAba()">
            Buscar em Nova Aba
          </app-button>
        </div>
      </app-card>

      <!-- Mensagem de erro -->
      <app-error-message 
        *ngIf="errorMessage" 
        type="error"
        [message]="errorMessage"
        [dismissible]="true">
      </app-error-message>

      <!-- Card de resultado -->
      <app-card 
        *ngIf="credito && !loading"
        title="Detalhes do Crédito"
        variant="default"
        class="result-card"
        [showFooter]="true">
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
              <app-badge [variant]="credito.simplesNacional ? 'success' : 'danger'">
                {{ credito.simplesNacional ? 'Sim' : 'Não' }}
              </app-badge>
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
        
        <div slot="footer">
          <app-button variant="info" size="small" (clicked)="abrirEmNovaAba()">
            Abrir em Nova Aba
          </app-button>
        </div>
      </app-card>
    </div>
  `,
  styles: [`
    .buscar-credito-container {
      max-width: 1000px;
      margin: 0 auto;
      padding: 20px;
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
      margin-bottom: 20px;
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
      background: #f8f9fa;
      border-radius: 6px;
      border-left: 4px solid #3498db;
    }

    .currency {
      text-align: right;
      font-weight: 600;
      color: #27ae60;
    }

    @media (max-width: 768px) {
      .search-form {
        flex-direction: column;
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

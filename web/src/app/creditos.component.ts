import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, PaginatedCreditoResponse } from './api.service';
import { CardComponent } from './components/ui/card/card.component';
import { ButtonComponent } from './components/ui/button/button.component';
import { InputComponent } from './components/ui/input/input.component';
import { BadgeComponent } from './components/ui/badge/badge.component';
import { ErrorMessageComponent } from './components/ui/error-message/error-message.component';

@Component({
  selector: 'app-creditos',
  standalone: true,
  imports: [CommonModule, FormsModule, CardComponent, ButtonComponent, InputComponent, BadgeComponent, ErrorMessageComponent],
  template: `
    <div class="creditos-container">
      <!-- Card de busca -->
      <app-card 
        title="Buscar Créditos por NFS-e"
        variant="elevated"
        class="search-card">
        <div class="search-form">
          <app-input
            type="text"
            placeholder="Digite o número da NFS-e"
            [(ngModel)]="numeroNfse"
            (enterKey)="buscarCreditos()">
          </app-input>
          <app-button 
            variant="primary"
            [disabled]="loading || !numeroNfse.trim()"
            [loading]="loading"
            (clicked)="buscarCreditos()">
            {{ loading ? 'Buscando...' : 'Buscar' }}
          </app-button>
        </div>
        
        <div class="page-size-selector">
          <label for="pageSize">Itens por página:</label>
          <select 
            id="pageSize" 
            [(ngModel)]="pageSize" 
            (change)="onPageSizeChange()"
            class="page-size-select">
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="20">20</option>
            <option value="50">50</option>
          </select>
        </div>
      </app-card>

      <!-- Mensagem de erro -->
      <app-error-message 
        *ngIf="errorMessage" 
        type="error"
        [message]="errorMessage"
        [dismissible]="true">
      </app-error-message>

      <!-- Card de resultados -->
      <app-card 
        *ngIf="creditosResponse && !loading"
        title="Resultados da Busca"
        variant="default"
        class="results-card">
        <div class="results-info">
          <span class="total-info">
            Total: {{ creditosResponse.totalElements }} crédito(s) encontrado(s)
          </span>
          <span class="page-info">
            Página {{ creditosResponse.page + 1 }} de {{ creditosResponse.totalPages }}
          </span>
        </div>

        <!-- Tabela de créditos -->
        <div class="table-container">
          <table class="creditos-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Número do Crédito</th>
                <th>Data Constituição</th>
                <th>Valor ISSQN</th>
                <th>Tipo</th>
                <th>Simples Nacional</th>
                <th>Alíquota</th>
                <th>Valor Faturado</th>
                <th>Valor Dedução</th>
                <th>Base Cálculo</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let credito of creditosResponse.content" class="credito-row">
                <td>{{ credito.id }}</td>
                <td>{{ credito.numeroCredito }}</td>
                <td>{{ formatDate(credito.dataConstituicao) }}</td>
                <td class="currency">{{ formatCurrency(credito.valorIssqn) }}</td>
                <td>{{ credito.tipoCredito }}</td>
                <td>
                  <app-badge [variant]="credito.simplesNacional ? 'success' : 'danger'">
                    {{ credito.simplesNacional ? 'Sim' : 'Não' }}
                  </app-badge>
                </td>
                <td>{{ credito.aliquota }}%</td>
                <td class="currency">{{ formatCurrency(credito.valorFaturado) }}</td>
                <td class="currency">{{ formatCurrency(credito.valorDeducao) }}</td>
                <td class="currency">{{ formatCurrency(credito.baseCalculo) }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Paginação -->
        <div *ngIf="creditosResponse.totalPages > 1" class="pagination">
          <app-button 
            variant="secondary" 
            size="small"
            (clicked)="irParaPagina(0)"
            [disabled]="creditosResponse.first">
            Primeira
          </app-button>
          <app-button 
            variant="secondary" 
            size="small"
            (clicked)="irParaPagina(creditosResponse.page - 1)"
            [disabled]="!creditosResponse.hasPrevious">
            Anterior
          </app-button>
          
          <span class="page-numbers">
            <app-button 
              *ngFor="let page of getPageNumbers()" 
              variant="secondary" 
              size="small"
              [class.btn-active]="page === creditosResponse.page"
              (clicked)="irParaPagina(page)">
              {{ page + 1 }}
            </app-button>
          </span>
          
          <app-button 
            variant="secondary" 
            size="small"
            (clicked)="irParaPagina(creditosResponse.page + 1)"
            [disabled]="!creditosResponse.hasNext">
            Próxima
          </app-button>
          <app-button 
            variant="secondary" 
            size="small"
            (clicked)="irParaPagina(creditosResponse.totalPages - 1)"
            [disabled]="creditosResponse.last">
            Última
          </app-button>
        </div>
      </app-card>
    </div>
  `,
  styles: [`
    .creditos-container {
      max-width: 1400px;
      margin: 0 auto;
      padding: 20px;
      display: flex;
      flex-direction: column;
      gap: 20px;
    }

    .search-card {
      margin-bottom: 20px;
    }

    .results-card {
      margin-top: 20px;
    }

    .search-form {
      display: flex;
      gap: 15px;
      margin-bottom: 20px;
      flex-wrap: wrap;
      align-items: center;
    }

    .page-size-selector {
      display: flex;
      align-items: center;
      gap: 10px;
      color: #555;
      font-weight: 500;
    }

    .page-size-select {
      padding: 8px 12px;
      border: 2px solid #e1e8ed;
      border-radius: 6px;
      background: white;
      font-size: 14px;
    }

    .results-info {
      display: flex;
      gap: 20px;
      font-size: 14px;
      color: #6c757d;
      margin-bottom: 20px;
      padding-bottom: 15px;
      border-bottom: 1px solid #e9ecef;
    }

    .table-container {
      overflow-x: auto;
      margin-bottom: 20px;
    }

    .creditos-table {
      width: 100%;
      border-collapse: collapse;
      font-size: 14px;
    }

    .creditos-table th {
      background: #34495e;
      color: white;
      padding: 15px 12px;
      text-align: left;
      font-weight: 600;
      position: sticky;
      top: 0;
      z-index: 10;
    }

    .creditos-table td {
      padding: 12px;
      border-bottom: 1px solid #e9ecef;
      vertical-align: middle;
    }

    .credito-row:hover {
      background: #f8f9fa;
    }

    .currency {
      text-align: right;
      font-weight: 600;
      color: #27ae60;
    }

    .pagination {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 10px;
      flex-wrap: wrap;
      padding-top: 20px;
      border-top: 1px solid #e9ecef;
    }

    .page-numbers {
      display: flex;
      gap: 5px;
    }

    .btn-active {
      background: #3498db !important;
      color: white !important;
    }

    @media (max-width: 768px) {
      .search-form {
        flex-direction: column;
        align-items: stretch;
      }

      .results-info {
        flex-direction: column;
        gap: 5px;
      }

      .creditos-table {
        font-size: 12px;
      }

      .creditos-table th,
      .creditos-table td {
        padding: 8px 6px;
      }

      .pagination {
        flex-direction: column;
        gap: 15px;
      }

      .page-numbers {
        justify-content: center;
      }
    }
  `]
})
export class CreditosComponent {
  numeroNfse: string = '';
  creditosResponse: PaginatedCreditoResponse | null = null;
  loading: boolean = false;
  errorMessage: string = '';
  pageSize: number = 10;

  constructor(private apiService: ApiService) {}

  buscarCreditos(): void {
    if (!this.numeroNfse.trim()) {
      this.errorMessage = 'Por favor, digite um número de NFS-e válido.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.creditosResponse = null;

    this.apiService.buscarCreditosPorNfse(this.numeroNfse.trim(), 0, this.pageSize).subscribe({
      next: (response) => {
        this.creditosResponse = response;
        this.loading = false;
      },
      error: (error) => {
        if (error.status === 404) {
          this.errorMessage = `Nenhum crédito encontrado para a NFS-e: ${this.numeroNfse}`;
        } else {
          this.errorMessage = `Erro ao buscar créditos: ${error.message || 'Erro interno do servidor'}`;
        }
        this.loading = false;
      }
    });
  }

  onPageSizeChange(): void {
    if (this.creditosResponse) {
      this.buscarCreditos();
    }
  }

  irParaPagina(page: number): void {
    if (this.numeroNfse.trim() && page >= 0) {
      this.loading = true;
      this.errorMessage = '';

      this.apiService.buscarCreditosPorNfse(this.numeroNfse.trim(), page, this.pageSize).subscribe({
        next: (response) => {
          this.creditosResponse = response;
          this.loading = false;
        },
        error: (error) => {
          this.errorMessage = `Erro ao carregar página: ${error.message || 'Erro interno do servidor'}`;
          this.loading = false;
        }
      });
    }
  }

  getPageNumbers(): number[] {
    if (!this.creditosResponse) return [];
    
    const current = this.creditosResponse.page;
    const total = this.creditosResponse.totalPages;
    const pages: number[] = [];
    
    // Mostrar até 5 páginas ao redor da página atual
    const start = Math.max(0, current - 2);
    const end = Math.min(total - 1, current + 2);
    
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    
    return pages;
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


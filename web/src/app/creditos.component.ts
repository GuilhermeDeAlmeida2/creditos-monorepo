import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Credito, PaginatedCreditoResponse } from './api.service';

@Component({
  selector: 'app-creditos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="creditos-container">
      <div class="search-section">
        <h2>Buscar Créditos por NFS-e</h2>
        <div class="search-form">
          <input 
            type="text" 
            [(ngModel)]="numeroNfse" 
            placeholder="Digite o número da NFS-e"
            class="search-input"
            (keyup.enter)="buscarCreditos()">
          <button 
            class="btn btn-primary" 
            (click)="buscarCreditos()" 
            [disabled]="loading || !numeroNfse.trim()">
            {{ loading ? 'Buscando...' : 'Buscar' }}
          </button>
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
      </div>

      <!-- Mensagem de erro -->
      <div *ngIf="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <!-- Resultados -->
      <div *ngIf="creditosResponse && !loading" class="results-section">
        <div class="results-header">
          <h3>Resultados da Busca</h3>
          <div class="results-info">
            <span class="total-info">
              Total: {{ creditosResponse.totalElements }} crédito(s) encontrado(s)
            </span>
            <span class="page-info">
              Página {{ creditosResponse.page + 1 }} de {{ creditosResponse.totalPages }}
            </span>
          </div>
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
                  <span class="badge" [class.badge-success]="credito.simplesNacional" [class.badge-danger]="!credito.simplesNacional">
                    {{ credito.simplesNacional ? 'Sim' : 'Não' }}
                  </span>
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
          <button 
            class="btn btn-secondary" 
            (click)="irParaPagina(0)"
            [disabled]="creditosResponse.first">
            Primeira
          </button>
          <button 
            class="btn btn-secondary" 
            (click)="irParaPagina(creditosResponse.page - 1)"
            [disabled]="!creditosResponse.hasPrevious">
            Anterior
          </button>
          
          <span class="page-numbers">
            <button 
              *ngFor="let page of getPageNumbers()" 
              class="btn btn-page" 
              [class.btn-active]="page === creditosResponse.page"
              (click)="irParaPagina(page)">
              {{ page + 1 }}
            </button>
          </span>
          
          <button 
            class="btn btn-secondary" 
            (click)="irParaPagina(creditosResponse.page + 1)"
            [disabled]="!creditosResponse.hasNext">
            Próxima
          </button>
          <button 
            class="btn btn-secondary" 
            (click)="irParaPagina(creditosResponse.totalPages - 1)"
            [disabled]="creditosResponse.last">
            Última
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .creditos-container {
      max-width: 1400px;
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

    .btn-page {
      background: #ecf0f1;
      color: #2c3e50;
      padding: 8px 12px;
      min-width: 40px;
    }

    .btn-page:hover:not(:disabled) {
      background: #d5dbdb;
    }

    .btn-active {
      background: #3498db;
      color: white;
    }

    .error-message {
      background: #e74c3c;
      color: white;
      padding: 15px 20px;
      border-radius: 8px;
      margin-bottom: 20px;
      font-weight: 500;
    }

    .results-section {
      background: white;
      border-radius: 12px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      overflow: hidden;
    }

    .results-header {
      padding: 20px 25px;
      background: #f8f9fa;
      border-bottom: 1px solid #e9ecef;
    }

    .results-header h3 {
      margin: 0 0 10px 0;
      color: #2c3e50;
      font-size: 20px;
    }

    .results-info {
      display: flex;
      gap: 20px;
      font-size: 14px;
      color: #6c757d;
    }

    .table-container {
      overflow-x: auto;
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

    .badge {
      padding: 4px 8px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 600;
      text-transform: uppercase;
    }

    .badge-success {
      background: #d4edda;
      color: #155724;
    }

    .badge-danger {
      background: #f8d7da;
      color: #721c24;
    }

    .pagination {
      padding: 20px 25px;
      background: #f8f9fa;
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 10px;
      flex-wrap: wrap;
    }

    .page-numbers {
      display: flex;
      gap: 5px;
    }

    @media (max-width: 768px) {
      .search-form {
        flex-direction: column;
        align-items: stretch;
      }

      .search-input {
        min-width: auto;
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


import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, PaginatedCreditoResponse, Credito } from '../api.service';
import { CardComponent } from '../components/ui/card/card.component';
import { ButtonComponent } from '../components/ui/button/button.component';
import { InputComponent } from '../components/ui/input/input.component';
import { BadgeComponent } from '../components/ui/badge/badge.component';
import { ErrorMessageComponent } from '../components/ui/error-message/error-message.component';
import { TableComponent, TableColumn, TableAction } from '../components/ui/table/table.component';
import { PaginationComponent, PaginationInfo } from '../components/ui/pagination/pagination.component';
import { ModalComponent, ModalButton } from '../components/ui/modal/modal.component';

@Component({
  selector: 'app-creditos',
  standalone: true,
  imports: [CommonModule, FormsModule, CardComponent, ButtonComponent, InputComponent, BadgeComponent, ErrorMessageComponent, TableComponent, PaginationComponent, ModalComponent],
  templateUrl: './creditos.component.html',
  styleUrls: ['./creditos.component.css']
})
export class CreditosComponent implements AfterViewInit {
  numeroNfse: string = '';
  creditosResponse: PaginatedCreditoResponse | null = null;
  loading: boolean = false;
  errorMessage: string = '';
  pageSize: number = 10;

  // Propriedades para TableComponent
  tableColumns: TableColumn[] = [];

  tableActions: TableAction[] = [
    {
      label: 'Ver Detalhes',
      icon: '👁️',
      variant: 'info',
      onClick: (credito: Credito) => this.verDetalhesCredito(credito)
    }
  ];

  // Propriedades para ModalComponent
  showModal: boolean = false;
  creditoDetalhes: Credito | null = null;
  modalButtons: ModalButton[] = [
    {
      label: 'Fechar',
      variant: 'secondary',
      onClick: () => this.fecharModal()
    }
  ];

  // Propriedades para PaginationComponent
  paginationInfo: PaginationInfo | null = null;

  constructor(private apiService: ApiService) {}

  ngAfterViewInit(): void {
    this.initializeTableColumns();
  }

  initializeTableColumns(): void {
    this.tableColumns = [
      { key: 'id', label: 'ID', width: '60px', align: 'center' },
      { key: 'numeroCredito', label: 'Número do Crédito', width: '150px' },
      { key: 'dataConstituicao', label: 'Data Constituição', width: '120px', render: (value) => this.formatDate(value) },
      { key: 'valorIssqn', label: 'Valor ISSQN', width: '120px', align: 'right', render: (value) => `<span class="currency">${this.formatCurrency(value)}</span>` },
      { key: 'tipoCredito', label: 'Tipo', width: '100px' },
      { key: 'simplesNacional', label: 'Simples Nacional', width: '120px', align: 'center', render: (value) => this.renderSimplesNacional(value) },
      { key: 'aliquota', label: 'Alíquota', width: '80px', align: 'center', render: (value) => `${value}%` },
      { key: 'valorFaturado', label: 'Valor Faturado', width: '120px', align: 'right', render: (value) => `<span class="currency">${this.formatCurrency(value)}</span>` },
      { key: 'valorDeducao', label: 'Valor Dedução', width: '120px', align: 'right', render: (value) => `<span class="currency">${this.formatCurrency(value)}</span>` },
      { key: 'baseCalculo', label: 'Base Cálculo', width: '120px', align: 'right', render: (value) => `<span class="currency">${this.formatCurrency(value)}</span>` }
    ];
  }

  renderSimplesNacional(value: boolean): string {
    const variant = value ? 'success' : 'danger';
    const text = value ? 'Sim' : 'Não';
    return `<span class="badge badge-${variant}">${text}</span>`;
  }

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
        this.updatePaginationInfo(response);
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

  onPageSizeChange(newSize: number): void {
    this.pageSize = newSize;
    if (this.creditosResponse) {
      this.buscarCreditos();
    }
  }

  onPageChange(page: number): void {
    if (this.numeroNfse.trim() && page >= 0) {
      this.loading = true;
      this.errorMessage = '';

      this.apiService.buscarCreditosPorNfse(this.numeroNfse.trim(), page, this.pageSize).subscribe({
        next: (response) => {
          this.creditosResponse = response;
          this.updatePaginationInfo(response);
          this.loading = false;
        },
        error: (error) => {
          this.errorMessage = `Erro ao carregar página: ${error.message || 'Erro interno do servidor'}`;
          this.loading = false;
        }
      });
    }
  }

  updatePaginationInfo(response: PaginatedCreditoResponse): void {
    this.paginationInfo = {
      page: response.page,
      size: response.size,
      totalElements: response.totalElements,
      totalPages: response.totalPages,
      first: response.first,
      last: response.last,
      hasNext: response.hasNext,
      hasPrevious: response.hasPrevious
    };
  }

  verDetalhesCredito(credito: Credito): void {
    this.creditoDetalhes = credito;
    this.showModal = true;
  }

  fecharModal(): void {
    this.showModal = false;
    this.creditoDetalhes = null;
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


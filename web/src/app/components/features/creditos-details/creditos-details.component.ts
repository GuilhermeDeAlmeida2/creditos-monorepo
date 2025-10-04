import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, PaginatedCreditoResponse, Credito } from '../../../api.service';
import { CardComponent } from '../../ui/card/card.component';
import { ButtonComponent } from '../../ui/button/button.component';
import { InputComponent } from '../../ui/input/input.component';
import { BadgeComponent } from '../../ui/badge/badge.component';
import { ErrorMessageComponent } from '../../ui/error-message/error-message.component';
import { TableComponent, TableColumn, TableAction } from '../../ui/table/table.component';
import { PaginationComponent, PaginationInfo } from '../../ui/pagination/pagination.component';
import { ModalComponent, ModalButton } from '../../ui/modal/modal.component';
import { CurrencyFormatPipe } from '../../shared/pipes/currency-format.pipe';
import { DateFormatPipe } from '../../shared/pipes/date-format.pipe';
import { PercentageFormatPipe } from '../../shared/pipes/percentage-format.pipe';

@Component({
  selector: 'app-creditos-details',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CardComponent,
    ButtonComponent,
    InputComponent,
    BadgeComponent,
    ErrorMessageComponent,
    TableComponent,
    PaginationComponent,
    ModalComponent,
    CurrencyFormatPipe,
    DateFormatPipe,
    PercentageFormatPipe,
  ],
  templateUrl: './creditos-details.component.html',
  styleUrls: ['./creditos-details.component.css'],
})
export class CreditosDetailsComponent implements AfterViewInit {
  numeroNfse: string = '';
  creditosResponse: PaginatedCreditoResponse | null = null;
  loading: boolean = false;
  errorMessage: string = '';
  pageSize: number = 10;
  
  // Propriedades para filtro por numeroCredito
  filtroNumeroCredito: string = '';
  creditosFiltrados: Credito[] = [];
  
  // Propriedades para ordenação
  currentSort: { column: string; direction: 'asc' | 'desc' } = {
    column: 'dataConstituicao',
    direction: 'desc'
  };

  // Propriedades para TableComponent
  tableColumns: TableColumn[] = [];

  tableActions: TableAction[] = [
    {
      label: 'Ver Detalhes',
      icon: '👁️',
      variant: 'info',
      onClick: (credito: Credito) => this.verDetalhesCredito(credito),
    },
  ];

  // Propriedades para ModalComponent
  showModal: boolean = false;
  creditoDetalhes: Credito | null = null;
  modalButtons: ModalButton[] = [
    {
      label: 'Fechar',
      variant: 'secondary',
      onClick: () => this.fecharModal(),
    },
  ];

  // Propriedades para PaginationComponent
  paginationInfo: PaginationInfo | null = null;

  constructor(private apiService: ApiService) {}

  ngAfterViewInit(): void {
    this.initializeTableColumns();
  }

  initializeTableColumns(): void {
    this.tableColumns = [
      { key: 'id', label: 'ID', width: '60px', align: 'center', sortable: true },
      { key: 'numeroCredito', label: 'Número do Crédito', width: '150px', sortable: true },
      {
        key: 'dataConstituicao',
        label: 'Data Constituição',
        width: '120px',
        sortable: true,
        render: value => this.formatDate(value),
      },
      {
        key: 'valorIssqn',
        label: 'Valor ISSQN',
        width: '120px',
        align: 'right',
        sortable: true,
        render: value => `<span class="currency">${this.formatCurrency(value)}</span>`,
      },
      { key: 'tipoCredito', label: 'Tipo', width: '100px', sortable: true },
      {
        key: 'simplesNacional',
        label: 'Simples Nacional',
        width: '120px',
        align: 'center',
        sortable: true,
        render: value => this.renderSimplesNacional(value),
      },
      {
        key: 'aliquota',
        label: 'Alíquota',
        width: '80px',
        align: 'center',
        sortable: true,
        render: value => `${value}%`,
      },
      {
        key: 'valorFaturado',
        label: 'Valor Faturado',
        width: '120px',
        align: 'right',
        sortable: true,
        render: value => `<span class="currency">${this.formatCurrency(value)}</span>`,
      },
      {
        key: 'valorDeducao',
        label: 'Valor Dedução',
        width: '120px',
        align: 'right',
        sortable: true,
        render: value => `<span class="currency">${this.formatCurrency(value)}</span>`,
      },
      {
        key: 'baseCalculo',
        label: 'Base Cálculo',
        width: '120px',
        align: 'right',
        sortable: true,
        render: value => `<span class="currency">${this.formatCurrency(value)}</span>`,
      },
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
    this.filtroNumeroCredito = ''; // Limpa o filtro ao fazer nova busca
    this.creditosFiltrados = [];

    this.apiService.buscarCreditosPorNfse(
      this.numeroNfse.trim(), 
      0, 
      this.pageSize, 
      this.currentSort.column, 
      this.currentSort.direction
    ).subscribe({
      next: response => {
        this.creditosResponse = response;
        this.creditosFiltrados = response.content; // Inicializa com todos os créditos
        this.updatePaginationInfo(response);
        this.loading = false;
      },
      error: error => {
        if (error.status === 404) {
          this.errorMessage = `Nenhum crédito encontrado para a NFS-e: ${this.numeroNfse}`;
        } else {
          this.errorMessage = `Erro ao buscar créditos: ${error.message || 'Erro interno do servidor'}`;
        }
        this.loading = false;
      },
    });
  }

  onPageSizeChange(newSize: number): void {
    this.pageSize = newSize;
    if (this.numeroNfse.trim()) {
      this.loading = true;
      this.errorMessage = '';

      this.apiService.buscarCreditosPorNfse(
        this.numeroNfse.trim(), 
        0, // Volta para a primeira página
        this.pageSize, 
        this.currentSort.column, 
        this.currentSort.direction
      ).subscribe({
        next: response => {
          this.creditosResponse = response;
          this.creditosFiltrados = response.content;
          this.updatePaginationInfo(response);
          this.loading = false;
        },
        error: error => {
          this.errorMessage = `Erro ao alterar tamanho da página: ${error.message || 'Erro interno do servidor'}`;
          this.loading = false;
        },
      });
    }
  }

  onPageChange(page: number): void {
    if (this.numeroNfse.trim() && page >= 0) {
      this.loading = true;
      this.errorMessage = '';

      this.apiService.buscarCreditosPorNfse(
        this.numeroNfse.trim(), 
        page, 
        this.pageSize, 
        this.currentSort.column, 
        this.currentSort.direction
      ).subscribe({
        next: response => {
          this.creditosResponse = response;
          this.creditosFiltrados = response.content; // Atualiza os créditos filtrados
          this.updatePaginationInfo(response);
          this.loading = false;
        },
        error: error => {
          this.errorMessage = `Erro ao carregar página: ${error.message || 'Erro interno do servidor'}`;
          this.loading = false;
        },
      });
    }
  }

  filtrarPorNumeroCredito(): void {
    if (!this.creditosResponse) {
      return;
    }

    if (!this.filtroNumeroCredito.trim()) {
      // Se o filtro estiver vazio, mostra todos os créditos
      this.creditosFiltrados = this.creditosResponse.content;
    } else {
      // Filtra os créditos pelo número digitado (busca parcial, case-insensitive)
      this.creditosFiltrados = this.creditosResponse.content.filter(credito =>
        credito.numeroCredito.toLowerCase().includes(this.filtroNumeroCredito.toLowerCase().trim())
      );
    }
  }

  limparFiltro(): void {
    this.filtroNumeroCredito = '';
    if (this.creditosResponse) {
      this.creditosFiltrados = this.creditosResponse.content;
    }
  }

  onSort(sortInfo: { column: string; direction: 'asc' | 'desc' }): void {
    this.currentSort = sortInfo;
    
    // Se há uma busca ativa, refazer a busca com a nova ordenação
    if (this.numeroNfse.trim()) {
      this.loading = true;
      this.errorMessage = '';

      this.apiService.buscarCreditosPorNfse(
        this.numeroNfse.trim(), 
        0, // Volta para a primeira página
        this.pageSize, 
        this.currentSort.column, 
        this.currentSort.direction
      ).subscribe({
        next: response => {
          this.creditosResponse = response;
          this.creditosFiltrados = response.content;
          this.updatePaginationInfo(response);
          this.loading = false;
        },
        error: error => {
          this.errorMessage = `Erro ao ordenar dados: ${error.message || 'Erro interno do servidor'}`;
          this.loading = false;
        },
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
      hasPrevious: response.hasPrevious,
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
      currency: 'BRL',
    }).format(value);
  }
}

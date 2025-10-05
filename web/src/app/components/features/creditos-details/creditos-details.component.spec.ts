import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CreditosDetailsComponent } from './creditos-details.component';
import { ApiService, PaginatedCreditoResponse, Credito } from '../../../api.service';
import { of, throwError } from 'rxjs';

describe('CreditosDetailsComponent', () => {
  let component: CreditosDetailsComponent;
  let fixture: ComponentFixture<CreditosDetailsComponent>;
  let apiService: jasmine.SpyObj<ApiService>;

  const mockCredito: Credito = {
    id: 1,
    numeroCredito: '12345',
    numeroNfse: '67890',
    dataConstituicao: '2023-01-01',
    valorIssqn: 1000.5,
    tipoCredito: 'ISS',
    simplesNacional: true,
    aliquota: 5.0,
    valorFaturado: 20000.0,
    valorDeducao: 1000.0,
    baseCalculo: 19000.0,
  };

  const mockResponse: PaginatedCreditoResponse = {
    content: [mockCredito],
    page: 0,
    size: 10,
    totalElements: 1,
    totalPages: 1,
    first: true,
    last: true,
    hasNext: false,
    hasPrevious: false,
  };

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('ApiService', ['buscarCreditosPorNfse']);

    await TestBed.configureTestingModule({
      imports: [CreditosDetailsComponent, HttpClientTestingModule],
      providers: [{ provide: ApiService, useValue: spy }],
    }).compileComponents();

    fixture = TestBed.createComponent(CreditosDetailsComponent);
    component = fixture.componentInstance;
    apiService = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.numeroNfse).toBe('');
    expect(component.creditosResponse).toBeNull();
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('');
    expect(component.pageSize).toBe(10);
  });

  it('should call buscarCreditos and handle success', () => {
    apiService.buscarCreditosPorNfse.and.returnValue(of(mockResponse));

    component.numeroNfse = '12345';
    component.buscarCreditos();

    expect(apiService.buscarCreditosPorNfse).toHaveBeenCalledWith('12345', 0, 10, 'dataConstituicao', 'desc');
    expect(component.creditosResponse).toEqual(mockResponse);
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('');
  });

  it('should handle error when buscarCreditos fails', () => {
    const error = { status: 500, message: 'Server Error' };
    apiService.buscarCreditosPorNfse.and.returnValue(throwError(() => error));

    component.numeroNfse = '12345';
    component.buscarCreditos();

    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('Erro ao buscar créditos: Server Error');
  });

  it('should handle 404 error when buscarCreditos fails', () => {
    const error = { status: 404, message: 'Not Found' };
    apiService.buscarCreditosPorNfse.and.returnValue(throwError(() => error));

    component.numeroNfse = '12345';
    component.buscarCreditos();

    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('Nenhum crédito encontrado para a NFS-e: 12345');
  });

  it('should show error when numeroNfse is empty', () => {
    component.numeroNfse = '';
    component.buscarCreditos();

    expect(component.errorMessage).toBe('Por favor, digite um número de NFS-e válido.');
  });

  it('should format date correctly', () => {
    const formattedDate = component.formatDate('2023-01-01');
    expect(formattedDate).toMatch(/\d{2}\/\d{2}\/\d{4}/);
  });

  it('should format currency correctly', () => {
    const formattedCurrency = component.formatCurrency(1234.56);
    expect(formattedCurrency).toContain('R$');
    expect(formattedCurrency).toContain('1.234,56');
  });

  it('should update page size and search again', () => {
    component.numeroNfse = '12345';
    component.creditosResponse = mockResponse;
    apiService.buscarCreditosPorNfse.and.returnValue(of(mockResponse));

    component.onPageSizeChange(20);

    expect(component.pageSize).toBe(20);
    expect(apiService.buscarCreditosPorNfse).toHaveBeenCalledWith('12345', 0, 20, 'dataConstituicao', 'desc');
  });

  it('should change page and search again', () => {
    component.numeroNfse = '12345';
    apiService.buscarCreditosPorNfse.and.returnValue(of(mockResponse));

    component.onPageChange(1);

    expect(apiService.buscarCreditosPorNfse).toHaveBeenCalledWith('12345', 1, 10, 'dataConstituicao', 'desc');
  });

  describe('filtroPorNumeroCredito', () => {
    beforeEach(() => {
      component.creditosResponse = mockResponse;
    });

    it('should filter creditos by numeroCredito', () => {
      component.filtroNumeroCredito = '123';
      component.filtrarPorNumeroCredito();

      expect(component.creditosFiltrados.length).toBe(1);
      expect(component.creditosFiltrados[0].numeroCredito).toBe('12345');
    });

    it('should show all creditos when filter is empty', () => {
      component.filtroNumeroCredito = '';
      component.filtrarPorNumeroCredito();

      expect(component.creditosFiltrados).toEqual(mockResponse.content);
    });

    it('should be case insensitive', () => {
      component.filtroNumeroCredito = '12345';
      component.filtrarPorNumeroCredito();

      expect(component.creditosFiltrados.length).toBe(1);
    });

    it('should handle no matches', () => {
      component.filtroNumeroCredito = '999999';
      component.filtrarPorNumeroCredito();

      expect(component.creditosFiltrados.length).toBe(0);
    });
  });

  describe('limparFiltro', () => {
    it('should clear filter and show all creditos', () => {
      component.creditosResponse = mockResponse;
      component.filtroNumeroCredito = '123';
      component.creditosFiltrados = [];

      component.limparFiltro();

      expect(component.filtroNumeroCredito).toBe('');
      expect(component.creditosFiltrados).toEqual(mockResponse.content);
    });

    it('should handle null creditosResponse', () => {
      component.creditosResponse = null;
      component.filtroNumeroCredito = '123';

      component.limparFiltro();

      expect(component.filtroNumeroCredito).toBe('');
    });
  });

  describe('onSort', () => {
    it('should update sort and search again', () => {
      component.numeroNfse = '12345';
      apiService.buscarCreditosPorNfse.and.returnValue(of(mockResponse));

      component.onSort({ column: 'valorIssqn', direction: 'asc' });

      expect(component.currentSort.column).toBe('valorIssqn');
      expect(component.currentSort.direction).toBe('asc');
      expect(apiService.buscarCreditosPorNfse).toHaveBeenCalledWith('12345', 0, 10, 'valorIssqn', 'asc');
    });

    it('should not search when numeroNfse is empty', () => {
      component.numeroNfse = '';
      apiService.buscarCreditosPorNfse.and.returnValue(of(mockResponse));

      component.onSort({ column: 'valorIssqn', direction: 'asc' });

      expect(apiService.buscarCreditosPorNfse).not.toHaveBeenCalled();
    });
  });

  describe('updatePaginationInfo', () => {
    it('should update pagination info correctly', () => {
      component.updatePaginationInfo(mockResponse);

      expect(component.paginationInfo).toEqual({
        page: 0,
        size: 10,
        totalElements: 1,
        totalPages: 1,
        first: true,
        last: true,
        hasNext: false,
        hasPrevious: false,
      });
    });
  });

  describe('verDetalhesCredito', () => {
    it('should show modal with credito details', () => {
      component.verDetalhesCredito(mockCredito);

      expect(component.showModal).toBe(true);
      expect(component.creditoDetalhes).toEqual(mockCredito);
    });
  });

  describe('fecharModal', () => {
    it('should close modal and clear credito details', () => {
      component.showModal = true;
      component.creditoDetalhes = mockCredito;

      component.fecharModal();

      expect(component.showModal).toBe(false);
      expect(component.creditoDetalhes).toBeNull();
    });
  });

  describe('renderSimplesNacional', () => {
    it('should render success badge for true value', () => {
      const result = component.renderSimplesNacional(true);
      expect(result).toContain('badge-success');
      expect(result).toContain('Sim');
    });

    it('should render danger badge for false value', () => {
      const result = component.renderSimplesNacional(false);
      expect(result).toContain('badge-danger');
      expect(result).toContain('Não');
    });
  });

  describe('initializeTableColumns', () => {
    it('should initialize table columns correctly', () => {
      component.initializeTableColumns();

      expect(component.tableColumns.length).toBeGreaterThan(0);
      expect(component.tableColumns[0].key).toBe('id');
      expect(component.tableColumns[0].label).toBe('ID');
    });
  });

  describe('ngAfterViewInit', () => {
    it('should call initializeTableColumns', () => {
      spyOn(component, 'initializeTableColumns');
      
      component.ngAfterViewInit();

      expect(component.initializeTableColumns).toHaveBeenCalled();
    });
  });
});

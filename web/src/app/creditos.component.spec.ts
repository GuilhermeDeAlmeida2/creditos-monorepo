import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CreditosComponent } from './creditos.component';
import { ApiService, PaginatedCreditoResponse, Credito } from './api.service';
import { BuscarCreditoComponent } from './buscar-credito.component';
import { of, throwError } from 'rxjs';

describe('CreditosComponent', () => {
  let component: CreditosComponent;
  let fixture: ComponentFixture<CreditosComponent>;
  let apiService: jasmine.SpyObj<ApiService>;

  const mockCredito: Credito = {
    id: 1,
    numeroCredito: '123456',
    numeroNfse: '7891011',
    dataConstituicao: '2024-02-25',
    valorIssqn: 1500.75,
    tipoCredito: 'ISSQN',
    simplesNacional: true,
    aliquota: 5.00,
    valorFaturado: 30000.00,
    valorDeducao: 5000.00,
    baseCalculo: 25000.00
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
    hasPrevious: false
  };

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('ApiService', ['buscarCreditosPorNfse']);

    await TestBed.configureTestingModule({
      imports: [FormsModule, HttpClientTestingModule, CreditosComponent, BuscarCreditoComponent],
      providers: [
        { provide: ApiService, useValue: spy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CreditosComponent);
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

  describe('buscarCreditos', () => {
    it('should display error message for empty NFS-e', () => {
      component.numeroNfse = '';
      component.buscarCreditos();

      expect(component.errorMessage).toBe('Por favor, digite um número de NFS-e válido.');
      expect(apiService.buscarCreditosPorNfse).not.toHaveBeenCalled();
    });

    it('should display error message for whitespace-only NFS-e', () => {
      component.numeroNfse = '   ';
      component.buscarCreditos();

      expect(component.errorMessage).toBe('Por favor, digite um número de NFS-e válido.');
      expect(apiService.buscarCreditosPorNfse).not.toHaveBeenCalled();
    });

    it('should call API service with correct parameters on successful search', () => {
      apiService.buscarCreditosPorNfse.and.returnValue(of(mockResponse));
      component.numeroNfse = '7891011';

      component.buscarCreditos();

      expect(apiService.buscarCreditosPorNfse).toHaveBeenCalledWith('7891011', 0, 10);
      expect(component.creditosResponse).toEqual(mockResponse);
      expect(component.loading).toBeFalse();
      expect(component.errorMessage).toBe('');
    });

    it('should handle 404 error', () => {
      const error = { status: 404 };
      apiService.buscarCreditosPorNfse.and.returnValue(throwError(() => error));
      component.numeroNfse = '9999999';

      component.buscarCreditos();

      expect(component.errorMessage).toBe('Nenhum crédito encontrado para a NFS-e: 9999999');
      expect(component.loading).toBeFalse();
      expect(component.creditosResponse).toBeNull();
    });

    it('should handle server error', () => {
      const error = { status: 500, message: 'Internal Server Error' };
      apiService.buscarCreditosPorNfse.and.returnValue(throwError(() => error));
      component.numeroNfse = '7891011';

      component.buscarCreditos();

      expect(component.errorMessage).toBe('Erro ao buscar créditos: Internal Server Error');
      expect(component.loading).toBeFalse();
      expect(component.creditosResponse).toBeNull();
    });

    it('should handle generic error', () => {
      const error = { status: 500 };
      apiService.buscarCreditosPorNfse.and.returnValue(throwError(() => error));
      component.numeroNfse = '7891011';

      component.buscarCreditos();

      expect(component.errorMessage).toBe('Erro ao buscar créditos: Erro interno do servidor');
      expect(component.loading).toBeFalse();
      expect(component.creditosResponse).toBeNull();
    });
  });

  describe('onPageSizeChange', () => {
    it('should call buscarCreditos when creditosResponse exists', () => {
      component.creditosResponse = mockResponse;
      spyOn(component, 'buscarCreditos');
      component.pageSize = 20;

      component.onPageSizeChange();

      expect(component.buscarCreditos).toHaveBeenCalled();
    });

    it('should not call buscarCreditos when creditosResponse is null', () => {
      component.creditosResponse = null;
      spyOn(component, 'buscarCreditos');

      component.onPageSizeChange();

      expect(component.buscarCreditos).not.toHaveBeenCalled();
    });
  });

  describe('irParaPagina', () => {
    beforeEach(() => {
      component.numeroNfse = '7891011';
    });

    it('should call API service with correct page number', () => {
      apiService.buscarCreditosPorNfse.and.returnValue(of(mockResponse));

      component.irParaPagina(1);

      expect(apiService.buscarCreditosPorNfse).toHaveBeenCalledWith('7891011', 1, 10);
      expect(component.creditosResponse).toEqual(mockResponse);
      expect(component.loading).toBeFalse();
    });

    it('should handle error when changing page', () => {
      const error = { status: 500, message: 'Server Error' };
      apiService.buscarCreditosPorNfse.and.returnValue(throwError(() => error));

      component.irParaPagina(1);

      expect(component.errorMessage).toBe('Erro ao carregar página: Server Error');
      expect(component.loading).toBeFalse();
    });

    it('should not call API when numeroNfse is empty', () => {
      component.numeroNfse = '';

      component.irParaPagina(1);

      expect(apiService.buscarCreditosPorNfse).not.toHaveBeenCalled();
    });
  });

  describe('getPageNumbers', () => {
    it('should return empty array when creditosResponse is null', () => {
      component.creditosResponse = null;
      expect(component.getPageNumbers()).toEqual([]);
    });

    it('should return correct page numbers for current page in middle', () => {
      component.creditosResponse = {
        ...mockResponse,
        page: 2,
        totalPages: 5
      };

      const pages = component.getPageNumbers();
      expect(pages).toEqual([0, 1, 2, 3, 4]);
    });

    it('should return correct page numbers for current page at start', () => {
      component.creditosResponse = {
        ...mockResponse,
        page: 0,
        totalPages: 5
      };

      const pages = component.getPageNumbers();
      expect(pages).toEqual([0, 1, 2]);
    });

    it('should return correct page numbers for current page at end', () => {
      component.creditosResponse = {
        ...mockResponse,
        page: 4,
        totalPages: 5
      };

      const pages = component.getPageNumbers();
      expect(pages).toEqual([2, 3, 4]);
    });

    it('should return single page when totalPages is 1', () => {
      component.creditosResponse = {
        ...mockResponse,
        page: 0,
        totalPages: 1
      };

      const pages = component.getPageNumbers();
      expect(pages).toEqual([0]);
    });
  });

  describe('formatDate', () => {
    it('should format date correctly', () => {
      const formattedDate = component.formatDate('2024-02-25');
      expect(formattedDate).toMatch(/\d{2}\/\d{2}\/\d{4}/);
    });
  });

  describe('formatCurrency', () => {
    it('should format currency correctly', () => {
      const formattedCurrency = component.formatCurrency(1500.75);
      expect(formattedCurrency).toMatch(/R\$\s*1\.500,75/);
    });

    it('should format large numbers correctly', () => {
      const formattedCurrency = component.formatCurrency(30000.00);
      expect(formattedCurrency).toMatch(/R\$\s*30\.000,00/);
    });
  });

  describe('Tab functionality', () => {
    it('should initialize with nfse tab active', () => {
      expect(component.activeTab).toBe('nfse');
    });

    it('should set active tab to credito', () => {
      component.setActiveTab('credito');
      expect(component.activeTab).toBe('credito');
    });

    it('should set active tab to nfse', () => {
      component.activeTab = 'credito';
      component.setActiveTab('nfse');
      expect(component.activeTab).toBe('nfse');
    });

    it('should render tab buttons', () => {
      fixture.detectChanges();
      const compiled = fixture.nativeElement;
      
      const tabButtons = compiled.querySelectorAll('.tab-button');
      expect(tabButtons.length).toBe(2);
      expect(tabButtons[0].textContent.trim()).toBe('Buscar por NFS-e');
      expect(tabButtons[1].textContent.trim()).toBe('Buscar por Número do Crédito');
    });

    it('should show nfse tab content when nfse tab is active', () => {
      component.activeTab = 'nfse';
      fixture.detectChanges();
      
      const compiled = fixture.nativeElement;
      expect(compiled.querySelector('input[placeholder="Digite o número da NFS-e"]')).toBeTruthy();
      expect(compiled.querySelector('app-buscar-credito')).toBeFalsy();
    });

    it('should return true for isNfseTabActive when nfse tab is active', () => {
      component.activeTab = 'nfse';
      expect(component.isNfseTabActive()).toBeTrue();
    });

    it('should return false for isNfseTabActive when credito tab is active', () => {
      component.activeTab = 'credito';
      expect(component.isNfseTabActive()).toBeFalse();
    });

    it('should return true for isCreditoTabActive when credito tab is active', () => {
      component.activeTab = 'credito';
      expect(component.isCreditoTabActive()).toBeTrue();
    });

    it('should return false for isCreditoTabActive when nfse tab is active', () => {
      component.activeTab = 'nfse';
      expect(component.isCreditoTabActive()).toBeFalse();
    });

    it('should show credito tab content when credito tab is active', () => {
      component.activeTab = 'credito';
      fixture.detectChanges();
      
      const compiled = fixture.nativeElement;
      expect(compiled.querySelector('input[placeholder="Digite o número da NFS-e"]')).toBeFalsy();
      // Verificar se a aba credito está ativa
      const creditoTabButton = compiled.querySelectorAll('.tab-button')[1];
      expect(creditoTabButton.classList.contains('active')).toBeTrue();
    });

    it('should have correct active tab styling', () => {
      component.activeTab = 'nfse';
      fixture.detectChanges();
      
      const compiled = fixture.nativeElement;
      const tabButtons = compiled.querySelectorAll('.tab-button');
      
      expect(tabButtons[0].classList.contains('active')).toBeTrue();
      expect(tabButtons[1].classList.contains('active')).toBeFalse();
    });

    it('should switch active tab styling when tab changes', () => {
      component.activeTab = 'credito';
      fixture.detectChanges();
      
      const compiled = fixture.nativeElement;
      const tabButtons = compiled.querySelectorAll('.tab-button');
      
      expect(tabButtons[0].classList.contains('active')).toBeFalse();
      expect(tabButtons[1].classList.contains('active')).toBeTrue();
    });
  });
});

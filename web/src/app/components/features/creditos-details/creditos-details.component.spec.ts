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

    expect(apiService.buscarCreditosPorNfse).toHaveBeenCalledWith('12345', 0, 10);
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
    expect(apiService.buscarCreditosPorNfse).toHaveBeenCalledWith('12345', 0, 20);
  });

  it('should change page and search again', () => {
    component.numeroNfse = '12345';
    apiService.buscarCreditosPorNfse.and.returnValue(of(mockResponse));

    component.onPageChange(1);

    expect(apiService.buscarCreditosPorNfse).toHaveBeenCalledWith('12345', 1, 10);
  });
});

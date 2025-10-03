import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CreditoSearchComponent } from './credito-search.component';
import { ApiService, Credito } from '../../../api.service';
import { of, throwError } from 'rxjs';

describe('CreditoSearchComponent', () => {
  let component: CreditoSearchComponent;
  let fixture: ComponentFixture<CreditoSearchComponent>;
  let apiService: jasmine.SpyObj<ApiService>;

  const mockCredito: Credito = {
    id: 1,
    numeroCredito: '12345',
    numeroNfse: '67890',
    dataConstituicao: '2023-01-01',
    valorIssqn: 1000.50,
    tipoCredito: 'ISS',
    simplesNacional: true,
    aliquota: 5.0,
    valorFaturado: 20000.00,
    valorDeducao: 1000.00,
    baseCalculo: 19000.00
  };

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('ApiService', ['buscarCreditoPorNumero']);

    await TestBed.configureTestingModule({
      imports: [CreditoSearchComponent, HttpClientTestingModule],
      providers: [
        { provide: ApiService, useValue: spy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CreditoSearchComponent);
    component = fixture.componentInstance;
    apiService = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.numeroCredito).toBe('');
    expect(component.credito).toBeNull();
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('');
  });

  it('should call buscarCredito and handle success', () => {
    apiService.buscarCreditoPorNumero.and.returnValue(of(mockCredito));

    component.numeroCredito = '12345';
    component.buscarCredito();

    expect(apiService.buscarCreditoPorNumero).toHaveBeenCalledWith('12345');
    expect(component.credito).toEqual(mockCredito);
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('');
  });

  it('should handle error when buscarCredito fails', () => {
    const error = { status: 500, message: 'Server Error' };
    apiService.buscarCreditoPorNumero.and.returnValue(throwError(() => error));

    component.numeroCredito = '12345';
    component.buscarCredito();

    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('Erro ao buscar crédito: Server Error');
  });

  it('should handle 404 error when buscarCredito fails', () => {
    const error = { status: 404, message: 'Not Found' };
    apiService.buscarCreditoPorNumero.and.returnValue(throwError(() => error));

    component.numeroCredito = '12345';
    component.buscarCredito();

    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('Nenhum crédito encontrado para o número: 12345');
  });

  it('should show error when numeroCredito is empty', () => {
    component.numeroCredito = '';
    component.buscarCredito();

    expect(component.errorMessage).toBe('Por favor, digite um número de crédito válido.');
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

  it('should open new tab when numeroCredito is provided', () => {
    component.numeroCredito = '12345';
    spyOn(window, 'open');
    
    component.abrirEmNovaAba();
    
    expect(window.open).toHaveBeenCalled();
  });

  it('should show error when trying to open new tab with empty numeroCredito', () => {
    component.numeroCredito = '';
    spyOn(window, 'open');
    
    component.abrirEmNovaAba();
    
    expect(component.errorMessage).toBe('Por favor, digite um número de crédito válido.');
    expect(window.open).not.toHaveBeenCalled();
  });
});
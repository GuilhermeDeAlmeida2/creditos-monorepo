import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { BuscarCreditoComponent } from './buscar-credito.component';
import { ApiService, Credito } from './api.service';

describe('BuscarCreditoComponent', () => {
  let component: BuscarCreditoComponent;
  let fixture: ComponentFixture<BuscarCreditoComponent>;
  let mockApiService: jasmine.SpyObj<ApiService>;

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

  beforeEach(async () => {
    const apiServiceSpy = jasmine.createSpyObj('ApiService', ['buscarCreditoPorNumero']);

    await TestBed.configureTestingModule({
      imports: [BuscarCreditoComponent, FormsModule],
      providers: [
        { provide: ApiService, useValue: apiServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(BuscarCreditoComponent);
    component = fixture.componentInstance;
    mockApiService = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty values', () => {
    expect(component.numeroCredito).toBe('');
    expect(component.credito).toBeNull();
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('');
  });

  describe('buscarCredito', () => {
    it('should show error message when numeroCredito is empty', () => {
      component.numeroCredito = '';
      component.buscarCredito();
      
      expect(component.errorMessage).toBe('Por favor, digite um número de crédito válido.');
      expect(mockApiService.buscarCreditoPorNumero).not.toHaveBeenCalled();
    });

    it('should show error message when numeroCredito is only whitespace', () => {
      component.numeroCredito = '   ';
      component.buscarCredito();
      
      expect(component.errorMessage).toBe('Por favor, digite um número de crédito válido.');
      expect(mockApiService.buscarCreditoPorNumero).not.toHaveBeenCalled();
    });

    it('should call API service and set credito on success', () => {
      mockApiService.buscarCreditoPorNumero.and.returnValue(of(mockCredito));
      component.numeroCredito = '123456';
      
      component.buscarCredito();
      
      expect(component.loading).toBeFalse();
      expect(component.credito).toEqual(mockCredito);
      expect(component.errorMessage).toBe('');
      expect(mockApiService.buscarCreditoPorNumero).toHaveBeenCalledWith('123456');
    });

    it('should handle 404 error', () => {
      const error = { status: 404 };
      mockApiService.buscarCreditoPorNumero.and.returnValue(throwError(() => error));
      component.numeroCredito = '999999';
      
      component.buscarCredito();
      
      expect(component.loading).toBeFalse();
      expect(component.credito).toBeNull();
      expect(component.errorMessage).toBe('Nenhum crédito encontrado para o número: 999999');
    });

    it('should handle server error', () => {
      const error = { status: 500, message: 'Internal Server Error' };
      mockApiService.buscarCreditoPorNumero.and.returnValue(throwError(() => error));
      component.numeroCredito = '123456';
      
      component.buscarCredito();
      
      expect(component.loading).toBeFalse();
      expect(component.credito).toBeNull();
      expect(component.errorMessage).toBe('Erro ao buscar crédito: Internal Server Error');
    });

    it('should handle error without message', () => {
      const error = { status: 500 };
      mockApiService.buscarCreditoPorNumero.and.returnValue(throwError(() => error));
      component.numeroCredito = '123456';
      
      component.buscarCredito();
      
      expect(component.loading).toBeFalse();
      expect(component.credito).toBeNull();
      expect(component.errorMessage).toBe('Erro ao buscar crédito: Erro interno do servidor');
    });

    it('should set loading state correctly', () => {
      mockApiService.buscarCreditoPorNumero.and.returnValue(of(mockCredito));
      component.numeroCredito = '123456';
      
      component.buscarCredito();
      
      expect(component.loading).toBeFalse();
      expect(component.errorMessage).toBe('');
      expect(component.credito).toEqual(mockCredito);
    });
  });

  describe('abrirEmNovaAba', () => {
    beforeEach(() => {
      spyOn(window, 'open');
    });

    it('should show error message when numeroCredito is empty', () => {
      component.numeroCredito = '';
      component.abrirEmNovaAba();
      
      expect(component.errorMessage).toBe('Por favor, digite um número de crédito válido.');
      expect(window.open).not.toHaveBeenCalled();
    });

    it('should show error message when numeroCredito is only whitespace', () => {
      component.numeroCredito = '   ';
      component.abrirEmNovaAba();
      
      expect(component.errorMessage).toBe('Por favor, digite um número de crédito válido.');
      expect(window.open).not.toHaveBeenCalled();
    });

    it('should open new window with correct URL', () => {
      component.numeroCredito = '123456';
      spyOn(component, 'gerarUrlNovaAba' as any).and.returnValue('http://localhost:4200?numeroCredito=123456&novaAba=true');
      
      component.abrirEmNovaAba();
      
      expect(window.open).toHaveBeenCalledWith('http://localhost:4200?numeroCredito=123456&novaAba=true', '_blank');
    });
  });

  describe('formatDate', () => {
    it('should format date correctly', () => {
      const result = component.formatDate('2024-02-25');
      expect(result).toMatch(/25\/02\/2024|24\/02\/2024/);
    });

    it('should handle different date formats', () => {
      const result = component.formatDate('2024-12-31');
      expect(result).toMatch(/31\/12\/2024|30\/12\/2024/);
    });
  });

  describe('formatCurrency', () => {
    it('should format currency correctly', () => {
      const result = component.formatCurrency(1500.75);
      expect(result).toMatch(/R\$\s*1\.500,75/);
    });

    it('should handle zero value', () => {
      const result = component.formatCurrency(0);
      expect(result).toMatch(/R\$\s*0,00/);
    });

    it('should handle large values', () => {
      const result = component.formatCurrency(1234567.89);
      expect(result).toMatch(/R\$\s*1\.234\.567,89/);
    });
  });

  describe('Template rendering', () => {
    it('should render search form', () => {
      const compiled = fixture.nativeElement;
      expect(compiled.querySelector('input[placeholder="Digite o número do crédito"]')).toBeTruthy();
      expect(compiled.querySelector('button[class*="btn-primary"]')).toBeTruthy();
      expect(compiled.querySelector('button[class*="btn-secondary"]')).toBeTruthy();
    });

    it('should show error message when present', () => {
      component.errorMessage = 'Test error message';
      fixture.detectChanges();
      
      const compiled = fixture.nativeElement;
      const errorElement = compiled.querySelector('.error-message');
      expect(errorElement).toBeTruthy();
      expect(errorElement.textContent.trim()).toBe('Test error message');
    });

    it('should show loading state on button', () => {
      component.loading = true;
      fixture.detectChanges();
      
      const compiled = fixture.nativeElement;
      const button = compiled.querySelector('button[class*="btn-primary"]');
      expect(button.textContent.trim()).toBe('Buscando...');
      expect(button.disabled).toBeTrue();
    });

    it('should show credito details when present', () => {
      component.credito = mockCredito;
      fixture.detectChanges();
      
      const compiled = fixture.nativeElement;
      expect(compiled.querySelector('.result-section')).toBeTruthy();
      expect(compiled.querySelector('.credito-details')).toBeTruthy();
      expect(compiled.textContent).toContain('123456');
      expect(compiled.textContent).toContain('7891011');
      expect(compiled.textContent).toContain('ISSQN');
    });

    it('should not show credito details when not present', () => {
      component.credito = null;
      fixture.detectChanges();
      
      const compiled = fixture.nativeElement;
      expect(compiled.querySelector('.result-section')).toBeFalsy();
    });
  });
});

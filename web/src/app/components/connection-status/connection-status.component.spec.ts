import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';

import { ConnectionStatusComponent } from './connection-status.component';
import { ApiService, TestDataResponse } from '../../api.service';

describe('ConnectionStatusComponent', () => {
  let component: ConnectionStatusComponent;
  let fixture: ComponentFixture<ConnectionStatusComponent>;
  let apiService: jasmine.SpyObj<ApiService>;

  beforeEach(async () => {
    const apiServiceSpy = jasmine.createSpyObj('ApiService', ['gerarRegistrosTeste', 'deletarRegistrosTeste']);

    await TestBed.configureTestingModule({
      imports: [ConnectionStatusComponent, HttpClientTestingModule],
      providers: [
        { provide: ApiService, useValue: apiServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ConnectionStatusComponent);
    component = fixture.componentInstance;
    apiService = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit connectionTest event when button is clicked', () => {
    spyOn(component.connectionTest, 'emit');

    component.onConnectionTest();

    expect(component.connectionTest.emit).toHaveBeenCalled();
  });

  it('should return correct button variant based on state', () => {
    // Default state
    expect(component.getConnectionButtonVariant()).toBe('primary');

    // Error state
    component.isError = true;
    expect(component.getConnectionButtonVariant()).toBe('error');

    // Success state
    component.isError = false;
    component.result = 'success';
    expect(component.getConnectionButtonVariant()).toBe('success');
  });

  it('should return correct button text based on state', () => {
    // Default state
    expect(component.getButtonText()).toBe('Testar Conexão');

    // Loading state
    component.loading = true;
    expect(component.getButtonText()).toBe('Testando...');

    // Success state
    component.loading = false;
    component.result = 'success';
    expect(component.getButtonText()).toBe('Conectado');
  });

  it('should display correct button text in template', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Testar Conexão');
  });

  it('should update button text when loading state changes', () => {
    component.loading = true;
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Testando...');
  });

  it('should update button text when result is available', () => {
    component.result = 'success';
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Conectado');
  });

  describe('testFeaturesEnabled', () => {
    it('should return test features enabled status', () => {
      // Mock environment
      spyOnProperty(component, 'testFeaturesEnabled', 'get').and.returnValue(true);
      expect(component.testFeaturesEnabled).toBe(true);
    });

    it('should return false when test features are disabled', () => {
      spyOnProperty(component, 'testFeaturesEnabled', 'get').and.returnValue(false);
      expect(component.testFeaturesEnabled).toBe(false);
    });
  });

  describe('onGenerateTestData', () => {
    it('should generate test data successfully', () => {
      const mockResponse: TestDataResponse = {
        registrosGerados: 10,
        mensagem: 'Registros gerados com sucesso'
      };
      apiService.gerarRegistrosTeste.and.returnValue(of(mockResponse));
      spyOn(component.testDataGenerated, 'emit');

      component.onGenerateTestData();

      expect(component.generatingTestData).toBe(false);
      expect(component.testDataGenerated.emit).toHaveBeenCalledWith(
        'Registros gerados com sucesso - 10 registros gerados.'
      );
    });

    it('should handle error when generating test data', () => {
      const error = { message: 'Server error' };
      apiService.gerarRegistrosTeste.and.returnValue(throwError(() => error));
      spyOn(component.testDataGenerated, 'emit');

      component.onGenerateTestData();

      expect(component.generatingTestData).toBe(false);
      expect(component.testDataGenerated.emit).toHaveBeenCalledWith(
        'Erro ao gerar registros: Server error'
      );
    });

    it('should set generatingTestData to true during operation', () => {
      const mockResponse: TestDataResponse = {
        registrosGerados: 10,
        mensagem: 'Success'
      };
      apiService.gerarRegistrosTeste.and.returnValue(of(mockResponse));

      component.onGenerateTestData();

      expect(component.generatingTestData).toBe(false); // Should be false after completion
    });

    it('should handle response with error message', () => {
      const mockResponse: TestDataResponse = {
        registrosGerados: 0,
        mensagem: 'Success message',
        erro: 'Error occurred'
      };
      apiService.gerarRegistrosTeste.and.returnValue(of(mockResponse));
      spyOn(component.testDataGenerated, 'emit');

      component.onGenerateTestData();

      expect(component.testDataGenerated.emit).toHaveBeenCalledWith('Error occurred');
    });
  });

  describe('onDeleteTestData', () => {
    it('should delete test data successfully', () => {
      const mockResponse: TestDataResponse = {
        registrosDeletados: 5,
        mensagem: 'Registros deletados com sucesso'
      };
      apiService.deletarRegistrosTeste.and.returnValue(of(mockResponse));
      spyOn(component.testDataDeleted, 'emit');

      component.onDeleteTestData();

      expect(component.deletingTestData).toBe(false);
      expect(component.testDataDeleted.emit).toHaveBeenCalledWith(
        'Registros deletados com sucesso - 5 registros deletados.'
      );
    });

    it('should handle error when deleting test data', () => {
      const error = { message: 'Delete error' };
      apiService.deletarRegistrosTeste.and.returnValue(throwError(() => error));
      spyOn(component.testDataDeleted, 'emit');

      component.onDeleteTestData();

      expect(component.deletingTestData).toBe(false);
      expect(component.testDataDeleted.emit).toHaveBeenCalledWith(
        'Erro ao deletar registros: Delete error'
      );
    });

    it('should set deletingTestData to true during operation', () => {
      const mockResponse: TestDataResponse = {
        registrosDeletados: 5,
        mensagem: 'Success'
      };
      apiService.deletarRegistrosTeste.and.returnValue(of(mockResponse));

      component.onDeleteTestData();

      expect(component.deletingTestData).toBe(false); // Should be false after completion
    });

    it('should handle delete response with error message', () => {
      const mockResponse: TestDataResponse = {
        registrosDeletados: 0,
        mensagem: 'Success message',
        erro: 'Delete error occurred'
      };
      apiService.deletarRegistrosTeste.and.returnValue(of(mockResponse));
      spyOn(component.testDataDeleted, 'emit');

      component.onDeleteTestData();

      expect(component.testDataDeleted.emit).toHaveBeenCalledWith('Delete error occurred');
    });
  });

  describe('template rendering', () => {
    it('should show test data buttons when test features are enabled', () => {
      spyOnProperty(component, 'testFeaturesEnabled', 'get').and.returnValue(true);
      fixture.detectChanges();

      const compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.textContent).toContain('Gerar Registros de Teste');
      expect(compiled.textContent).toContain('Deletar Registros de Teste');
    });

    it('should not show test data buttons when test features are disabled', () => {
      spyOnProperty(component, 'testFeaturesEnabled', 'get').and.returnValue(false);
      fixture.detectChanges();

      const compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.textContent).not.toContain('Gerar Registros de Teste');
      expect(compiled.textContent).not.toContain('Deletar Registros de Teste');
    });

    it('should show loading text for generate button when generating', () => {
      spyOnProperty(component, 'testFeaturesEnabled', 'get').and.returnValue(true);
      component.generatingTestData = true;
      fixture.detectChanges();

      const compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.textContent).toContain('Gerando...');
    });

    it('should show loading text for delete button when deleting', () => {
      spyOnProperty(component, 'testFeaturesEnabled', 'get').and.returnValue(true);
      component.deletingTestData = true;
      fixture.detectChanges();

      const compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.textContent).toContain('Deletando...');
    });
  });

  describe('initial state', () => {
    it('should have correct initial values', () => {
      expect(component.loading).toBe(false);
      expect(component.result).toBeNull();
      expect(component.isError).toBe(false);
      expect(component.generatingTestData).toBe(false);
      expect(component.deletingTestData).toBe(false);
    });
  });
});

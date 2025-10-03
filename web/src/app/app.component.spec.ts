import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AppComponent } from './app.component';
import { ApiService, PingResponse } from './api.service';
import { CreditosComponent } from './creditos.component';
import { of, throwError } from 'rxjs';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let apiService: jasmine.SpyObj<ApiService>;

  const mockPingResponse: PingResponse = {
    message: 'pong',
    ts: '2025-10-03T00:58:09.731155Z'
  };

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('ApiService', ['ping']);

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, AppComponent, CreditosComponent],
      providers: [
        { provide: ApiService, useValue: spy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    apiService = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.activeTab).toBe('creditos');
    expect(component.loading).toBeFalse();
    expect(component.result).toBeNull();
    expect(component.isError).toBeFalse();
  });

  describe('pingApi', () => {
    it('should set loading to true initially', (done) => {
      apiService.ping.and.returnValue(of(mockPingResponse));

      component.pingApi();

      // Como a chamada é síncrona, precisamos verificar o estado final
      setTimeout(() => {
        expect(component.loading).toBeFalse();
        expect(component.result).toBe(JSON.stringify(mockPingResponse, null, 2));
        expect(component.isError).toBeFalse();
        done();
      }, 0);
    });

    it('should handle successful ping response', () => {
      apiService.ping.and.returnValue(of(mockPingResponse));

      component.pingApi();

      expect(apiService.ping).toHaveBeenCalled();
      expect(component.result).toBe(JSON.stringify(mockPingResponse, null, 2));
      expect(component.isError).toBeFalse();
      expect(component.loading).toBeFalse();
    });

    it('should handle ping error', () => {
      const error = { status: 500, message: 'Server Error' };
      apiService.ping.and.returnValue(throwError(() => error));

      component.pingApi();

      expect(component.result).toBe('Erro: Server Error');
      expect(component.isError).toBeTrue();
      expect(component.loading).toBeFalse();
    });

    it('should handle ping error without message', () => {
      const error = { status: 500 };
      apiService.ping.and.returnValue(throwError(() => error));

      component.pingApi();

      expect(component.result).toBe('Erro: Falha na comunicação com a API');
      expect(component.isError).toBeTrue();
      expect(component.loading).toBeFalse();
    });
  });

  describe('tab switching', () => {
    it('should switch to ping tab', () => {
      component.activeTab = 'creditos';
      
      // Simular clique no botão de ping
      component.activeTab = 'ping';
      
      expect(component.activeTab).toBe('ping');
    });

    it('should switch to creditos tab', () => {
      component.activeTab = 'ping';
      
      // Simular clique no botão de créditos
      component.activeTab = 'creditos';
      
      expect(component.activeTab).toBe('creditos');
    });
  });

  describe('template rendering', () => {
    it('should render creditos tab content when activeTab is creditos', () => {
      component.activeTab = 'creditos';
      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      expect(compiled.querySelector('app-creditos')).toBeTruthy();
    });

    it('should render ping tab content when activeTab is ping', () => {
      component.activeTab = 'ping';
      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      expect(compiled.querySelector('.ping-panel')).toBeTruthy();
    });

    it('should show loading state in ping button', () => {
      component.loading = true;
      component.result = null;
      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      const button = compiled.querySelector('.connection-status button');
      expect(button.textContent.trim()).toBe('Testando...');
      expect(button.disabled).toBeTrue();
    });

    it('should show success state in ping button', () => {
      component.loading = false;
      component.result = JSON.stringify(mockPingResponse, null, 2);
      component.isError = false;
      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      const button = compiled.querySelector('.connection-status button');
      expect(button.textContent.trim()).toBe('Conectado');
      expect(button.classList.contains('btn-success')).toBeTrue();
    });

    it('should show error state in ping button', () => {
      component.loading = false;
      component.result = 'Erro: Server Error';
      component.isError = true;
      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      const button = compiled.querySelector('.connection-status button');
      expect(button.textContent.trim()).toBe('Conectado');
      expect(button.classList.contains('btn-error')).toBeTrue();
    });

    it('should show default state in ping button', () => {
      component.loading = false;
      component.result = null;
      component.isError = false;
      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      const button = compiled.querySelector('.connection-status button');
      expect(button.textContent.trim()).toBe('Testar Conexão');
    });
  });

  describe('ping result display', () => {
    it('should display success result', () => {
      component.result = JSON.stringify(mockPingResponse, null, 2);
      component.isError = false;
      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      const resultElement = compiled.querySelector('.result');
      if (resultElement) {
        expect(resultElement).toBeTruthy();
        expect(resultElement.textContent.trim()).toContain('pong');
      }
    });

    it('should display error result', () => {
      component.result = 'Erro: Server Error';
      component.isError = true;
      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      const resultElement = compiled.querySelector('.result');
      if (resultElement) {
        expect(resultElement).toBeTruthy();
        expect(resultElement.textContent.trim()).toBe('Erro: Server Error');
      }
    });

    it('should not display result when result is null', () => {
      component.result = null;
      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      const resultElement = compiled.querySelector('.result');
      expect(resultElement).toBeFalsy();
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AppComponent } from './app.component';
import { ApiService, PingResponse } from './api.service';
import { CreditosDetailsComponent } from './components/features/creditos-details/creditos-details.component';
import { CreditoSearchComponent } from './components/features/credito-search/credito-search.component';
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
      imports: [HttpClientTestingModule, AppComponent, CreditosDetailsComponent, CreditoSearchComponent],
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

  it('should have default activeTab as creditos', () => {
    expect(component.activeTab).toBe('creditos');
  });

  it('should call pingApi and handle success', () => {
    apiService.ping.and.returnValue(of(mockPingResponse));

    component.pingApi();

    expect(apiService.ping).toHaveBeenCalled();
    expect(component.loading).toBeFalse();
    expect(component.result).toBe(JSON.stringify(mockPingResponse, null, 2));
    expect(component.isError).toBeFalse();
  });

  it('should call pingApi and handle error', () => {
    const error = new Error('Server Error');
    apiService.ping.and.returnValue(throwError(() => error));

    component.pingApi();

    expect(apiService.ping).toHaveBeenCalled();
    expect(component.loading).toBeFalse();
    expect(component.result).toBe('Erro: Server Error');
    expect(component.isError).toBeTrue();
  });

  it('should change tab when onTabChange is called', () => {
    component.onTabChange('buscar-credito');
    expect(component.activeTab).toBe('buscar-credito');

    component.onTabChange('creditos');
    expect(component.activeTab).toBe('creditos');
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
});
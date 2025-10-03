import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectionStatusComponent } from './connection-status.component';

describe('ConnectionStatusComponent', () => {
  let component: ConnectionStatusComponent;
  let fixture: ComponentFixture<ConnectionStatusComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConnectionStatusComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConnectionStatusComponent);
    component = fixture.componentInstance;
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
});

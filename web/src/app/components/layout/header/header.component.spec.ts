import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { HeaderComponent } from './header.component';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderComponent, HttpClientTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display default title and subtitle', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Sistema de Créditos Constituídos');
    expect(compiled.querySelector('p')?.textContent).toContain(
      'Gerenciamento e consulta de créditos por NFS-e'
    );
  });

  it('should display custom title and subtitle when provided', () => {
    component.title = 'Custom Title';
    component.subtitle = 'Custom Subtitle';
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Custom Title');
    expect(compiled.querySelector('p')?.textContent).toContain('Custom Subtitle');
  });

  describe('event handling', () => {
    it('should emit connectionTest when onConnectionTest is called', () => {
      spyOn(component.connectionTest, 'emit');

      component.onConnectionTest();

      expect(component.connectionTest.emit).toHaveBeenCalled();
    });

    it('should emit testDataGenerated with message when onTestDataGenerated is called', () => {
      const message = 'Test data generated successfully';
      spyOn(component.testDataGenerated, 'emit');

      component.onTestDataGenerated(message);

      expect(component.testDataGenerated.emit).toHaveBeenCalledWith(message);
    });

    it('should emit testDataDeleted with message when onTestDataDeleted is called', () => {
      const message = 'Test data deleted successfully';
      spyOn(component.testDataDeleted, 'emit');

      component.onTestDataDeleted(message);

      expect(component.testDataDeleted.emit).toHaveBeenCalledWith(message);
    });
  });

  describe('input properties', () => {
    it('should have correct default values', () => {
      expect(component.title).toBe('Sistema de Créditos Constituídos');
      expect(component.subtitle).toBe('Gerenciamento e consulta de créditos por NFS-e');
      expect(component.loading).toBe(false);
      expect(component.result).toBeNull();
      expect(component.isError).toBe(false);
    });

    it('should accept custom input values', () => {
      component.title = 'New Title';
      component.subtitle = 'New Subtitle';
      component.loading = true;
      component.result = 'test result';
      component.isError = true;

      expect(component.title).toBe('New Title');
      expect(component.subtitle).toBe('New Subtitle');
      expect(component.loading).toBe(true);
      expect(component.result).toBe('test result');
      expect(component.isError).toBe(true);
    });
  });

  describe('template integration', () => {
    it('should pass loading state to connection-status component', () => {
      component.loading = true;
      fixture.detectChanges();

      const connectionStatusComponent = fixture.debugElement.nativeElement.querySelector('app-connection-status');
      expect(connectionStatusComponent).toBeTruthy();
    });

    it('should pass result state to connection-status component', () => {
      component.result = 'test result';
      fixture.detectChanges();

      const connectionStatusComponent = fixture.debugElement.nativeElement.querySelector('app-connection-status');
      expect(connectionStatusComponent).toBeTruthy();
    });

    it('should pass isError state to connection-status component', () => {
      component.isError = true;
      fixture.detectChanges();

      const connectionStatusComponent = fixture.debugElement.nativeElement.querySelector('app-connection-status');
      expect(connectionStatusComponent).toBeTruthy();
    });
  });

  describe('header structure', () => {
    it('should have correct header class', () => {
      const compiled = fixture.nativeElement as HTMLElement;
      const header = compiled.querySelector('header');
      expect(header?.classList.contains('app-header')).toBe(true);
    });

    it('should have header-content div', () => {
      const compiled = fixture.nativeElement as HTMLElement;
      const headerContent = compiled.querySelector('.header-content');
      expect(headerContent).toBeTruthy();
    });

    it('should render connection-status component', () => {
      const compiled = fixture.nativeElement as HTMLElement;
      const connectionStatus = compiled.querySelector('app-connection-status');
      expect(connectionStatus).toBeTruthy();
    });
  });
});

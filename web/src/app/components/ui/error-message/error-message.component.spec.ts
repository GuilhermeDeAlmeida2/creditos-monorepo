import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ErrorMessageComponent, MessageType, MessageSize } from './error-message.component';

describe('ErrorMessageComponent', () => {
  let component: ErrorMessageComponent;
  let fixture: ComponentFixture<ErrorMessageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ErrorMessageComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ErrorMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display message content', () => {
    component.message = 'Test error message';
    fixture.detectChanges();
    
    const messageElement = fixture.nativeElement.querySelector('.message-text');
    expect(messageElement.textContent.trim()).toBe('Test error message');
  });

  it('should display title when provided', () => {
    component.title = 'Error Title';
    component.message = 'Error message';
    fixture.detectChanges();
    
    const titleElement = fixture.nativeElement.querySelector('.message-title');
    expect(titleElement.textContent.trim()).toBe('Error Title');
  });

  it('should apply correct type class', () => {
    const types: MessageType[] = ['success', 'error', 'warning', 'info'];
    
    types.forEach(type => {
      component.type = type;
      fixture.detectChanges();
      
      const containerElement = fixture.nativeElement.querySelector('[role="alert"]');
      expect(containerElement.classList.contains(`message-${type}`)).toBe(true);
    });
  });

  it('should apply correct size class', () => {
    const sizes: MessageSize[] = ['small', 'medium', 'large'];
    
    sizes.forEach(size => {
      component.size = size;
      fixture.detectChanges();
      
      const containerElement = fixture.nativeElement.querySelector('[role="alert"]');
      
      if (size === 'medium') {
        expect(containerElement.classList.contains('message-medium')).toBe(false);
      } else {
        expect(containerElement.classList.contains(`message-${size}`)).toBe(true);
      }
    });
  });

  it('should show dismiss button when dismissible is true', () => {
    component.dismissible = true;
    fixture.detectChanges();
    
    const dismissButton = fixture.nativeElement.querySelector('.message-dismiss');
    expect(dismissButton).toBeTruthy();
  });

  it('should not show dismiss button when dismissible is false', () => {
    component.dismissible = false;
    fixture.detectChanges();
    
    const dismissButton = fixture.nativeElement.querySelector('.message-dismiss');
    expect(dismissButton).toBeFalsy();
  });

  it('should hide message when dismiss is called', () => {
    component.dismissible = true;
    fixture.detectChanges();
    
    expect(component.isVisible).toBe(true);
    
    const dismissButton = fixture.nativeElement.querySelector('.message-dismiss');
    dismissButton.click();
    
    expect(component.isVisible).toBe(false);
  });

  it('should show icon when showIcon is true', () => {
    component.showIcon = true;
    component.type = 'error';
    fixture.detectChanges();
    
    const iconElement = fixture.nativeElement.querySelector('.message-icon');
    expect(iconElement).toBeTruthy();
    expect(iconElement.textContent.trim()).toBe('✕');
  });

  it('should not show icon when showIcon is false', () => {
    component.showIcon = false;
    fixture.detectChanges();
    
    const iconElement = fixture.nativeElement.querySelector('.message-icon');
    expect(iconElement).toBeFalsy();
  });

  it('should return correct icon for each type', () => {
    const iconMap = {
      success: '✓',
      error: '✕',
      warning: '⚠',
      info: 'ℹ'
    };
    
    Object.entries(iconMap).forEach(([type, expectedIcon]) => {
      component.type = type as MessageType;
      expect(component.getIconClass()).toBe(expectedIcon);
    });
  });

  it('should have correct default values', () => {
    expect(component.type).toBe('error');
    expect(component.size).toBe('medium');
    expect(component.dismissible).toBe(false);
    expect(component.showIcon).toBe(true);
    expect(component.title).toBe('');
    expect(component.message).toBe('');
    expect(component.autoHide).toBe(false);
    expect(component.autoHideDelay).toBe(5000);
    expect(component.isVisible).toBe(true);
  });

  it('should display content from ng-content', () => {
    fixture.nativeElement.innerHTML = '<app-error-message>Custom content</app-error-message>';
    fixture.detectChanges();
    
    const messageElement = fixture.nativeElement.querySelector('.message-text');
    expect(messageElement.textContent.trim()).toBe('Custom content');
  });

  it('should have correct ARIA attributes', () => {
    component.type = 'error';
    fixture.detectChanges();
    
    const alertElement = fixture.nativeElement.querySelector('[role="alert"]');
    expect(alertElement.getAttribute('aria-live')).toBe('assertive');
  });

  it('should have correct ARIA attributes for non-error types', () => {
    component.type = 'info';
    fixture.detectChanges();
    
    const alertElement = fixture.nativeElement.querySelector('[role="alert"]');
    expect(alertElement.getAttribute('aria-live')).toBe('polite');
  });

  it('should have dismiss button with correct ARIA attributes', () => {
    component.dismissible = true;
    fixture.detectChanges();
    
    const dismissButton = fixture.nativeElement.querySelector('.message-dismiss');
    expect(dismissButton.getAttribute('aria-label')).toBe('Fechar mensagem');
    expect(dismissButton.getAttribute('title')).toBe('Fechar');
  });
});

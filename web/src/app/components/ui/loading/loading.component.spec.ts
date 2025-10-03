import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoadingComponent, LoadingType, LoadingSize, LoadingColor } from './loading.component';

describe('LoadingComponent', () => {
  let component: LoadingComponent;
  let fixture: ComponentFixture<LoadingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoadingComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LoadingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display spinner by default', () => {
    const spinnerElement = fixture.nativeElement.querySelector('.spinner');
    expect(spinnerElement).toBeTruthy();
  });

  it('should display dots when type is dots', () => {
    component.type = 'dots';
    fixture.detectChanges();
    
    const dotsElement = fixture.nativeElement.querySelector('.dots');
    expect(dotsElement).toBeTruthy();
    
    const dotElements = fixture.nativeElement.querySelectorAll('.dot');
    expect(dotElements.length).toBe(3);
  });

  it('should display pulse when type is pulse', () => {
    component.type = 'pulse';
    fixture.detectChanges();
    
    const pulseElement = fixture.nativeElement.querySelector('.pulse');
    expect(pulseElement).toBeTruthy();
  });

  it('should display skeleton when type is skeleton', () => {
    component.type = 'skeleton';
    fixture.detectChanges();
    
    const skeletonElement = fixture.nativeElement.querySelector('.skeleton');
    expect(skeletonElement).toBeTruthy();
    
    const skeletonLines = fixture.nativeElement.querySelectorAll('.skeleton-line');
    expect(skeletonLines.length).toBe(3);
  });

  it('should display text when provided', () => {
    component.text = 'Carregando dados...';
    fixture.detectChanges();
    
    const textElement = fixture.nativeElement.querySelector('.loading-text');
    expect(textElement).toBeTruthy();
    expect(textElement.textContent.trim()).toBe('Carregando dados...');
  });

  it('should apply correct size classes', () => {
    const sizes: LoadingSize[] = ['small', 'medium', 'large'];
    
    sizes.forEach(size => {
      component.size = size;
      fixture.detectChanges();
      
      const containerElement = fixture.nativeElement.querySelector('div[aria-label]');
      expect(containerElement.classList.contains(`loading-${size}`)).toBe(true);
    });
  });

  it('should apply correct color classes', () => {
    const colors: LoadingColor[] = ['primary', 'secondary', 'success', 'warning', 'danger', 'info'];
    
    colors.forEach(color => {
      component.color = color;
      fixture.detectChanges();
      
      const containerElement = fixture.nativeElement.querySelector('div[aria-label]');
      expect(containerElement.classList.contains(`loading-${color}`)).toBe(true);
    });
  });

  it('should apply overlay class when overlay is true', () => {
    component.overlay = true;
    fixture.detectChanges();
    
    const containerElement = fixture.nativeElement.querySelector('div[aria-label]');
    expect(containerElement.classList.contains('loading-overlay')).toBe(true);
  });

  it('should apply fullscreen class when fullScreen is true', () => {
    component.fullScreen = true;
    fixture.detectChanges();
    
    const containerElement = fixture.nativeElement.querySelector('div[aria-label]');
    expect(containerElement.classList.contains('loading-fullscreen')).toBe(true);
  });

  it('should have correct default values', () => {
    expect(component.type).toBe('spinner');
    expect(component.size).toBe('medium');
    expect(component.color).toBe('primary');
    expect(component.text).toBe('');
    expect(component.overlay).toBe(false);
    expect(component.fullScreen).toBe(false);
  });

  it('should have correct ARIA label', () => {
    component.text = 'Carregando';
    fixture.detectChanges();
    
    const containerElement = fixture.nativeElement.querySelector('div[aria-label]');
    expect(containerElement.getAttribute('aria-label')).toBe('Carregando');
  });

  it('should have default ARIA label when no text provided', () => {
    fixture.detectChanges();
    
    const containerElement = fixture.nativeElement.querySelector('div[aria-label]');
    expect(containerElement.getAttribute('aria-label')).toBe('Carregando');
  });

  it('should combine multiple classes correctly', () => {
    component.type = 'dots';
    component.size = 'large';
    component.color = 'success';
    component.overlay = true;
    fixture.detectChanges();
    
    const containerElement = fixture.nativeElement.querySelector('div[aria-label]');
    expect(containerElement.classList.contains('loading-dots')).toBe(true);
    expect(containerElement.classList.contains('loading-large')).toBe(true);
    expect(containerElement.classList.contains('loading-success')).toBe(true);
    expect(containerElement.classList.contains('loading-overlay')).toBe(true);
  });
});

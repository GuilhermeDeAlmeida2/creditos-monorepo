import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BadgeComponent, BadgeVariant, BadgeSize } from './badge.component';

describe('BadgeComponent', () => {
  let component: BadgeComponent;
  let fixture: ComponentFixture<BadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BadgeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should apply correct variant class', () => {
    const variants: BadgeVariant[] = ['success', 'danger', 'warning', 'info', 'primary', 'secondary', 'light', 'dark'];
    
    variants.forEach(variant => {
      component.variant = variant;
      fixture.detectChanges();
      
      const badgeElement = fixture.nativeElement.querySelector('span');
      expect(badgeElement.classList.contains(`badge-${variant}`)).toBe(true);
    });
  });

  it('should apply correct size class', () => {
    const sizes: BadgeSize[] = ['small', 'medium', 'large'];
    
    sizes.forEach(size => {
      component.size = size;
      fixture.detectChanges();
      
      const badgeElement = fixture.nativeElement.querySelector('span');
      
      if (size === 'medium') {
        expect(badgeElement.classList.contains('badge-medium')).toBe(false);
      } else {
        expect(badgeElement.classList.contains(`badge-${size}`)).toBe(true);
      }
    });
  });

  it('should apply pill class when pill is true', () => {
    component.pill = true;
    fixture.detectChanges();
    
    const badgeElement = fixture.nativeElement.querySelector('span');
    expect(badgeElement.classList.contains('badge-pill')).toBe(true);
  });

  it('should apply outline class when outline is true', () => {
    component.outline = true;
    fixture.detectChanges();
    
    const badgeElement = fixture.nativeElement.querySelector('span');
    expect(badgeElement.classList.contains('badge-outline')).toBe(true);
  });

  it('should apply clickable class when clickable is true', () => {
    component.clickable = true;
    fixture.detectChanges();
    
    const badgeElement = fixture.nativeElement.querySelector('span');
    expect(badgeElement.classList.contains('badge-clickable')).toBe(true);
  });

  it('should set tooltip attribute when tooltip is provided', () => {
    const tooltipText = 'This is a tooltip';
    component.tooltip = tooltipText;
    fixture.detectChanges();
    
    const badgeElement = fixture.nativeElement.querySelector('span');
    expect(badgeElement.getAttribute('title')).toBe(tooltipText);
  });

  it('should display content correctly', () => {
    const testContent = 'Test Badge';
    fixture.nativeElement.innerHTML = `<app-badge>${testContent}</app-badge>`;
    fixture.detectChanges();
    
    const badgeElement = fixture.nativeElement.querySelector('span');
    expect(badgeElement.textContent.trim()).toBe(testContent);
  });

  it('should have correct default values', () => {
    expect(component.variant).toBe('primary');
    expect(component.size).toBe('medium');
    expect(component.pill).toBe(false);
    expect(component.outline).toBe(false);
    expect(component.clickable).toBe(false);
    expect(component.tooltip).toBe('');
  });

  it('should combine multiple classes correctly', () => {
    component.variant = 'success';
    component.size = 'large';
    component.pill = true;
    component.outline = true;
    component.clickable = true;
    fixture.detectChanges();
    
    const badgeElement = fixture.nativeElement.querySelector('span');
    expect(badgeElement.classList.contains('badge-success')).toBe(true);
    expect(badgeElement.classList.contains('badge-large')).toBe(true);
    expect(badgeElement.classList.contains('badge-pill')).toBe(true);
    expect(badgeElement.classList.contains('badge-outline')).toBe(true);
    expect(badgeElement.classList.contains('badge-clickable')).toBe(true);
  });

  it('should render with success variant and content', () => {
    component.variant = 'success';
    fixture.nativeElement.innerHTML = '<app-badge>Sim</app-badge>';
    fixture.detectChanges();
    
    const badgeElement = fixture.nativeElement.querySelector('span');
    expect(badgeElement.textContent.trim()).toBe('Sim');
    expect(badgeElement.classList.contains('badge-success')).toBe(true);
  });

  it('should render with danger variant and content', () => {
    component.variant = 'danger';
    fixture.nativeElement.innerHTML = '<app-badge>Não</app-badge>';
    fixture.detectChanges();
    
    const badgeElement = fixture.nativeElement.querySelector('span');
    expect(badgeElement.textContent.trim()).toBe('Não');
    expect(badgeElement.classList.contains('badge-danger')).toBe(true);
  });
});
